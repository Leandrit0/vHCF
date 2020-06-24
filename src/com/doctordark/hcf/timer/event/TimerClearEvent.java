package com.doctordark.hcf.timer.event;

import java.util.UUID;

import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.google.common.base.Optional;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerClearEvent extends Event
{
    private static final HandlerList handlers;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    
    public TimerClearEvent(final Timer timer) {
        this.userUUID = Optional.absent();
        this.timer = timer;
    }
    
    public TimerClearEvent(final UUID userUUID, final PlayerTimer timer) {
        this.userUUID = Optional.of(userUUID);
        this.timer = timer;
    }
    
    public static HandlerList getHandlerList() {
        return TimerClearEvent.handlers;
    }
    
    public Optional<UUID> getUserUUID() {
        return this.userUUID;
    }
    
    public Timer getTimer() {
        return this.timer;
    }
    
    public HandlerList getHandlers() {
        return TimerClearEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
