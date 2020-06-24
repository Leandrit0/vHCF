package com.doctordark.hcf.faction.argument;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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
import com.google.common.collect.ImmutableList;

public class FactionUninviteArgument extends CommandArgument
{
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionUninviteArgument(final HCF plugin) {
        super("uninvite", "Revoke an invitation to a player.", new String[] { "deinvite", "deinv", "uninv", "revoke" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|playerName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-invite from a faction.");
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
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to un-invite players.");
            return true;
        }
        final Set<String> invitedPlayerNames = playerFaction.getInvitedPlayerNames();
        if (args[1].equalsIgnoreCase("all")) {
            invitedPlayerNames.clear();
            sender.sendMessage(ChatColor.YELLOW + "You have cleared all pending invitations.");
            return true;
        }
        if (!invitedPlayerNames.remove(args[1])) {
            sender.sendMessage(ChatColor.RED + "There is not a pending invitation for " + args[1] + '.');
            return true;
        }
        playerFaction.broadcast(ChatColor.YELLOW + factionMember.getRole().getAstrix() + sender.getName() + " has uninvited " + ConfigurationService.ENEMY_COLOUR + args[1] + ChatColor.YELLOW + " from the faction.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.<String>emptyList();
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null || playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            return Collections.<String>emptyList();
        }
        final List<String> results = new ArrayList<String>((Collection<? extends String>)FactionUninviteArgument.COMPLETIONS);
        results.addAll(playerFaction.getInvitedPlayerNames());
        return results;
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
