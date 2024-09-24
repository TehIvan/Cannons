package at.pavlov.cannons.earthvision.events;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class PlayerListener implements Listener {

    public HashMap<UUID, HitBox> crafts = new HashMap<>();


    @EventHandler
    public void onCraftPilot(CraftPilotEvent event) {
        if (!(event.getCraft() instanceof PilotedCraft)) return;

        PilotedCraft craft = (PilotedCraft) event.getCraft();

        crafts.put(craft.getPilot().getUniqueId(), craft.getHitBox());
    }

    @EventHandler
    public void onCraftDetect(CraftDetectEvent event) {
        if (!(event.getCraft() instanceof PilotedCraft)) return;

        PilotedCraft craft = (PilotedCraft) event.getCraft();

        crafts.put(craft.getPilot().getUniqueId(), craft.getHitBox());
    }

    @EventHandler
    public void onCraftRelease(CraftReleaseEvent event) {
        if (!(event.getCraft() instanceof PilotedCraft)) return;

        PilotedCraft craft = (PilotedCraft) event.getCraft();
        
        crafts.remove(craft.getPilot().getUniqueId());
    }
}
