package org.ausimus.wurmunlimited.mods.ed.actions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTypes;
import java.util.Collections;
import java.util.List;

import com.wurmonline.shared.constants.ProtoConstants;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class CreateDye implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer, ProtoConstants
{
    private short actionID;
    private ActionEntry actionEntry;

    CreateDye()
    {
        actionID = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionID, "Create Dye", "creating", new int[0]);
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
        if (target.getTemplateId() == ItemList.water)
            return Collections.singletonList(actionEntry);
        else
            return null;
    }


    public List<ActionEntry> getBehavioursFor(Creature performer, Item target)
    {
        return getBehavioursFor(performer, null, target);
    }

    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter)
    {
        return action(action, performer, target, num, counter);
    }

    public boolean action(Action action, Creature performer, Item target, short num, float counter)
    {
        // Pre-conditions
        // Only do this to water
        if (target.getTemplateId() != ItemList.water)
        {
            performer.getCommunicator().sendNormalServerMessage(
                    "The " + target.getName() + " is not water.", M_FAIL);
            return true;
        }
        // Water must be in players inventory.
        else if (target.getTopParent() != performer.getInventory().getWurmId())
        {
            performer.getCommunicator().sendNormalServerMessage(
                    "The " + target.getName() + " must be in your inventory in order to do that.", M_FAIL);
            return true;
        }
        // End Pre-conditions

        // No need to destroy and recreate, just convert (retain sizes, volume and shit).
        target.setTemplateId(ItemList.dye);
        target.setDescription("");
        target.setName("dye");
        return true;
    }
}