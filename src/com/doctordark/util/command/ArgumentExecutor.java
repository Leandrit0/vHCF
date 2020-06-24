package com.doctordark.util.command;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Collection;

import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public abstract class ArgumentExecutor implements CommandExecutor, TabCompleter
{
    protected final List<CommandArgument> arguments;
    protected final String label;
    
    public ArgumentExecutor(final String label) {
        this.arguments = new ArrayList<CommandArgument>();
        this.label = label;
    }
    
    public boolean containsArgument(final CommandArgument argument) {
        return this.arguments.contains(argument);
    }
    
    public void addArgument(final CommandArgument argument) {
        this.arguments.add(argument);
    }
    
    public void removeArgument(final CommandArgument argument) {
        this.arguments.remove(argument);
    }
    
    public CommandArgument getArgument(final String id) {
        for (final CommandArgument argument : this.arguments) {
            final String name = argument.getName();
            if (name.equalsIgnoreCase(id) || Arrays.<String>asList(argument.getAliases()).contains(id.toLowerCase())) {
                return argument;
            }
        }
        return null;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public List<CommandArgument> getArguments() {
        return (List<CommandArgument>)ImmutableList.copyOf((Collection)this.arguments);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.STRIKETHROUGH + "-------" + ChatColor.AQUA + " [ " + WordUtils.capitalizeFully(this.label) + " Help" + " ] " + ChatColor.AQUA.toString() + ChatColor.STRIKETHROUGH + "-------");
            for (final CommandArgument argument : this.arguments) {
                final String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    sender.sendMessage(ChatColor.GOLD + argument.getUsage(label) + ChatColor.AQUA + " - " + ChatColor.YELLOW + argument.getDescription() + '.');
                }
            }
            return true;
        }
        final CommandArgument argument2 = this.getArgument(args[0]);
        final String permission2 = (argument2 == null) ? null : argument2.getPermission();
        if (argument2 == null || (permission2 != null && !sender.hasPermission(permission2))) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(this.label) + " sub-command " + args[0] + " not found.");
            return true;
        }
        argument2.onCommand(sender, command, label, args);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        List<String> results = new ArrayList<String>();
        if (args.length < 2) {
            for (final CommandArgument argument : this.arguments) {
                final String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }
        }
        else {
            final CommandArgument argument2 = this.getArgument(args[0]);
            if (argument2 == null) {
                return results;
            }
            final String permission2 = argument2.getPermission();
            if (permission2 == null || sender.hasPermission(permission2)) {
                results = argument2.onTabComplete(sender, command, label, args);
                if (results == null) {
                    return null;
                }
            }
        }
        return BukkitUtils.getCompletions(args, results);
    }
}
