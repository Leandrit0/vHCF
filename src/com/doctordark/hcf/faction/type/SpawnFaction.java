package com.doctordark.hcf.faction.type;

import java.util.Map;
import java.util.Iterator;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.faction.claim.Claim;

public class SpawnFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public SpawnFaction() {
        super("Spawn");
        this.safezone = true;
        for (final World world : Bukkit.getWorlds()) {
            final World.Environment environment = world.getEnvironment();
            if (environment != World.Environment.THE_END) {
                final double radius = ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment());
                this.addClaim(new Claim(this, new Location(world, radius, 0.0, radius), new Location(world, -radius, (double)world.getMaxHeight(), -radius)), null);
            }
            else {
                final double radius = ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment());
                this.addClaim(new Claim(this, new Location(world, 48.5, 0.0, -33.5), new Location(world, 107.5, (double)world.getMaxHeight(), 8.5)), null);
            }
        }
    }
    
    public SpawnFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public boolean isDeathban() {
        return false;
    }
}
