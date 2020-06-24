package com.doctordark.hcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.doctordark.hcf.HCF;

import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class EndListener implements Listener
{
    private final Location endExitLocation;
    
    public EndListener() {
    	double x = HCF.getPlugin().getConfig().getDouble("end-exit.x");
    	double y = HCF.getPlugin().getConfig().getDouble("end-exit.y");
    	double z = HCF.getPlugin().getConfig().getDouble("end-exit.z");
        this.endExitLocation = new Location(Bukkit.getWorld("world"), x, y, z);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(event.getTo().getWorld().getSpawnLocation().clone().add(0.5, 0.0, 0.5));
            }
            else if (event.getFrom().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.setTo(this.endExitLocation);
            }
        }
    }
}
