package com.doctordark.hcf.faction.argument.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.faction.type.SystemFaction;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;

public class FactionSystemCreateArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionSystemCreateArgument(final HCF plugin) {
        super("systemcreate", "Create a faction.", new String[] { "make", "define" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName> <safezone true or false>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final String name = args[1];


        if (ConfigurationService.DISALLOWED_FACTION_NAMES.contains(name.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "'" + name + "' is a blocked faction name.");
            return true;
        }
        if (name.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Faction names must have at least " + 3 + " characters.");
            return true;
        }
        if (name.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Faction names cannot be longer than " + 16 + " characters.");
            return true;
        }
        if (!JavaUtils.isAlphanumeric(name)) {
            sender.sendMessage(ChatColor.RED + "Faction names may only be alphanumeric.");
            return true;
        }
        if (this.plugin.getFactionManager().getFaction(name) != null) {
            sender.sendMessage(ChatColor.RED + "Faction '" + name + "' already exists.");
            return true;
        }
        final String upperCase2;
        final String upperCase = upperCase2 = args[2].toUpperCase();
        switch(upperCase2) {
        case "TRUE":{
        	this.plugin.getFactionManager().createFaction(new SystemFaction(name, true));
            break;
        }
        case "FALSE": {
        	this.plugin.getFactionManager().createFaction(new SystemFaction(name, false));
            break;
         }
        case "true":{
        	this.plugin.getFactionManager().createFaction(new SystemFaction(name, true));
            break;
        }
        case "false": {
        	this.plugin.getFactionManager().createFaction(new SystemFaction(name, false));
            break;
         }
        default:{       	
        	 sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
             return true;
         }
        }
        
        return true;
    }
}

