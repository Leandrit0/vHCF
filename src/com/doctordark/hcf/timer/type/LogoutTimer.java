package com.doctordark.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.combatlog.CombatLogListener;
import com.doctordark.hcf.timer.PlayerTimer;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.Listener;

public class LogoutTimer extends PlayerTimer implements Listener
{
    public LogoutTimer() {
        super("Logout", TimeUnit.SECONDS.toMillis(30L), false);
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }
    
    private void checkMovement(final Player player, final Location from, final Location to) {
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        if (this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You moved a block, " + this.getDisplayName() + ChatColor.RED + " timer cancelled.");
            this.clearCooldown(player);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.checkMovement(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (this.getRemaining(event.getPlayer().getUniqueId()) > 0L) {
            this.clearCooldown(uuid);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            if (this.getRemaining(player) > 0L) {
                player.sendMessage(ChatColor.RED + "You were damaged, " + this.getDisplayName() + ChatColor.RED + " timer ended.");
                this.clearCooldown(player);
            }
        }
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        CombatLogListener.safelyDisconnect(player, ChatColor.GREEN + "You have been safely logged out.");
    }
    
    public void run(final Player player) {
        final long remainingMillis = this.getRemaining(player);
        if (remainingMillis > 0L) {
            player.sendMessage(this.getDisplayName() + ChatColor.BLUE + " timer is disconnecting you in " + ChatColor.BOLD + HCF.getRemaining(remainingMillis, true, false) + ChatColor.BLUE + '.');
        }
    }
}
