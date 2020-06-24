package com.doctordark.hcf.timer;

import org.bukkit.plugin.Plugin;
import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.event.TimerExpireEvent;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class TimerRunnable
{
    private final UUID represented;
   @Getter private final Timer timer;
    private BukkitTask bukkitTask;
    private long expiryMillis;
    private long pauseMillis;
    
    public TimerRunnable(final Timer timer, final long duration) {
        this.represented = null;
        this.timer = timer;
        this.setRemaining(duration);
    }
    
    public TimerRunnable(final UUID playerUUID, final Timer timer, final long duration) {
        this.represented = playerUUID;
        this.timer = timer;
        this.setRemaining(duration);
    }
    

    public long getRemaining() {
        return this.getRemaining(false);
    }
    
    public void setRemaining(final long remaining) {
        this.setExpiryMillis(remaining);
    }
    
    public long getRemaining(final boolean ignorePaused) {
        if (!ignorePaused && this.pauseMillis != 0L) {
            return this.pauseMillis;
        }
        return this.expiryMillis - System.currentTimeMillis();
    }
    
    public long getExpiryMillis() {
        return this.expiryMillis;
    }
    
    private void setExpiryMillis(final long remainingMillis) {
        final long expiryMillis = System.currentTimeMillis() + remainingMillis;
        if (expiryMillis == this.expiryMillis) {
            return;
        }
        this.expiryMillis = expiryMillis;
        if (remainingMillis > 0L) {
            if (this.bukkitTask != null) {
                this.bukkitTask.cancel();
            }
            this.bukkitTask = new BukkitRunnable() {
                public void run() {
                    final TimerExpireEvent expireEvent = new TimerExpireEvent(TimerRunnable.this.represented, TimerRunnable.this.timer);
                    Bukkit.getPluginManager().callEvent((Event)expireEvent);
                }
            }.runTaskLater((Plugin)HCF.getPlugin(), remainingMillis / 50L);
        }
    }
    
    public long getPauseMillis() {
        return this.pauseMillis;
    }
    
    public void setPauseMillis(final long pauseMillis) {
        this.pauseMillis = pauseMillis;
    }
    
    public boolean isPaused() {
        return this.pauseMillis != 0L;
    }
    
    public void setPaused(final boolean paused) {
        if (paused == this.isPaused()) {
            return;
        }
        if (paused) {
            this.pauseMillis = this.getRemaining(true);
            this.cancel();
        }
        else {
            this.setExpiryMillis(this.pauseMillis);
            this.pauseMillis = 0L;
        }
    }
    
    public void cancel() {
        if (this.bukkitTask != null) {
            this.bukkitTask.cancel();
        }
    }
}
