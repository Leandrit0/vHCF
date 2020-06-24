package com.doctordark.hcf.faction.type;

import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Bukkit;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.util.BukkitUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.Map;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class RoadFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public static final int ROAD_EDGE_DIFF = 1000;
    public static final int ROAD_WIDTH_LEFT = 4;
    public static final int ROAD_WIDTH_RIGHT = 4;
    public static final int ROAD_MIN_HEIGHT = 0;
    public static final int ROAD_MAX_HEIGHT = 256;
    
    public RoadFaction(final String name) {
        super(name);
    }
    
    public RoadFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.ENEMY_COLOUR + this.getName().replace("st", "st ").replace("th", "th ");
    }
    
    @Override
    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + this.getDisplayName(sender));
        sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.GRAY + "None");
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public static class NorthRoadFaction extends RoadFaction implements ConfigurationSerializable
    {
        public NorthRoadFaction() {
            super("NorthRoad");
            for (final World world : Bukkit.getWorlds()) {
                final World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    final int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
                    final double offset = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
                    this.addClaim(new Claim(this, new Location(world, -2.5, 0.0, -offset), new Location(world, 3.0, 256.0, (double)(-(borderSize - 1)))), null);
                }
            }
        }
        
        public NorthRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class EastRoadFaction extends RoadFaction implements ConfigurationSerializable
    {
        public EastRoadFaction() {
            super("EastRoad");
            for (final World world : Bukkit.getWorlds()) {
                final World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    final int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
                    final double offset = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
                    this.addClaim(new Claim(this, new Location(world, offset, 0.0, -2.5), new Location(world, (double)(borderSize - 1), 256.0, 3.0)), null);
                }
            }
        }
        
        public EastRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class SouthRoadFaction extends RoadFaction implements ConfigurationSerializable
    {
        public SouthRoadFaction() {
            super("SouthRoad");
            for (final World world : Bukkit.getWorlds()) {
                final World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    final int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
                    final double offset = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
                    this.addClaim(new Claim(this, new Location(world, -2.5, 0.0, offset), new Location(world, 3.0, 256.0, (double)(borderSize - 1))), null);
                }
            }
        }
        
        public SouthRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }
    
    public static class WestRoadFaction extends RoadFaction implements ConfigurationSerializable
    {
        public WestRoadFaction() {
            super("WestRoad");
            for (final World world : Bukkit.getWorlds()) {
                final World.Environment environment = world.getEnvironment();
                if (environment != World.Environment.THE_END) {
                    final int borderSize = ConfigurationService.BORDER_SIZES.get(environment);
                    final double offset = ConfigurationService.SPAWN_RADIUS_MAP.get(environment) + 1.0;
                    this.addClaim(new Claim(this, new Location(world, -offset, 0.0, 3.0), new Location(world, (double)(-(borderSize - 1)), 256.0, -2.5)), null);
                }
            }
        }
        
        public WestRoadFaction(final Map<String, Object> map) {
            super(map);
        }
    }
}
