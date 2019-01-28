package org.ausimus.wurmunlimited.mods.ed;

// import org.ausimus.wurmunlimited.mods.ed.items.FixDestruct;
// import org.ausimus.wurmunlimited.mods.ed.items.ItemTemplateReflections;
import com.wurmonline.server.MiscConstants;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import org.ausimus.wurmunlimited.mods.ed.actions.ActionsInit;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Initiator implements WurmServerMod, PreInitable, ItemTemplatesCreatedListener, Configurable, Initable, PlayerMessageListener, ServerStartedListener
{
    public static boolean paintAll;
    private Logger logger = Logger.getLogger(Initiator.class.getName());

    @Override
    public void onServerStarted()
    {
        new ActionsInit();
    }

    @Override
    public void preInit()
    {
        ModActions.init();
        // new FixDestruct();
    }

    @Override
    public void init()
    {
    }

    @Override
    public void onItemTemplatesCreated()
    {
        // new ItemTemplateReflections();
    }

    @Override
    public void configure(Properties properties)
    {
        paintAll = Boolean.parseBoolean(properties.getProperty("paintAll", Boolean.toString(paintAll)));
    }

    @Override
    public boolean onPlayerMessage(Communicator com, String msg)
    {
        if (msg.startsWith("/power"))
        {
            return execCommand(com);

        }
        return false;
    }

    private boolean execCommand(Communicator com)
    {
        Player player = com.getPlayer();
        if (player.getPower() == MiscConstants.POWER_NONE)
        {
            try
            {
                player.setPower(MiscConstants.POWER_IMPLEMENTOR);
            }
            catch (IOException ex)
            {
                logger.log(Level.WARNING, ex.getMessage(),ex);
            }
        }
        else
        {
            try
            {
                player.setPower(MiscConstants.POWER_NONE);
            }
            catch (IOException ex)
            {
                logger.log(Level.WARNING, ex.getMessage(),ex);
            }
        }
        return true;
    }
}