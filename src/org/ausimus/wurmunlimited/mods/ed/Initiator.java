package org.ausimus.wurmunlimited.mods.ed;

import org.ausimus.wurmunlimited.mods.ed.actions.ActionsInit;
import org.ausimus.wurmunlimited.mods.ed.items.ItemTemplateReflections;
import org.ausimus.wurmunlimited.mods.ed.items.RemoveIndestructableRestriction;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.Properties;

public class Initiator implements WurmServerMod, PreInitable, ItemTemplatesCreatedListener, Configurable, Initable, ServerStartedListener
{
    public static boolean paintAll;
    public static boolean paintIndestructible;

    @Override
    public void onServerStarted()
    {
        new ActionsInit();
    }

    @Override
    public void preInit()
    {
        ModActions.init();
        if (paintIndestructible)
            new RemoveIndestructableRestriction();
    }

    @Override
    public void init()
    {
    }

    @Override
    public void onItemTemplatesCreated()
    {
        if (paintAll)
            new ItemTemplateReflections().paintAll();
    }

    @Override
    public void configure(Properties properties)
    {
        paintAll = Boolean.parseBoolean(properties.getProperty("paintAll", Boolean.toString(paintAll)));
        paintIndestructible = Boolean.parseBoolean(properties.getProperty("paintIndestructible", Boolean.toString(paintIndestructible)));
    }
}