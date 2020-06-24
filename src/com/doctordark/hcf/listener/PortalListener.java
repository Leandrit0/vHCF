package com.doctordark.hcf.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.PlayerTimer;

import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.World;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.entity.EntityPortalEvent;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.Bukkit;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import org.bukkit.Location;
import org.bukkit.event.Listener;

public class PortalListener implements Listener
{
    private static final long PORTAL_MESSAGE_DELAY_THRESHOLD = 2500L;
    private final Location endExit;
    private final TObjectLongMap<UUID> messageDelays;
    private final HCF plugin;
    
    public PortalListener(final HCF plugin) {
        this.endExit = new Location(Bukkit.getWorld("world"), 0.0, 67.5, 200.0);
        this.messageDelays = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        final World toWorld = event.getTo().getWorld();
        if (toWorld != null && toWorld.getEnvironment() == World.Environment.THE_END) {
            event.useTravelAgent(false);
            event.setTo(toWorld.getSpawnLocation());
            return;
        }
        final World fromWorld = event.getFrom().getWorld();
        if (fromWorld != null && fromWorld.getEnvironment() == World.Environment.THE_END) {
            event.useTravelAgent(false);
            event.setTo(this.endExit);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onWorldChanged(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final World from = event.getFrom();
        final World to = player.getWorld();
        if (from.getEnvironment() != World.Environment.THE_END && to.getEnvironment() == World.Environment.THE_END && player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPortalEnter(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        final Location to = event.getTo();
        final World toWorld = to.getWorld();
        if (toWorld == null) {
            return;
        }
        if (toWorld.getEnvironment() == World.Environment.THE_END) {
            final Player player = event.getPlayer();
            PlayerTimer timer = this.plugin.getTimerManager().spawnTagTimer;
            long remaining;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
                event.setCancelled(true);
                return;
            }
            timer = this.plugin.getTimerManager().pvpProtectionTimer;
            if ((remaining = timer.getRemaining(player)) > 0L) {
                this.message(player, ChatColor.RED + "You cannot enter the End whilst your " + timer.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
                event.setCancelled(true);
                return;
            }
            event.useTravelAgent(false);
            event.setTo(toWorld.getSpawnLocation().add(0.5, 0.0, 0.5));
        }
    }
    
    private void message(final Player player, final String message) {
        final long last = this.messageDelays.get((Object)player.getUniqueId());
        final long millis = System.currentTimeMillis();
        if (last != this.messageDelays.getNoEntryValue() && last + 2500L - millis > 0L) {
            return;
        }
        this.messageDelays.put(player.getUniqueId(), millis);
        player.sendMessage(message);
    }
}
