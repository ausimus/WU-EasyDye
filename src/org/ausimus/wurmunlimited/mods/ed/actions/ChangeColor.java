package org.ausimus.wurmunlimited.mods.ed.actions;

import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.questions.ColorQuestion;
import java.util.Collections;
import java.util.List;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class ChangeColor implements WurmServerMod, ItemTypes, MiscConstants, ModAction, BehaviourProvider, ActionPerformer
{
    private static short actionID;
    private static ActionEntry actionEntry;

    ChangeColor()
    {
        actionID = (short) ModActions.getNextActionId();
        actionEntry = ActionEntry.createEntry(actionID, "Change Color", "", new int[0]);
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
        if (target.getTemplateId() == ItemList.dye)
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
        if (target.getTemplateId() == ItemList.dye)
        {
            ColorQuestion q = new ColorQuestion(
                    performer, "Colorizer", "Choose your color.", target.getWurmId());
            q.sendQuestion();
        }
        return true;
    }
}