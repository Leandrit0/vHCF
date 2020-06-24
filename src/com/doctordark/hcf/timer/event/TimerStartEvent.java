package com.doctordark.hcf.timer.event;

import javax.annotation.Nullable;

import java.util.UUID;
import org.bukkit.entity.Player;

import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.google.common.base.Optional;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class TimerStartEvent extends Event
{
    private static final HandlerList handlers;
    private final Optional<Player> player;
    private final Optional<UUID> userUUID;
    private final Timer timer;
    private final long duration;
    
    public TimerStartEvent(final Timer timer, final long duration) {
        this.player = Optional.absent();
        this.userUUID = Optional.absent();
        this.timer = timer;
        this.duration = duration;
    }
    
    public TimerStartEvent(@Nullable final Player player, final UUID uniqueId, final PlayerTimer timer, final long duration) {
        this.player = Optional.fromNullable(player);
        this.userUUID = Optional.fromNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }
    
    public static HandlerList getHandlerList() {
        return TimerStartEvent.handlers;
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
    
    public long getDuration() {
        return this.duration;
    }
    
    public HandlerList getHandlers() {
        return TimerStartEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
