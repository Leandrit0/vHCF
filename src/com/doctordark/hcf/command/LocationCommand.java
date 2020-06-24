package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class LocationCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public LocationCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Player target;
        if (args.length >= 1 && sender.hasPermission(command.getPermission() + ".others")) {
            target = Bukkit.getPlayer(args[0]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " [playerName]");
                return true;
            }
            target = (Player)sender;
        }
        if (target == null || (sender instanceof Player && !((Player)sender).canSee(target))) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[0] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final Location location = target.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " is in the territory of " + factionAt.getDisplayName(sender) + ChatColor.YELLOW + '(' + (factionAt.isSafezone() ? (ChatColor.GREEN + "Non-Deathban") : (ChatColor.RED + "Deathban")) + ChatColor.YELLOW + ')');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1 && sender.hasPermission(command.getPermission() + ".others")) ? null : Collections.<String>emptyList();
    }
}
