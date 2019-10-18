package org.ausimus.wurmunlimited.mods.ed.items;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ItemTemplateReflections
{
    private static Logger logger = Logger.getLogger(ItemTemplateReflections.class.getName());
    public ItemTemplateReflections()
    {
    }
    public void paintAll()
    {
        try
        {
            // Make everything colorable.
            ItemTemplate[] itemTemplates = ItemTemplateFactory.getInstance().getTemplates();
            Field colorable = ReflectionUtil.getField(Class.forName("com.wurmonline.server.items.ItemTemplate"), "colorable");
            int i;
            for (i = 0; i < itemTemplates.length; i++)
                ReflectionUtil.setPrivateField(itemTemplates[i], colorable, true);
        }
        catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException ex)
        {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}