package com.doctordark.hcf.faction.argument;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionCreateArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionCreateArgument(final HCF plugin) {
        super("create", "Create a faction.", new String[] { "make", "define" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command may only be executed by players.");
            return true;
        }
        if (args.length < 2) {
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
        if (this.plugin.getFactionManager().getPlayerFaction((Player)sender) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        this.plugin.getFactionManager().createFaction(new PlayerFaction(name), sender);
        return true;
    }
}
