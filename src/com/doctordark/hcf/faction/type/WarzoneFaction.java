package com.doctordark.hcf.faction.type;

import org.bukkit.command.CommandSender;

import com.doctordark.hcf.ConfigurationService;

import java.util.Map;

public class WarzoneFaction extends Faction
{
    public WarzoneFaction() {
        super("Warzone");
    }
    
    public WarzoneFaction(final Map<String, Object> map) {
        super(map);
    }
    
    @Override
    public String getDisplayName(final CommandSender sender) {
        return ConfigurationService.WARZONE_COLOUR + this.getName();
    }
}
