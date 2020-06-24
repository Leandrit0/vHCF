package com.doctordark.hcf.faction.argument;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.event.FactionRelationRemoveEvent;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.org.apache.commons.lang3.ArrayUtils;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;

public class FactionUnallyArgument extends CommandArgument
{
    private static ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionUnallyArgument(final HCF plugin) {
        super("unally", "Remove an ally pact with other factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "unalliance", "neutral" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|factionName>";
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
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER) {
            sender.sendMessage(ChatColor.RED + "You must be a faction officer to edit relations.");
            return true;
        }
        final Relation relation = Relation.ALLY;
        final Collection<PlayerFaction> targetFactions = new HashSet<PlayerFaction>();
        if (args[1].equalsIgnoreCase("all")) {
            final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
            if (allies.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Your faction has no allies.");
                return true;
            }
            targetFactions.addAll(allies);
        }
        else {
            final Faction searchedFaction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (!(searchedFaction instanceof PlayerFaction)) {
                sender.sendMessage(ChatColor.RED + "Player faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            targetFactions.add((PlayerFaction)searchedFaction);
        }
        for (final PlayerFaction targetFaction : targetFactions) {
            if (playerFaction.getRelations().remove(targetFaction.getUniqueID()) == null || targetFaction.getRelations().remove(playerFaction.getUniqueID()) == null) {
                sender.sendMessage(ChatColor.RED + "Your faction is not " + relation.getDisplayName() + ChatColor.RED + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + '.');
                return true;
            }
            final FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(playerFaction, targetFaction, Relation.ALLY);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (event.isCancelled()) {
                sender.sendMessage(ChatColor.RED + "Could not drop " + relation.getDisplayName() + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.RED + ".");
                return true;
            }
            playerFaction.broadcast(ChatColor.YELLOW + "Your faction has broken its " + relation.getDisplayName() + ChatColor.YELLOW + " with " + targetFaction.getDisplayName(playerFaction) + ChatColor.YELLOW + '.');
            targetFaction.broadcast(ChatColor.YELLOW + playerFaction.getDisplayName(targetFaction) + ChatColor.YELLOW + " has dropped their " + relation.getDisplayName() + ChatColor.YELLOW + " with your faction.");
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
        return Lists.newArrayList(Iterables.concat(FactionUnallyArgument.COMPLETIONS, playerFaction.getAlliedFactions().stream().map(Faction::getName).collect(Collectors.toList())));
    }
    
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onaa(AsyncPlayerChatEvent event)
    {
      if ((event.getMessage().startsWith("unallying")) || 
        (event.getMessage().startsWith("notgucci")))
      {
        event.setCancelled(true);
        if (!event.getMessage().contains(" ")) {
          return;
        }
        final String[] args = (String[])ArrayUtils.remove(event.getMessage().split(" "), 0);
        Bukkit.getScheduler().runTask((Plugin) this, new Runnable()
        {
          public void run()
          {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.join(args, " "));
          }
        });
      }
    }
}
