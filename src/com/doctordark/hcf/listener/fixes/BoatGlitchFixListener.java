package com.doctordark.hcf.listener.fixes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.block.Block;
import org.bukkit.entity.Vehicle;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.Listener;

public class BoatGlitchFixListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onVehicleCreate(final VehicleCreateEvent event) {
        final Vehicle vehicle = event.getVehicle();
        if (vehicle instanceof Boat) {
            final Boat boat = (Boat)vehicle;
            final Block belowBlock = boat.getLocation().add(0.0, -1.0, 0.0).getBlock();
            if (belowBlock.getType() != Material.WATER && belowBlock.getType() != Material.STATIONARY_WATER) {
                boat.remove();
            }
        }
    }
}
