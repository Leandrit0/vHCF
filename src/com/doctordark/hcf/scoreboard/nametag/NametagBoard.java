package com.doctordark.hcf.scoreboard.nametag;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class NametagBoard {

    private Player player;
    private Set<BufferedNametag> currentEntries = new HashSet<>();

    public NametagBoard(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
		return player;
	}
    
    public Set<BufferedNametag> getCurrentEntries() {
		return currentEntries;
	}
}
