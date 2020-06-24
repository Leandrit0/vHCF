package com.doctordark.hcf.listener;

import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.hcf.visualise.VisualBlock;
import com.doctordark.hcf.visualise.VisualType;
import com.google.common.base.Predicate;

import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class CoreListener implements Listener
{
    private final HCF plugin;
    
    public CoreListener(final HCF plugin) {
        this.plugin = plugin;
    }
 
    
    
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage((String)null);
        Player p = event.getPlayer();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerKickEvent event) {
        event.setLeaveMessage((String)null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage((String)null);
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, null, null, false);
        this.plugin.getUserManager().getUser(player.getUniqueId()).setShowClaimMap(false);
    }
}
