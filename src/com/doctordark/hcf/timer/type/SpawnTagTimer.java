package com.doctordark.hcf.timer.type;

import org.bukkit.event.player.PlayerRespawnEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.command.CommandSender;

import com.google.common.base.Predicate;

import org.bukkit.Bukkit;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.event.PlayerClaimEnterEvent;
import com.doctordark.hcf.faction.event.PlayerJoinFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeaveFactionEvent;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.event.TimerClearEvent;
import com.doctordark.hcf.timer.event.TimerStartEvent;
import com.doctordark.hcf.visualise.VisualBlock;
import com.doctordark.hcf.visualise.VisualType;
import com.doctordark.util.BukkitUtils;
import com.google.common.base.Optional;
import java.util.UUID;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.event.Listener;

public class SpawnTagTimer extends PlayerTimer implements Listener
{
    private static final long NON_WEAPON_TAG = 5000L;
    private final HCF plugin;
    
    public SpawnTagTimer(final HCF plugin) {
        super("Spawn Tag", TimeUnit.SECONDS.toMillis(30L));
        this.plugin = plugin;
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }
    
   
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if (event.getTimer().equals(this)) {
            final Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire((UUID)optionalUserUUID.get());
            }
        }
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.SPAWN_BORDER, null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionJoin(final PlayerJoinFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            final long remaining = this.getRemaining(player);
            if (remaining > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(final PlayerLeaveFactionEvent event) {
        final Optional<Player> optional = event.getPlayer();
        if (optional.isPresent()) {
            final Player player = (Player)optional.get();
            if (this.getRemaining(player) > 0L) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPreventClaimEnter(final PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
            return;
        }
        final Player player = event.getPlayer();
        if (!event.getFromFaction().isSafezone() && event.getToFaction().isSafezone() && this.getRemaining(player) > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot enter " + event.getToFaction().getDisplayName((CommandSender)player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
        final Entity entity;
        if (attacker != null && (entity = event.getEntity()) instanceof Player) {
            final Player attacked = (Player)entity;
            boolean weapon = event.getDamager() instanceof Arrow;
            if (!weapon) {
                final ItemStack stack = attacker.getItemInHand();
                weapon = (stack != null && EnchantmentTarget.WEAPON.includes(stack));
            }
            final long duration = weapon ? this.defaultCooldown : 45000L;
            this.setCooldown(attacked, attacked.getUniqueId(), Math.max(this.getRemaining(attacked), duration), true);
            this.setCooldown(attacker, attacker.getUniqueId(), Math.max(this.getRemaining(attacker), duration), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStart(final TimerStartEvent event) {
        if (event.getTimer().equals(this)) {
            final Optional<Player> optional = event.getPlayer();
            if (optional.isPresent()) {
                final Player player = (Player)optional.get();
                player.sendMessage(ChatColor.AQUA + "You are now " + "spawn tagged" + ChatColor.AQUA + " for " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(event.getDuration(), true, true) + ChatColor.AQUA + '.');
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        this.clearCooldown(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPreventClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && !event.getFromFaction().isSafezone() && event.getToFaction().isSafezone()) {
            this.clearCooldown(event.getPlayer());
        }
    }
}
