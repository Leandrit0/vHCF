package com.doctordark.hcf.faction.argument;

import org.bukkit.OfflinePlayer;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionKickArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionKickArgument(final HCF plugin) {
        super("kick", "Kick a player from the faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "kickmember", "kickplayer" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can kick from a faction.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.isRaidable() && !this.plugin.getEotwHandler().isEndOfTheWorld()) {
            sender.sendMessage(ChatColor.RED + "You cannot kick players whilst your faction is raidable.");
            return true;
        }
        final FactionMember targetMember = playerFaction.getMember(args[1]);
        if (targetMember == null) {
            sender.sendMessage(ChatColor.RED + "Your faction does not have a member named '" + args[1] + "'.");
            return true;
        }
        final Role selfRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if (selfRole == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to kick members.");
            return true;
        }
        final Role targetRole = targetMember.getRole();
        if (targetRole == Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the faction leader.");
            return true;
        }
        if (targetRole == Role.CAPTAIN && selfRole == Role.CAPTAIN) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to kick captains.");
            return true;
        }
        if (playerFaction.setMember(targetMember.getUniqueID(), null, true)) {
            final Player onlineTarget = targetMember.toOnlinePlayer();
            if (onlineTarget != null) {
                onlineTarget.sendMessage(ChatColor.RED.toString() + "You were kicked from " + playerFaction.getName() + '.');
            }
            playerFaction.broadcast(ConfigurationService.ENEMY_COLOUR + targetMember.getName() + ChatColor.YELLOW + " has been kicked by " + ConfigurationService.TEAMMATE_COLOUR + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.<String>emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            return Collections.<String>emptyList();
        }
        final Role memberRole = playerFaction.getMember(player.getUniqueId()).getRole();
        if (memberRole == Role.MEMBER) {
            return Collections.<String>emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for (final UUID entry : playerFaction.getMembers().keySet()) {
            final Role targetRole = playerFaction.getMember(entry).getRole();
            if (targetRole != Role.LEADER) {
                if (targetRole == Role.CAPTAIN && memberRole != Role.LEADER) {
                    continue;
                }
                final OfflinePlayer target = Bukkit.getOfflinePlayer(entry);
                final String targetName = target.getName();
                if (targetName == null) {
                    continue;
                }
                if (results.contains(targetName)) {
                    continue;
                }
                results.add(targetName);
            }
        }
        return results;
    }
}
