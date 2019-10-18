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

public class RemoveIndestructableRestriction
{
    private static Logger logger = Logger.getLogger(RemoveIndestructableRestriction.class.getName());

    public RemoveIndestructableRestriction()
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
                    parameters, () -> (object, method, args) -> invokeColor(args));
            HookManager.getInstance().registerHook(
                    "com.wurmonline.server.behaviours.MethodsItems", "removeColor",
                    parameters, () -> (object, method, args) -> invokeColorRemove(args));

        }
        catch (NotFoundException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private Object invokeColorRemove(Object[] args)
    {
        Creature performer = (Creature) args[0];
        Item brush = (Item) args[1];
        Item target = (Item) args[2];
        Action act = (Action) args[3];
        boolean primary = (boolean) args[4];

        boolean done;
        if (target.isDragonArmour() && primary)
        {
            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " is too tough for the " + brush.getName() + " and the colour refuses to disappear.");
            return true;
        }
        else
        {
            boolean insta = performer.getPower() >= 5;
            int colourNeeded = 0;
            String sItem = "";
            if (target.getTemplateId() != 1396)
            {
                if (primary && brush.getTemplateId() != 441)
                {
                    performer.getCommunicator().sendNormalServerMessage("You cannot use the " + brush.getName() + " to do this.");
                    return true;
                }

                if (!primary)
                {
                    sItem = target.getSecondryItemName();
                }
            }
            else
            {
                if (primary && brush.getTemplateId() != 441 || !primary && brush.getTemplateId() != 73)
                {
                    performer.getCommunicator().sendNormalServerMessage("You cannot use the " + brush.getName() + " to do this.");
                    return true;
                }

                if (primary)
                {
                    sItem = "barrel";
                }
                else
                {
                    sItem = "lamp";
                }
            }

            if (!insta && target.color == -1 && primary)
            {
                performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has no colour.");
                return true;
            }
            else
            {
                int dyeOverride;
                if (!primary)
                {
                    if (brush.getTemplateId() == 441)
                    {
                        if (!insta && target.color2 == -1)
                        {
                            performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " has no colour.");
                            return true;
                        }

                        if (target.isColorable() && target.getTemplateId() != 1396)
                        {
                            performer.getCommunicator().sendNormalServerMessage("You cannot use the " + brush.getName() + " to do this.");
                            return true;
                        }
                    }
                    else
                    {
                        if (brush.getTemplateId() != 73)
                        {
                            performer.getCommunicator().sendNormalServerMessage("You cannot use the " + brush.getName() + " to do this.");
                            return true;
                        }

                        dyeOverride = target.getTemplate().getDyePrimaryAmountGrams();
                        if (target.getTemplate().getDyeSecondaryAmountGrams() > 0)
                        {
                            dyeOverride = target.getTemplate().getDyeSecondaryAmountGrams();
                        }
                        else if (dyeOverride > 0)
                        {
                            dyeOverride = (int) ((float) dyeOverride * 0.3F);
                        }

                        if (dyeOverride > 0)
                        {
                            colourNeeded = dyeOverride;
                        }
                        else
                        {
                            colourNeeded = (int) Math.max(1.0D, (double) target.getSurfaceArea() * 0.3D / 25.0D);
                        }

                        colourNeeded = Math.max(1, colourNeeded / 2);
                        if (!insta && colourNeeded > brush.getWeightGrams())
                        {
                            performer.getCommunicator().sendNormalServerMessage("You need more lye (" + colourNeeded + "g) to bleach that item.");
                            return true;
                        }
                    }
                }

                done = false;
                if (act.currentSecond() == 1)
                {
                    String type;
                    if (brush.getTemplateId() == 441)
                    {
                        type = "brush";
                    }
                    else
                    {
                        type = "bleach";
                    }

                    act.setTimeLeft(150);
                    performer.getCommunicator().sendNormalServerMessage("You start to " + type + " the " + target.getName() + ".");
                    Server.getInstance().broadCastAction(performer.getName() + " starts to " + type + " " + target.getNameWithGenus() + ".", performer, 5);
                    String verb;
                    if (brush.getTemplateId() == 441 && target.getTemplateId() != 1396)
                    {
                        verb = Actions.actionEntrys[232].getVerbString();
                    }
                    else
                    {
                        verb = Actions.actionEntrys[924].getVerbString();
                    }

                    performer.sendActionControl(verb, true, act.getTimeLeft());
                }
                else
                {
                    dyeOverride = act.getTimeLeft();
                    if (insta || act.getCounterAsFloat() * 10.0F >= (float) dyeOverride)
                    {
                        done = true;
                        if (primary)
                        {
                            target.setColor(-1);
                            if (brush.getTemplateId() == 441)
                            {
                                brush.setDamage((float) ((double) brush.getDamage() + 0.5D * (double) brush.getDamageModifier()));
                            }
                            else
                            {
                                brush.setWeight(brush.getWeightGrams() - colourNeeded, true);
                            }

                            if (sItem.length() == 0)
                            {
                                performer.getCommunicator().sendNormalServerMessage("You remove the colour from the " + target.getName() + ".");
                            }
                            else
                            {
                                performer.getCommunicator().sendNormalServerMessage("You remove the colour from the " + target.getName() + "'s " + sItem + ".");
                            }
                        }
                        else
                        {
                            target.setColor2(-1);
                            if (brush.getTemplateId() == 441)
                            {
                                brush.setDamage((float) ((double) brush.getDamage() + 0.5D * (double) brush.getDamageModifier()));
                            }
                            else
                            {
                                brush.setWeight(brush.getWeightGrams() - colourNeeded, true);
                            }

                            if (sItem.length() == 0)
                            {
                                performer.getCommunicator().sendNormalServerMessage("You remove the colour from the " + target.getName() + ".");
                            }
                            else
                            {
                                performer.getCommunicator().sendNormalServerMessage("You remove the colour from the " + target.getName() + "'s " + sItem + ".");
                            }
                        }
                    }
                }

                return done;
            }
        }
    }

    private Object invokeColor(Object[] args)
    {
        Creature performer = (Creature) args[0];
        Item colour = (Item) args[1];
        Item target = (Item) args[2];
        Action act = (Action) args[3];
        boolean primary = (boolean) args[4];

        boolean done = true;
        String sItem = "";
        if (primary)
        {
            if (target.getTemplateId() == 1396)
            {
                sItem = "barrel";
            }

            if (target.color != -1)
            {
                if (sItem.length() == 0)
                {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " already has colour on it.");
                }
                else
                {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " already has colour on it's " + sItem + ". Remove it first with a metal brush.");
                }

                return true;
            }
        }

        if (!primary)
        {
            if (target.getTemplateId() == 1396)
            {
                sItem = "lamp";
            }
            else
            {
                sItem = target.getSecondryItemName();
            }

            if (target.color2 != -1)
            {
                if (sItem.length() == 0)
                {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " already has colour on it.");
                }
                else
                {
                    performer.getCommunicator().sendNormalServerMessage("The " + target.getName() + " already has colour on it's " + sItem + ". Remove it first with some lye.");
                }

                return true;
            }
        }

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
                if ((primary || target.isDragonArmour()) && target.getTemplateId() != 1396)
                {
                    performer.getCommunicator().sendNormalServerMessage("You start to " + type + " the " + target.getName() + " (using " + colourNeeded + "g of " + type + ").");
                }
                else
                {
                    performer.getCommunicator().sendNormalServerMessage("You start to " + type + " the " + target.getName() + "'s " + sItem + " (using " + colourNeeded + "g of " + type + ").");
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
                        if (target.getTemplateId() == 1396)
                        {
                            performer.getCommunicator().sendNormalServerMessage("You paint the " + target.getName() + "'s " + sItem + ".");
                        }
                        else
                        {
                            performer.getCommunicator().sendNormalServerMessage("You dye the " + target.getName() + "'s " + sItem + ".");
                        }
                    }
                    else if (target.getTemplateId() == 1396)
                    {
                        performer.getCommunicator().sendNormalServerMessage("You paint the " + target.getName() + "'s " + sItem + ".");
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