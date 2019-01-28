package com.wurmonline.server.spells;

import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.shared.constants.ProtoConstants;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

class Dye extends ReligiousSpell
{
    Dye()
    {
        super("Dye", ModActions.getNextActionId(),
                10, 10, 0, 30, 0L);
        this.targetItem = true;
        this.description = "creates dye";
        ActionEntry actionEntry = ActionEntry.createEntry((short) number, name, "enchanting",
                new int[]{2, 36, 48});
        ModActions.registerAction(actionEntry);
    }

    boolean precondition(Skill castSkill, Creature performer, Item target)
    {
        if (target.getTemplateId() != ItemList.water)
        {
            performer.getCommunicator().sendNormalServerMessage("This spell can only be cast on water.", (byte) 3);
            return false;
        }
        return true;
    }

    void doEffect(Skill castSkill, double power, Creature performer, Item target)
    {
        target.setTemplateId(ItemList.dye);
        target.setQualityLevel(100.0F);
        performer.getCommunicator().sendNormalServerMessage(
                "You create some dye Right click it to change its color.");
    }
}