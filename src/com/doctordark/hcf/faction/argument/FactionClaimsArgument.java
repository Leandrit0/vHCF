package com.doctordark.hcf.faction.argument;

import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionClaimsArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionClaimsArgument(final HCF plugin) {
        super("claims", "View all claims for a faction.");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [factionName]";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final PlayerFaction selfFaction = (sender instanceof Player) ? this.plugin.getFactionManager().getPlayerFaction((Player)sender) : null;
        ClaimableFaction targetFaction;
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            if (selfFaction == null) {
                sender.sendMessage(ChatColor.RED + "You are not in a faction.");
                return true;
            }
            targetFaction = selfFaction;
        }
        else {
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            if (!(faction instanceof ClaimableFaction)) {
                sender.sendMessage(ChatColor.RED + "You can only check the claims of factions that can have claims.");
                return true;
            }
            targetFaction = (ClaimableFaction)faction;
        }
        final Collection<Claim> claims = targetFaction.getClaims();
        if (claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Faction " + targetFaction.getDisplayName(sender) + ChatColor.RED + " has no claimed land.");
            return true;
        }
        if (sender instanceof Player && !sender.isOp() && targetFaction instanceof PlayerFaction && ((PlayerFaction)targetFaction).getHome() == null && (selfFaction == null || !selfFaction.equals(targetFaction))) {
            sender.sendMessage(ChatColor.RED + "You cannot view the claims of " + targetFaction.getDisplayName(sender) + ChatColor.RED + " because their home is unset.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Claims of " + targetFaction.getDisplayName(sender));
        for (final Claim claim : claims) {
            sender.sendMessage(ChatColor.GRAY + " " + claim.getFormattedName());
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.<String>emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
}
