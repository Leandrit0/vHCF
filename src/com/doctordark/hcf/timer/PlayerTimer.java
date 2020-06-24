package com.doctordark.hcf.timer;

import java.util.Set;
import java.util.LinkedHashMap;
import org.bukkit.configuration.MemorySection;

import javax.annotation.Nullable;
import java.util.Iterator;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import com.doctordark.hcf.timer.event.TimerClearEvent;
import com.doctordark.hcf.timer.event.TimerExpireEvent;
import com.doctordark.hcf.timer.event.TimerExtendEvent;
import com.doctordark.hcf.timer.event.TimerPauseEvent;
import com.doctordark.hcf.timer.event.TimerStartEvent;
import com.doctordark.util.Config;
import com.google.common.base.Optional;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;
import java.util.Map;

public abstract class PlayerTimer extends Timer
{
    private static final String COOLDOWN_PATH = "timer-cooldowns";
    private static final String PAUSE_PATH = "timer-pauses";
    protected final boolean persistable;
    protected final Map<UUID, TimerRunnable> cooldowns;
    
    public PlayerTimer(final String name, final long defaultCooldown) {
        this(name, defaultCooldown, true);
    }
    
    public PlayerTimer(final String name, final long defaultCooldown, final boolean persistable) {
        super(name, defaultCooldown);
        this.cooldowns = new ConcurrentHashMap<UUID, TimerRunnable>();
        this.persistable = persistable;
    }
    
    public void onExpire(final UUID userUUID) {
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerExpireLoadReduce(final TimerExpireEvent event) {
        if (event.getTimer().equals(this)) {
            final Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                final UUID userUUID = (UUID)optionalUserUUID.get();
                this.onExpire(userUUID);
                this.clearCooldown(userUUID);
            }
        }
    }
    
    public void clearCooldown(final Player player) {
        this.clearCooldown(player.getUniqueId());
    }
    
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = this.cooldowns.remove(playerUUID);
        if (runnable != null) {
            runnable.cancel();
            Bukkit.getPluginManager().callEvent((Event)new TimerClearEvent(playerUUID, this));
            return runnable;
        }
        return null;
    }
    
    public void clearCooldowns() {
        for (final UUID uuid : this.cooldowns.keySet()) {
            this.clearCooldown(uuid);
        }
    }
    
    public boolean isPaused(final Player player) {
        return this.isPaused(player.getUniqueId());
    }
    
    public boolean isPaused(final UUID playerUUID) {
        final TimerRunnable runnable = this.cooldowns.get(playerUUID);
        return runnable != null && runnable.isPaused();
    }
    
    public void setPaused(@Nullable final Player player, final UUID playerUUID, final boolean paused) {
        final TimerRunnable runnable = this.cooldowns.get(playerUUID);
        if (runnable != null && runnable.isPaused() != paused) {
            final TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                runnable.setPaused(paused);
            }
        }
    }
    
    public long getRemaining(final Player player) {
        return this.getRemaining(player.getUniqueId());
    }
    
    public long getRemaining(final UUID playerUUID) {
        final TimerRunnable runnable = this.cooldowns.get(playerUUID);
        return (runnable == null) ? 0L : runnable.getRemaining();
    }
    
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID) {
        return this.setCooldown(player, playerUUID, this.defaultCooldown, false);
    }
    
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long duration, final boolean overwrite) {
        TimerRunnable runnable;
        if (duration <= 0L) {
            runnable = this.clearCooldown(playerUUID);
        }
        else {
            runnable = this.cooldowns.get(playerUUID);
        }
        if (runnable != null) {
            final long remaining = runnable.getRemaining();
            if (!overwrite && remaining > 0L && duration > remaining) {
                return false;
            }
            final TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                return false;
            }
            runnable.setRemaining(duration);
        }
        else {
            Bukkit.getPluginManager().callEvent((Event)new TimerStartEvent(player, playerUUID, this, duration));
            runnable = new TimerRunnable(playerUUID, this, duration);
        }
        this.cooldowns.put(playerUUID, runnable);
        return true;
    }
    
    @Override
    public void load(final Config config) {
        if (!this.persistable) {
            return;
        }
        String path = "timer-cooldowns." + this.name;
        Object object = config.get(path);
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final long millis = System.currentTimeMillis();
            for (final String id : section.getKeys(false)) {
                final long remaining = config.getLong(section.getCurrentPath() + '.' + id) - millis;
                if (remaining > 0L) {
                    this.setCooldown(null, UUID.fromString(id), remaining, true);
                }
            }
        }
        path = "timer-pauses." + this.name;
        if ((object = config.get(path)) instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            for (final String id2 : section.getKeys(false)) {
                final TimerRunnable timerRunnable = this.cooldowns.get(UUID.fromString(id2));
                if (timerRunnable == null) {
                    continue;
                }
                timerRunnable.setPauseMillis(config.getLong(path + '.' + id2));
            }
        }
    }
    
    @Override
    public void onDisable(final Config config) {
        if (this.persistable) {
            final Set<Map.Entry<UUID, TimerRunnable>> entrySet = this.cooldowns.entrySet();
            final Map<String, Long> pauseSavemap = new LinkedHashMap<String, Long>(entrySet.size());
            final Map<String, Long> cooldownSavemap = new LinkedHashMap<String, Long>(entrySet.size());
            for (final Map.Entry<UUID, TimerRunnable> entry : entrySet) {
                final String id = entry.getKey().toString();
                final TimerRunnable runnable = entry.getValue();
                pauseSavemap.put(id, runnable.getPauseMillis());
                cooldownSavemap.put(id, runnable.getExpiryMillis());
            }
            config.set("timer-pauses." + this.name, (Object)pauseSavemap);
            config.set("timer-cooldowns." + this.name, (Object)cooldownSavemap);
        }
    }
}
