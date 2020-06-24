package com.doctordark.hcf.eventgame.argument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.eventgame.faction.ConquestFaction;
import com.doctordark.hcf.eventgame.faction.KothFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

public class EventCreateArgument extends CommandArgument
{
    private final HCF plugin;
    
    public EventCreateArgument(final HCF plugin) {
        super("create", "Defines a new event", new String[] { "make", "define" });
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <eventName> <Conquest|KOTH|Siege>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
            return true;
        }
        final String upperCase2;
        final String upperCase = upperCase2 = args[2].toUpperCase();
        switch (upperCase2) {
            case "CONQUEST": {
                faction = new ConquestFaction(args[1]);
                break;
            }
            case "KOTH": {
                faction = new KothFaction(args[1]);
                break;
            }
            default: {
                sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
        }
        this.plugin.getFactionManager().createFaction(faction, sender);
        sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
        return true;
    }
    
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            return Collections.emptyList();
        }
        EventType[] eventTypes = EventType.values();
        ArrayList<String> results = new ArrayList<String>(eventTypes.length);
        for (EventType eventType : eventTypes) {
            results.add(eventType.name());
        }
        return results;
    }
}
