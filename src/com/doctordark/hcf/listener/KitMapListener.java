package com.doctordark.hcf.listener;

import org.bukkit.event.entity.ItemSpawnEvent;

import com.doctordark.hcf.HCF;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.Listener;

public class KitMapListener implements Listener
{
    final HCF plugin;
    
    public KitMapListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent e) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
    }
    
  
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemSpawn(final ItemSpawnEvent event) {
    }
}
