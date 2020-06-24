package com.doctordark.hcf.listener.fixes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.Listener;

public class InfinityArrowFixListener implements Listener
{
    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(final ProjectileHitEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Arrow) {
            final Arrow arrow = (Arrow)entity;
            if (!(arrow.getShooter() instanceof Player) || ((CraftArrow)arrow).getHandle().fromPlayer == 2) {
                arrow.remove();
            }
        }
    }
}
