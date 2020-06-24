package com.doctordark.hcf.faction.argument;

import java.util.Iterator;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.HashSet;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.chat.ClickAction;
import com.doctordark.util.chat.Text;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

public class FactionUnclaimArgument extends CommandArgument
{
    private static final ImmutableList<String> COMPLETIONS;
    private static final HashSet<String> stuff;
    private final HCF plugin;
    
    public FactionUnclaimArgument(final HCF plugin) {
        super("unclaim", "Unclaims land from your faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " ";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can un-claim land from a faction.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final FactionMember factionMember = playerFaction.getMember(player);
        if (factionMember.getRole() != Role.LEADER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction leader to unclaim land.");
            return true;
        }
        final Collection<Claim> factionClaims = playerFaction.getClaims();
        if (factionClaims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your faction does not own any claims.");
            return true;
        }
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("yes") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                for (final Claim claims : factionClaims) {
                    playerFaction.removeClaim(claims, (CommandSender)player);
                }
                factionClaims.clear();
                return true;
            }
            if (args[1].equalsIgnoreCase("no") && FactionUnclaimArgument.stuff.contains(player.getName())) {
                FactionUnclaimArgument.stuff.remove(player.getName());
                player.sendMessage(ChatColor.YELLOW + "You have been removed the unclaim-set.");
                return true;
            }
        }
        FactionUnclaimArgument.stuff.add(player.getName());
        new Text(ChatColor.YELLOW + "Do you want to unclaim " + ChatColor.BOLD + "all" + ChatColor.YELLOW + " of your land?").send((CommandSender)player);
        new Text(ChatColor.YELLOW + "If so, " + ChatColor.DARK_GREEN + "/f unclaim yes" + ChatColor.YELLOW + " otherwise do" + ChatColor.DARK_RED + " /f unclaim no" + ChatColor.GRAY + " (Click here to unclaim)").setHoverText(ChatColor.GOLD + "Click here to unclaim all").setClick(ClickAction.RUN_COMMAND, "/f unclaim yes").send((CommandSender)player);
        return true;
    }
    
    static {
        stuff = new HashSet<String>();
        COMPLETIONS = ImmutableList.of("all");
    }
}
