package com.doctordark.hcf.faction.argument;

import java.util.Collection;
import java.util.Iterator;
import net.md_5.bungee.api.chat.TextComponent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.MapSorting;
import com.doctordark.util.command.CommandArgument;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import net.md_5.bungee.api.chat.BaseComponent;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class FactionListArgument extends CommandArgument
{
    private static final int MAX_FACTIONS_PER_PAGE = 10;
    private final HCF plugin;
    
    public FactionListArgument(final HCF plugin) {
        super("list", "See a list of all factions.");
        this.plugin = plugin;
        this.aliases = new String[] { "l" };
    }
    
    private static net.md_5.bungee.api.ChatColor fromBukkit(final ChatColor chatColor) {
        return net.md_5.bungee.api.ChatColor.getByChar(chatColor.getChar());
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        Integer page;
        if (args.length < 2) {
            page = 1;
        }
        else {
            page = Ints.tryParse(args[1]);
            if (page == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                return true;
            }
        }
        new BukkitRunnable() {
            public void run() {
                FactionListArgument.this.showList(page, label, sender);
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    private void showList(final int pageNumber, final String label, final CommandSender sender) {
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        final Map<PlayerFaction, Integer> factionOnlineMap = new HashMap<PlayerFaction, Integer>();
        final Player senderPlayer = (Player)sender;
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (senderPlayer == null || senderPlayer.canSee(target)) {
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(target);
                if (playerFaction == null) {
                    continue;
                }
                factionOnlineMap.put(playerFaction, factionOnlineMap.getOrDefault(playerFaction, 0) + 1);
            }
        }
        final Map<Integer, List<BaseComponent[]>> pages = new HashMap<Integer, List<BaseComponent[]>>();
        final List<Map.Entry<PlayerFaction, Integer>> sortedMap = (List<Map.Entry<PlayerFaction, Integer>>)MapSorting.sortedValues((Map)factionOnlineMap, (Comparator)Comparator.<Comparable>reverseOrder());
        for (final Map.Entry<PlayerFaction, Integer> entry : sortedMap) {
            int currentPage = pages.size();
            List<BaseComponent[]> results = pages.get(currentPage);
            if (results == null || results.size() >= 10) {
                pages.put(++currentPage, results = new ArrayList<BaseComponent[]>(10));
            }
            final PlayerFaction playerFaction2 = entry.getKey();
            final String displayName = playerFaction2.getDisplayName(sender);
            final int index = results.size() + ((currentPage > 1) ? ((currentPage - 1) * 10) : 0) + 1;
            final ComponentBuilder builder = new ComponentBuilder("  " + index + ". ").color(net.md_5.bungee.api.ChatColor.WHITE);
            builder.append(displayName).color(net.md_5.bungee.api.ChatColor.RED).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, '/' + label + " show " + playerFaction2.getName())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(net.md_5.bungee.api.ChatColor.YELLOW + "Click to view " + displayName + ChatColor.YELLOW + '.').create()));
            builder.append(" [" + ChatColor.GREEN + entry.getValue() + ChatColor.GRAY + '/' + playerFaction2.getMembers().size() + " Online] ", ComponentBuilder.FormatRetention.FORMATTING).color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(" [").color(net.md_5.bungee.api.ChatColor.GRAY);
            builder.append(JavaUtils.format((Number)playerFaction2.getDeathsUntilRaidable())).color(fromBukkit(playerFaction2.getDtrColour()));
            builder.append('/' + JavaUtils.format((Number)playerFaction2.getMaximumDeathsUntilRaidable()) + " DTR]").color(net.md_5.bungee.api.ChatColor.GRAY);
            results.add(builder.create());
        }
        final int maxPages = pages.size();
        if (pageNumber > maxPages) {
            sender.sendMessage(ChatColor.RED + "There " + ((maxPages == 1) ? ("is only " + maxPages + " page") : ("are only " + maxPages + " pages")) + ".");
            return;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.BLUE + " Faction List " + ChatColor.GRAY + "(Page " + pageNumber + '/' + maxPages + ')');
        final Player player = (Player)sender;
        final Collection<BaseComponent[]> components = pages.get(pageNumber);
        for (final BaseComponent[] component : components) {
            if (component == null) {
                continue;
            }
            if (player != null) {
                player.spigot().sendMessage(component);
            }
            else {
                sender.sendMessage(TextComponent.toPlainText(component));
            }
        }
        sender.sendMessage(ChatColor.BLUE + " To view other pages, use " + ChatColor.YELLOW + '/' + label + ' ' + this.getName() + " <page#>" + ChatColor.GOLD + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
