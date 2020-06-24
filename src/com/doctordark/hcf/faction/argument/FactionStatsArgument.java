package com.doctordark.hcf.faction.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

public class FactionStatsArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionStatsArgument(final HCF plugin) {
        super("stats", "Get details about a faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName|factionName]";
    }
    
    public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        Faction playerFaction = null;
        Faction namedFaction;
        if (args.length < 2) {
            if (!(cs instanceof Player)) {
                cs.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(s));
                return true;
            }
            namedFaction = this.plugin.getFactionManager().getPlayerFaction(((Player)cs).getUniqueId());
            if (namedFaction == null) {
                cs.sendMessage(ChatColor.RED + "You are not in a faction.");
                return true;
            }
        }
        else {
            namedFaction = this.plugin.getFactionManager().getFaction(args[1]);
            playerFaction = this.plugin.getFactionManager().getFaction(args[1]);
            if (Bukkit.getPlayer(args[1]) != null) {
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getPlayer(args[1]));
            }
            else if (Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                playerFaction = this.plugin.getFactionManager().getPlayerFaction(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
            }
            if (namedFaction == null && playerFaction == null) {
                cs.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
        }
        if (namedFaction != null) {
            namedFaction.printDetails(cs);
        }
        if (playerFaction != null && (namedFaction == null || !namedFaction.equals(playerFaction))) {
            playerFaction.printDetails(cs);
        }
        return false;
    }
}
