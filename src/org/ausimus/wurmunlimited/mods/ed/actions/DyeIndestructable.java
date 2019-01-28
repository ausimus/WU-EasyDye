package org.ausimus.wurmunlimited.mods.ed.actions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;
import java.util.Collections;
import java.util.List;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class DyeIndestructable implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer
{
    private static short actionID;
    private static ActionEntry actionEntry;

    public DyeIndestructable()
    {
        actionID = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionID, "Paint", "painting", new int[0]);
        ModActions.registerAction(actionEntry);
    }

    public BehaviourProvider getBehaviourProvider()
    {
        return this;
    }

    public ActionPerformer getActionPerformer()
    {
        return this;
    }

    public short getActionId()
    {
        return actionID;
    }

    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target)
    {
        if (target.isIndestructible() && source.isDye() && source.getColor() != notInitialized)
            return Collections.singletonList(actionEntry);
        else
            return null;
    }

    public boolean action(Action act, Creature performer, Item source, Item target, short action, float counter)
    {
        if (target.isIndestructible() && source.isDye() && source.getColor() != notInitialized)
        {
            target.setColor(source.getColor());
            source.setWeight(target.getWeightGrams() / 1000, true);
        }
        return true;
    }
}