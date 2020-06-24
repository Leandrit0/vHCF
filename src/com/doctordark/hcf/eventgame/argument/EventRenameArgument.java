package com.doctordark.hcf.eventgame.argument;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.faction.EventFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

public class EventRenameArgument extends CommandArgument
{
    private final HCF plugin;
    
    public EventRenameArgument(final HCF plugin) {
        super("rename", "Renames an event");
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <oldName> <newName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Faction faction = this.plugin.getFactionManager().getFaction(args[2]);
        if (faction != null) {
            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[2] + '.');
            return true;
        }
        faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        final String oldName = faction.getName();
        faction.setName(args[2], sender);
        sender.sendMessage(ChatColor.YELLOW + "Renamed event " + ChatColor.WHITE + oldName + ChatColor.YELLOW + " to " + ChatColor.WHITE + faction.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.<String>emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
