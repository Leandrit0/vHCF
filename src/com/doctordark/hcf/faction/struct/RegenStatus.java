package com.doctordark.hcf.faction.struct;

import org.bukkit.ChatColor;

public enum RegenStatus
{
    FULL(ChatColor.GREEN.toString() + '\u25b6'), 
    REGENERATING(ChatColor.GOLD.toString() + '\u25b2'), 
    PAUSED(ChatColor.RED.toString() + '\u25a0');
    
    private final String symbol;
    
    private RegenStatus(final String symbol) {
        this.symbol = symbol;
    }
    
    public String getSymbol() {
        return this.symbol;
    }
}
