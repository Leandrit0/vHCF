package com.doctordark.hcf.combatlog;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class LoggerSpawnEvent extends Event
{
    private static final HandlerList handlers;
    private final LoggerEntity loggerEntity;
    
    public LoggerSpawnEvent(final LoggerEntity loggerEntity) {
        this.loggerEntity = loggerEntity;
    }
    
    public static HandlerList getHandlerList() {
        return LoggerSpawnEvent.handlers;
    }
    
    public LoggerEntity getLoggerEntity() {
        return this.loggerEntity;
    }
    
    public HandlerList getHandlers() {
        return LoggerSpawnEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
