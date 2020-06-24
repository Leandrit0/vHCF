package com.doctordark.hcf.faction.struct;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.util.BukkitUtils;

import org.bukkit.DyeColor;
import org.bukkit.ChatColor;

public enum Relation
{
    MEMBER(3), 
    ALLY(2), 
    ENEMY(1);
    
    private final int value;
    
    private Relation(final int value) {
        this.value = value;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public boolean isAtLeast(final Relation relation) {
        return this.value >= relation.value;
    }
    
    public boolean isAtMost(final Relation relation) {
        return this.value <= relation.value;
    }
    
    public boolean isMember() {
        return this == Relation.MEMBER;
    }
    
    public boolean isAlly() {
        return this == Relation.ALLY;
    }
    
    public boolean isEnemy() {
        return this == Relation.ENEMY;
    }
    
    public String getDisplayName() {
        switch (this) {
            case ALLY: {
                return this.toChatColour() + "alliance";
            }
            default: {
                return this.toChatColour() + this.name().toLowerCase();
            }
        }
    }
    
    public ChatColor toChatColour() {
        switch (this) {
            case MEMBER: {
                return ConfigurationService.TEAMMATE_COLOUR;
            }
            case ALLY: {
                return ConfigurationService.ALLY_COLOUR;
            }
            default: {
                return ConfigurationService.ENEMY_COLOUR;
            }
        }
    }
    
    public DyeColor toDyeColour() {
        return BukkitUtils.toDyeColor(this.toChatColour());
    }
}
