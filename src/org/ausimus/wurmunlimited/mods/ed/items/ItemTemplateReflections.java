package org.ausimus.wurmunlimited.mods.ed.items;

import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ausimus.wurmunlimited.mods.ed.Initiator;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;

public class ItemTemplateReflections
{
    private static ArrayList<String> items = new ArrayList<>();

    /**
     * Basicly sets a specified item with a given templateID to colorable.
     */
    public ItemTemplateReflections()
    {
        try
        {
            // Lets grab the array file that stores the items we wish to be colorable.
            BufferedReader br = new BufferedReader(new FileReader("mods/EasyDyeItems.txt"));
            String cl;
            while ((cl = br.readLine()) != null)
            {
                // Actually add from file to the above ArrayList. ItemID are integer each on new line.
                items.add(cl);
            }

            // Lets call all templates as an array, so we can do da loops.
            ItemTemplate[] itemTemplates = ItemTemplateFactory.getInstance().getTemplates();
            // Get the template field we want to edit.
            Field fieldPaint = ReflectionUtil.getField(Class.forName(
                    /*Class*/"com.wurmonline.server.items.ItemTemplate"), /*Field*/"colorable");

            // Loops, the changes happen here.
            for (int x = 0; x <= itemTemplates.length; ++x)
                // If paintAll is true every item in the game minus items that are indestructible can be painted.
                // Indestructible items are handled with a custom action.
                if (Initiator.paintAll && !itemTemplates[x].isIndestructible())
                    if (itemTemplates[x].getTemplateId() >= 0)
                        ReflectionUtil.setPrivateField(itemTemplates[x], fieldPaint, true);
                    else
                    {
                        // Below loop calls items ArrayList, so that custom items can be specified instead of all.
                        Object[] theArray = items.toArray();
                        for (int y = 0; y <= theArray.length; ++y)
                            if (itemTemplates[x].getTemplateId() == Integer.parseInt(items.get(y)) && !itemTemplates[x].isIndestructible())
                                ReflectionUtil.setPrivateField(itemTemplates[x], fieldPaint, true);
                    }
        }
        catch (IOException | NoSuchFieldException | IllegalAccessException | ClassNotFoundException ex)
        {
            Logger logger = Logger.getLogger(ItemTemplateReflections.class.getName());
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}