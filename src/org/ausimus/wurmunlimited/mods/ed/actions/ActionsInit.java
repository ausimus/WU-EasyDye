package org.ausimus.wurmunlimited.mods.ed.actions;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class ActionsInit
{
    public ActionsInit()
    {
        ModActions.registerAction(new ChangeColor());
        ModActions.registerAction(new DyeIndestructable());
    }
}