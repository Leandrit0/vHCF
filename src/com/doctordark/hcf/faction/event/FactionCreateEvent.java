package com.doctordark.hcf.faction.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.doctordark.hcf.faction.type.Faction;

import org.bukkit.event.Cancellable;

public class FactionCreateEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final CommandSender sender;
    private boolean cancelled;
    
    public FactionCreateEvent(final Faction faction, final CommandSender sender) {
        super(faction);
        this.sender = sender;
    }
    
    public static HandlerList getHandlerList() {
        return FactionCreateEvent.handlers;
    }
    
    public CommandSender getSender() {
        return this.sender;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return FactionCreateEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
