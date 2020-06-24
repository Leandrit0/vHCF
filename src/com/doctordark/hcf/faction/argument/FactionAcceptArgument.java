package com.doctordark.hcf.faction.argument;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.struct.ChatChannel;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionAcceptArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionAcceptArgument(final HCF plugin) {
        super("accept", "Accept a join request from an existing faction.", new String[] { "join", "a" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <factionName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        if (this.plugin.getFactionManager().getPlayerFaction(player) != null) {
            sender.sendMessage(ChatColor.RED + "You are already in a faction.");
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only join player factions.");
            return true;
        }
        final PlayerFaction targetFaction = (PlayerFaction)faction;
        if (targetFaction.getMembers().size() >= 30) {
            sender.sendMessage(faction.getDisplayName(sender) + ChatColor.RED + " is full. Faction limits are at " + 30 + '.');
            return true;
        }
        if (!targetFaction.isOpen() && !targetFaction.getInvitedPlayerNames().contains(player.getName())) {
            sender.sendMessage(ChatColor.RED + faction.getDisplayName(sender) + ChatColor.RED + " has not invited you.");
            return true;
        }
        if (targetFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.MEMBER))) {
            targetFaction.broadcast(Relation.MEMBER.toChatColour() + sender.getName() + ChatColor.YELLOW + " has joined the faction.");
        }
        return true;
    }
}
