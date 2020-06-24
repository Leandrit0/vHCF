package com.doctordark.hcf.scoreboard.nametag;

import org.bukkit.entity.Player;

public class BufferedNametag {

    private String groupName, prefix, suffix;
    private boolean showHealth = false, friendlyInvis = false;
    private Player player;

    public BufferedNametag(String groupName, String prefix, String suffix, boolean showHealth, boolean friendlyInvis, Player player) {
    	this.groupName = groupName;
    	this.prefix = prefix;
    	this.suffix = suffix;
    	this.showHealth = showHealth;
    	this.friendlyInvis = friendlyInvis;
    	this.player = player;
	}
    
    public String getGroupName() {
		return groupName;
	}
    
    public String getPrefix() {
		return prefix;
	}
    
    public String getSuffix() {
		return suffix;
	}
    
    public boolean isShowHealth() {
		return showHealth;
	}
    
    public boolean isFriendlyInvis() {
		return friendlyInvis;
	}
    
    public Player getPlayer() {
		return player;
	}
}
