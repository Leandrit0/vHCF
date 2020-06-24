package com.doctordark.hcf.combatlog;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class LoggerDeathEvent extends Event
{
    private static final HandlerList handlers;
    private final LoggerEntity loggerEntity;
    
    public LoggerDeathEvent(final LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }
    
    public static HandlerList getHandlerList() {
        return LoggerDeathEvent.handlers;
    }
    
    public LoggerEntity getLoggerEntity() {
        return this.loggerEntity;
    }
    
    public HandlerList getHandlers() {
        return LoggerDeathEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
