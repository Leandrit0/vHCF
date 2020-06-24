package com.doctordark.hcf.faction.type;

import net.md_5.bungee.api.ChatColor;
import java.util.Map;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.doctordark.hcf.faction.claim.Claim;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable
{
    public EndPortalFaction() {
        super("EndPortal");
        final World overworld = Bukkit.getWorld("world");
        final int maxHeight = overworld.getMaxHeight();
        final int min = 985;
        final int max = 1015;
        this.addClaim(new Claim(this, new Location(overworld, 985.0, 0.0, 985.0), new Location(overworld, 1015.0, (double)maxHeight, 1015.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, -1015.0, (double)maxHeight, -1015.0), new Location(overworld, -985.0, 0.0, -985.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, -1015.0, 0.0, 985.0), new Location(overworld, -985.0, (double)maxHeight, 1015.0)), null);
        this.addClaim(new Claim(this, new Location(overworld, 985.0, 0.0, -1015.0), new Location(overworld, 1015.0, (double)maxHeight, -985.0)), null);
        this.safezone = false;
    }
    
    public EndPortalFaction(final Map<String, Object> map) {
        super(map);
    }
    
    public String getDisplayName(final CommandSender sender) {
        return ChatColor.DARK_AQUA + this.getName().replace("EndPortal", "End Portal");
    }
    
    public boolean isDeathban() {
        return true;
    }
}
