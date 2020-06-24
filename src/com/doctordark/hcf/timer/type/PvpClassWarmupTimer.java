package com.doctordark.hcf.timer.type;

import java.util.Collection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.PvpClass;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.TimerRunnable;
import com.doctordark.util.Config;
import com.google.common.base.Preconditions;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.event.Listener;

public class PvpClassWarmupTimer extends PlayerTimer implements Listener
{
    protected final ConcurrentMap<Object, Object> classWarmups;
    private final HCF plugin;
    
    public PvpClassWarmupTimer(final HCF plugin) {
        super("Class Warmup", TimeUnit.SECONDS.toMillis(10L), false);
        this.plugin = plugin;
        this.classWarmups = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + 5000L, TimeUnit.MILLISECONDS).build().asMap();
        new BukkitRunnable() {
            public void run() {
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    PvpClassWarmupTimer.this.attemptEquip(player);
                }
            }
        }.runTaskLater((Plugin)plugin, 10L);
    }
    
    @Override
    public void onDisable(final Config config) {
        super.onDisable(config);
        this.classWarmups.clear();
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.AQUA + ChatColor.BOLD.toString();
    }
    
    @Override
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.classWarmups.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        final String className = (String) this.classWarmups.remove(userUUID);
        Preconditions.checkNotNull((Object)className, "Attempted to equip a class for %s, but nothing was added", new Object[] { player.getName() });
        this.plugin.getPvpClassManager().setEquippedClass(player, this.plugin.getPvpClassManager().getPvpClass(className));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(final EquipmentSetEvent event) {
        final HumanEntity humanEntity = event.getHumanEntity();
        if (humanEntity instanceof Player) {
            this.attemptEquip((Player)humanEntity);
        }
    }
    
    private void attemptEquip(final Player player) {
        final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (equipped != null) {
            if (equipped.isApplicableFor(player)) {
                return;
            }
            this.plugin.getPvpClassManager().setEquippedClass(player, null);
        }
        PvpClass warmupClass = null;
        final String warmup = (String) this.classWarmups.get(player.getUniqueId());
        if (warmup != null) {
            warmupClass = this.plugin.getPvpClassManager().getPvpClass(warmup);
            if (!warmupClass.isApplicableFor(player)) {
                this.clearCooldown(player.getUniqueId());
            }
        }
        final Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
        for (final PvpClass pvpClass : pvpClasses) {
            if (warmupClass != pvpClass && pvpClass.isApplicableFor(player)) {
                this.classWarmups.put(player.getUniqueId(), pvpClass.getName());
                this.setCooldown(player, player.getUniqueId(), pvpClass.getWarmupDelay(), false);
                break;
            }
        }
    }
}
