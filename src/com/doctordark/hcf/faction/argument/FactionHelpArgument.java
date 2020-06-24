package com.doctordark.hcf.faction.argument;

import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;

import com.google.common.collect.Multimap;
import com.doctordark.hcf.faction.FactionExecutor;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ArrayListMultimap;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.google.common.collect.ImmutableMultimap;

public class FactionHelpArgument extends CommandArgument
{
    private static final int HELP_PER_PAGE = 10;
    private final FactionExecutor executor;
    private ImmutableMultimap<Integer, String> pages;
    
    public FactionHelpArgument(final FactionExecutor executor) {
        super("help", "View help on how to use factions.");
        this.executor = executor;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            this.showPage(sender, label, 1);
            return true;
        }
        final Integer page = Ints.tryParse(args[1]);
        if (page == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        this.showPage(sender, label, page);
        return true;
    }
    
    private void showPage(final CommandSender sender, final String label, final int pageNumber) {
        if (this.pages == null) {
            final boolean isPlayer = sender instanceof Player;
            int val = 1;
            int count = 0;
            final Multimap<Integer, String> pages = ArrayListMultimap.create();
            for (final CommandArgument argument : this.executor.getArguments()) {
                if (argument.equals((Object)this)) {
                    continue;
                }
                final String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                if (argument.isPlayerOnly() && !isPlayer) {
                    continue;
                }
                ++count;
                pages.get(val).add(ChatColor.YELLOW + "/" + label + ' ' + argument.getName() + ChatColor.AQUA + " - " + ChatColor.GRAY + argument.getDescription());
                if (count % 10 != 0) {
                    continue;
                }
                ++val;
            }
            this.pages = (ImmutableMultimap<Integer, String>)ImmutableMultimap.copyOf((Multimap)pages);
        }
        final int totalPageCount = this.pages.size() / 10 + 1;
        if (pageNumber < 1) {
            sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1.");
            return;
        }
        if (pageNumber > totalPageCount) {
            sender.sendMessage(ChatColor.RED + "There are only " + totalPageCount + " pages.");
            return;
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.BLUE + " Faction Help " + ChatColor.DARK_GRAY + "(Page " + pageNumber + '/' + totalPageCount + ')');
        for (final String message : this.pages.get(pageNumber)) {
            sender.sendMessage("  " + message);
        }
        sender.sendMessage(ChatColor.BLUE + " To view other pages, use " + ChatColor.YELLOW + '/' + label + ' ' + this.getName() + " <page#>" + ChatColor.GOLD + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}
