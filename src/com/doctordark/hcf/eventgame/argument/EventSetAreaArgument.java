package com.doctordark.hcf.eventgame.argument;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collections;
import java.util.List;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.faction.EventFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;
import com.doctordark.util.cuboid.Cuboid;

public class EventSetAreaArgument extends CommandArgument
{
    private static final int MIN_EVENT_CLAIM_AREA = 8;
    private final HCF plugin;
    
    public EventSetAreaArgument(final HCF plugin) {
        super("setarea", "Sets the area of an event");
        this.plugin = plugin;
        this.aliases = new String[] { "setclaim", "setclaimarea", "setland" };
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <kothName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set event claim areas");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final WorldEditPlugin worldEditPlugin = this.plugin.getWorldEdit();
        if (worldEditPlugin == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set event claim areas.");
            return true;
        }
        final Player player = (Player)sender;
        final Selection selection = worldEditPlugin.getSelection(player);
        if (selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if (selection.getWidth() < 8 || selection.getLength() < 8) {
            sender.sendMessage(ChatColor.RED + "Event claim areas must be at least " + 8 + 'x' + 8 + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
        if (!(faction instanceof EventFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not an event faction named '" + args[1] + "'.");
            return true;
        }
        ((EventFaction)faction).setClaim(new Cuboid(selection.getMinimumPoint(), selection.getMaximumPoint()), (CommandSender)player);
        sender.sendMessage(ChatColor.YELLOW + "Updated the claim for event " + faction.getName() + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.<String>emptyList();
        }
        return this.plugin.getFactionManager().getFactions().stream().filter(faction -> faction instanceof EventFaction).map(Faction::getName).collect(Collectors.toList());
    }
}
