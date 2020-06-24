package com.doctordark.hcf.listener;

import org.bukkit.entity.Squid;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.material.EnderChest;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.entity.Player;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.doctordark.hcf.HCF;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.Listener;

public class WorldListener implements Listener
{
    public static final String DEFAULT_WORLD_NAME = "world";
    private final HCF plugin;
    
    public WorldListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onEntityExplode(final EntityExplodeEvent event) {
        event.blockList().clear();
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockChange(final BlockFromToEvent event) {
        if (event.getBlock().getType() == Material.DRAGON_EGG) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityPortalEnter(final EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBedEnter(final PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Beds are disabled on this server.");
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onWitherChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Wither || entity instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        switch (event.getBlock().getType()) {
            case SNOW:
            case ICE: {
                event.setCancelled(true);
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerSpawn(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            this.plugin.getEconomyManager().addBalance(player.getUniqueId(), 250);
            event.setSpawnLocation(Bukkit.getWorld("world").getSpawnLocation().add(0.5, 0.0, 0.5));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Squid) {
            event.setCancelled(true);
        }
    }
}
