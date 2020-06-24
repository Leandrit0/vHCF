package com.doctordark.hcf.listener;

import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.doctordark.hcf.ConfigurationService;

import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class BorderListener implements Listener
{
    private static final int BORDER_OFFSET_TELEPORTS = 50;
    
    public static boolean isWithinBorder(final Location location) {
        final int borderSize = ConfigurationService.BORDER_SIZES.get(location.getWorld().getEnvironment());
        return Math.abs(location.getBlockX()) <= borderSize && Math.abs(location.getBlockZ()) <= borderSize;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreaturePreSpawn(final CreatureSpawnEvent event) {
        if (!isWithinBorder(event.getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketFillEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot fill buckets past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!isWithinBorder(event.getBlockClicked().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot empty buckets past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!isWithinBorder(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break blocks past the border.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (!isWithinBorder(to) && isWithinBorder(from)) {
            final Player player = event.getPlayer();
            player.sendMessage(ChatColor.RED + "You cannot go past the border.");
            event.setTo(from);
            final Entity vehicle = player.getVehicle();
            if (vehicle != null) {
                vehicle.eject();
                vehicle.teleport(from);
                vehicle.setPassenger((Entity)player);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        final Location to = event.getTo();
        if (!isWithinBorder(to)) {
            final PlayerTeleportEvent.TeleportCause cause = event.getCause();
            if (cause != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || (cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL && isWithinBorder(event.getFrom()))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot go past the border.");
            }
            else {
                final World.Environment toEnvironment = to.getWorld().getEnvironment();
                if (toEnvironment != World.Environment.NORMAL) {
                    return;
                }
                final int x = to.getBlockX();
                final int z = to.getBlockZ();
                final int borderSize = ConfigurationService.BORDER_SIZES.get(toEnvironment);
                boolean extended = false;
                if (Math.abs(x) > borderSize) {
                    to.setX((x > 0) ? (borderSize - 50) : (-borderSize + 50));
                    extended = true;
                }
                if (Math.abs(z) > borderSize) {
                    to.setZ((z > 0) ? (borderSize - 50) : (-borderSize + 50));
                    extended = true;
                }
                if (extended) {
                    to.add(0.5, 0.0, 0.5);
                    event.setTo(to);
                    event.getPlayer().sendMessage(ChatColor.RED + "This portals travel location was over the border. It has been moved inwards.");
                }
            }
        }
    }
}
