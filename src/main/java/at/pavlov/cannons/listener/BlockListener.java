package at.pavlov.cannons.listener;


import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;

public class BlockListener implements Listener
{
	private final Cannons plugin;

	public BlockListener(Cannons plugin)
	{
		this.plugin = plugin;
	}


    @EventHandler
    public void blockExplodeEvent(BlockExplodeEvent event) {
        if (plugin.getMyConfig().isRelayExplosionEvent()) {
            EntityExplodeEvent explodeEvent = new EntityExplodeEvent(null, event.getBlock().getLocation(), event.blockList(), event.getYield());
            Bukkit.getServer().getPluginManager().callEvent(explodeEvent);
            event.setCancelled(explodeEvent.isCancelled());
        }
    }

    /**
     * Water will not destroy button and torches
     * @param event
     */
    @EventHandler
    public void BlockFromTo(BlockFromToEvent event)
    {
        Block block = event.getToBlock();
        Cannon cannon = plugin.getCannonManager().getCannon(block.getLocation(), null);
        if (cannon !=  null)//block.getType() == Material.STONE_BUTTON || block.getType() == Material.WOOD_BUTTON || block.getType() == Material.   || block.getType() == Material.TORCH)
        {
            if (cannon.isCannonBlock(block))
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * prevent fire on cannons
     * @param event
     */
    @EventHandler
    public void BlockSpread(BlockSpreadEvent  event)
    {
        Block block = event.getBlock().getRelative(BlockFace.DOWN);
        Cannon cannon = plugin.getCannonManager().getCannon(block.getLocation(), null);

        if (cannon !=  null)
        {
            if (cannon.isCannonBlock(block))
            {
                event.setCancelled(true);
            }
        }
    }


    /**
     * retraction pistons will trigger this event. If the pulled block is part of a cannon, it is canceled
     * @param event - BlockPistonRetractEvent
     */
    @EventHandler
    public void BlockPistonRetract(BlockPistonRetractEvent event)
    {
        // when piston is sticky and has a cannon block attached delete the
        // cannon
        if (event.isSticky())
        {
            Location loc = event.getBlock().getRelative(event.getDirection(), 2).getLocation();
            Cannon cannon = plugin.getCannonManager().getCannon(loc, null);
            if (cannon != null)
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * pushing pistons will trigger this event. If the pused block is part of a cannon, it is canceled
     * @param event - BlockPistonExtendEvent
     */
    @EventHandler
    public void BlockPistonExtend(BlockPistonExtendEvent event)
    {
        // when the moved block is a cannonblock
        for (Iterator<Block> iter = event.getBlocks().iterator(); iter.hasNext();)
        {
            // if moved block is cannonBlock delete cannon
            Cannon cannon = plugin.getCannonManager().getCannon(iter.next().getLocation(), null);
            if (cannon != null)
            {
                event.setCancelled(true);
            }
        }
    }

    /**
     * if the block catches fire this event is triggered. Cannons can't burn.
     * @param event - BlockBurnEvent
     */
    @EventHandler
    public void BlockBurn(BlockBurnEvent event)
    {
        // the cannon will not burn down
        if (plugin.getCannonManager().getCannon(event.getBlock().getLocation(), null) != null)
        {
            event.setCancelled(true);
        }
    }

    /**
     * if one block of the cannon is destroyed, it is removed from the list of cannons
     * @param event - BlockBreakEvent
     */
    @EventHandler
    public void BlockBreak(BlockBreakEvent event)
    {

        Cannon cannon = plugin.getCannonManager().getCannon(event.getBlock().getLocation(), null);
        if (cannon != null)
        {
            //breaking is only allowed when the barrel is broken - minor stuff as buttons are canceled
            //you can't break your own cannon in aiming mode
            //breaking cannon while player is in selection (command) mode is not allowed
            Cannon aimingCannon = null;
            if (event.getPlayer()!=null)
                 aimingCannon = plugin.getAiming().getCannonInAimingMode(event.getPlayer());

            if (cannon.isDestructibleBlock(event.getBlock().getLocation()) && (aimingCannon==null||!cannon.equals(aimingCannon)) && !plugin.getCommandListener().isSelectingMode(event.getPlayer())) {
                plugin.getCannonManager().removeCannon(cannon, false, true, BreakCause.PlayerBreak);
                plugin.logDebug("cannon broken:  " + cannon.isDestructibleBlock(event.getBlock().getLocation()));
            }
            else {
                event.setCancelled(true);
                plugin.logDebug("cancelled cannon destruction: " + cannon.isDestructibleBlock(event.getBlock().getLocation()));
            }
        }
    }
}
