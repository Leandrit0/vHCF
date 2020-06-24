package com.doctordark.hcf.faction.event;

import org.bukkit.Bukkit;
import com.google.common.base.Preconditions;

import org.bukkit.entity.Player;

import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.google.common.base.Optional;
import java.util.UUID;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;

public class PlayerJoinFactionEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers;
    private final UUID uniqueID;
    private boolean cancelled;
    private Optional<Player> player;
    
    public PlayerJoinFactionEvent(final Player player, final PlayerFaction playerFaction) {
        super(playerFaction);
        Preconditions.checkNotNull((Object)player, (Object)"Player cannot be null");
        this.player = Optional.of(player);
        this.uniqueID = player.getUniqueId();
    }
    
    public PlayerJoinFactionEvent(final UUID playerUUID, final PlayerFaction playerFaction) {
        super(playerFaction);
        Preconditions.checkNotNull((Object)playerUUID, (Object)"Player UUID cannot be null");
        this.uniqueID = playerUUID;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerJoinFactionEvent.handlers;
    }
    
    public Optional<Player> getPlayer() {
        if (this.player == null) {
            this.player = Optional.fromNullable(Bukkit.getPlayer(this.uniqueID));
        }
        return this.player;
    }
    
    public UUID getUniqueID() {
        return this.uniqueID;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public HandlerList getHandlers() {
        return PlayerJoinFactionEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
