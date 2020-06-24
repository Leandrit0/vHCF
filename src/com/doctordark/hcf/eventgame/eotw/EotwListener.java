package com.doctordark.hcf.eventgame.eotw;

import org.bukkit.ChatColor;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.event.FactionClaimChangeEvent;
import com.doctordark.hcf.faction.event.cause.ClaimChangeCause;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;


import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public class EotwListener implements Listener
{
    private final HCF plugin;
    
    public EotwListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getPlayer());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final EotwHandler.EotwRunnable runnable = this.plugin.getEotwHandler().getRunnable();
        if (runnable != null) {
            runnable.handleDisconnect(event.getEntity());
        }
    }
    
  
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionClaimChange(final FactionClaimChangeEvent event) {
        if (this.plugin.getEotwHandler().isEndOfTheWorld() && event.getCause() == ClaimChangeCause.CLAIM) {
            final Faction faction = event.getClaimableFaction();
            if (faction instanceof PlayerFaction) {
                event.setCancelled(true);
                event.getSender().sendMessage(ChatColor.RED + "Player based faction land cannot be claimed during EOTW.");
            }
        }
    }
}
