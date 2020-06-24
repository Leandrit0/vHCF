package com.doctordark.hcf.scoreboard.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class AssembleBoardDestroyEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();

    private Player player;
    public static HandlerList getHandlerList() {
		return handlerList;
	}

	public static void setHandlerList(HandlerList handlerList) {
		AssembleBoardDestroyEvent.handlerList = handlerList;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	private boolean cancelled = false;

    public AssembleBoardDestroyEvent(Player player) {
        this.player = player;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
