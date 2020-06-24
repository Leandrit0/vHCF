package com.doctordark.hcf.eventgame.faction;

import java.util.List;
import org.bukkit.Location;

import java.util.Collection;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.cuboid.Cuboid;


import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

import java.util.Map;

public abstract class EventFaction extends ClaimableFaction
{
    public EventFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public EventFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
    }
    
    @Override
    public String getDisplayName(final Faction faction) {
        if (this.getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE.toString() + this.getName() + ' ' + this.getEventType().getDisplayName();
        }
        return ChatColor.DARK_PURPLE + this.getEventType().getDisplayName();
    }
    public String getScoreboardName() {
        if(getEventType() == EventType.KOTH){
          return Color.color(HCF.getPlugin().getConfig().getString("event.koth.formatted").replace("%name%", name));
      }
          return Color.color(HCF.getPlugin().getConfig().getString("event.conquest.formatted").replace("%name%",name));
      }

    @Override
    public String getDisplayName(final CommandSender sender) {
        if (this.getEventType() == EventType.KOTH) {
            return ChatColor.LIGHT_PURPLE.toString() + this.getName() + ' ' + this.getEventType().getDisplayName();
        }
        return ChatColor.DARK_PURPLE + this.getEventType().getDisplayName();
    }
    
    public void setClaim(final Cuboid cuboid, final CommandSender sender) {
        this.removeClaims(this.getClaims(), sender);
        final Location min = cuboid.getMinimumPoint();
        min.setY(0);
        final Location max = cuboid.getMaximumPoint();
        max.setY(256);
        this.addClaim(new Claim(this, min, max), sender);
    }
    
    public abstract EventType getEventType();
    
    public abstract List<CaptureZone> getCaptureZones();
}
