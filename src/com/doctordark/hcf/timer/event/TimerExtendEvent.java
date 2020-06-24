package com.doctordark.hcf.timer.event;

import javax.annotation.Nullable;

import java.util.UUID;
import org.bukkit.entity.Player;

import com.doctordark.hcf.timer.GlobalTimer;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.google.common.base.Optional;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class TimerExtendEvent extends Event implements Cancellable
{
    private static final HandlerList handlers;
    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long previousDuration;
    private boolean cancelled;
    private long newDuration;
    
    public TimerExtendEvent(final GlobalTimer timer, final long previousDuration, final long newDuration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }
    
    public TimerExtendEvent(@Nullable final Player player, final UUID uniqueId, final PlayerTimer timer, final long previousDuration, final long newDuration) {
        this.player = Optional.fromNullable(player);
        this.userUUID = Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }
    
    public static HandlerList getHandlerList() {
        return TimerExtendEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        return this.player;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public long getPreviousDuration() {
        return this.previousDuration;
    }
    
    public long getNewDuration() {
        return this.newDuration;
    }
    
    public void setNewDuration(final long newDuration) {
        this.newDuration = newDuration;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return TimerExtendEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
