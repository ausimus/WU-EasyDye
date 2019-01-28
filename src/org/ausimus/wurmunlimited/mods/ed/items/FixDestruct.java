package org.ausimus.wurmunlimited.mods.ed.items;

import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.Actions;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import javassist.*;
import javassist.bytecode.Descriptor;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FixDestruct
{
    private static Logger logger = Logger.getLogger(FixDestruct.class.getName());

    public FixDestruct()
    {
        try
        {
            String parameters;
            parameters = Descriptor.ofMethod(
                    // Method Type
                    CtClass.booleanType,
                    // Array of Parameters
                    new CtClass[]{
                            HookManager.getInstance().getClassPool().get("com.wurmonline.server.creatures.Creature"),
                            HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
                            HookManager.getInstance().getClassPool().get("com.wurmonline.server.items.Item"),
                            HookManager.getInstance().getClassPool().get("com.wurmonline.server.behaviours.Action"),
                            CtPrimitiveType.booleanType
                    }
            );
            HookManager.getInstance().registerHook(
                    "com.wurmonline.server.behaviours.MethodsItems", "colorItem",
                    parameters, () -> (object, method, args) -> invoke(args));

        }
        catch (NotFoundException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private Object invoke(Object[] args)
    {
        Creature performer = (Creature) args[0];
        Item colour = (Item) args[1];
        Item target = (Item) args[2];
        Action act = (Action) args[3];
        boolean primary = (boolean) args[4];

        boolean done = true;
        int dyeOverride = target.getTemplate().getDyePrimaryAmountGrams();
        if (!primary && !target.isDragonArmour())
        {
            if (target.getTemplate().getDyeSecondaryAmountGrams() > 0)
            {
                dyeOverride = target.getTemplate().getDyeSecondaryAmountGrams();
            }
            else if (dyeOverride > 0)
            {
                dyeOverride = (int) ((float) dyeOverride * 0.3F);
            }
        }

        int colourNeeded;
        if (dyeOverride > 0)
        {
            colourNeeded = dyeOverride;
        }
        else
        {
            colourNeeded = (int) Math.max(1.0D, (double) target.getSurfaceArea() * (primary ? 1.0D : 0.3D) / 25.0D);
        }

        boolean insta = performer.getPower() >= 5;
        String type;
        if (!primary && !target.isDragonArmour())
        {
            type = "dye";
        }
        else
        {
            type = "paint";
        }

        if (!insta && colourNeeded > colour.getWeightGrams())
        {
            performer.getCommunicator().sendNormalServerMessage("You need more " + type + " to colour that item - at least " + colourNeeded + "g of " + type + ".");
        }
        else
        {
            done = false;
            if (act.currentSecond() == 1)
            {
                act.setTimeLeft(Math.max(50, colourNeeded / 50));
                if (!primary && !target.isDragonArmour())
                {
                    performer.getCommunicator().sendNormalServerMessage("You start to " + type + " the " + target.getName() + "'s " + target.getSecondryItemName() + " (using " + colourNeeded + "g of " + type + ").");
                }
                else
                {
                    performer.getCommunicator().sendNormalServerMessage("You start to " + type + " the " + target.getName() + " (using " + colourNeeded + "g of " + type + ").");
                }

                Server.getInstance().broadCastAction(performer.getName() + " starts to " + type + " " + target.getNameWithGenus() + ".", performer, 5);
                String verb;
                if (primary)
                {
                    verb = Actions.actionEntrys[231].getVerbString();
                }
                else
                {
                    verb = Actions.actionEntrys[923].getVerbString();
                }

                performer.sendActionControl(verb, true, act.getTimeLeft());
            }
            else
            {
                int timeleft = act.getTimeLeft();
                if (insta || act.getCounterAsFloat() * 10.0F >= (float) timeleft)
                {
                    done = true;
                    if (primary)
                    {
                        target.setColor(colour.getColor());
                    }
                    else
                    {
                        target.setColor2(colour.getColor());
                    }

                    colour.setWeight(colour.getWeightGrams() - colourNeeded, true);
                    if (!primary && !target.isDragonArmour())
                    {
                        performer.getCommunicator().sendNormalServerMessage("You dye the " + target.getName() + "'s " + target.getSecondryItemName() + ".");
                    }
                    else
                    {
                        performer.getCommunicator().sendNormalServerMessage("You paint the " + target.getName() + ".");
                    }

                    if (target.isBoat() && !primary)
                    {
                        performer.achievement(494);
                    }

                    if (target.isArmour())
                    {
                        performer.achievement(493);
                    }
                }
            }
        }
        return done;
    }
}
