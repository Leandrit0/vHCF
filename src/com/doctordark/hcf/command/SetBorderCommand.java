package com.doctordark.hcf.command;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.util.BukkitUtils;

import net.minecraft.util.com.google.common.base.Enums;
import net.minecraft.util.com.google.common.base.Optional;
import net.minecraft.util.com.google.common.primitives.Ints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class SetBorderCommand implements CommandExecutor, TabCompleter
{
    private static final int MIN_SET_SIZE = 50;
    private static final int MAX_SET_SIZE = 25000;
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <worldType> <amount>");
            return true;
        }
        final Optional<World.Environment> optional = (Optional<World.Environment>)Enums.getIfPresent((Class)World.Environment.class, args[0]);
        if (!optional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Environment '" + args[0] + "' not found.");
            return true;
        }
        final Integer amount = Ints.tryParse(args[1]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if (amount < 50) {
            sender.sendMessage(ChatColor.RED + "Minimum border size is " + 50 + 100 + '.');
            return true;
        }
        if (amount > 25000) {
            sender.sendMessage(ChatColor.RED + "Maximum border size is " + 25000 + '.');
            return true;
        }
        final World.Environment environment = (World.Environment)optional.get();
        ConfigurationService.BORDER_SIZES.put(environment, amount);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set border size of environment " + environment.name() + " to " + amount + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 1) {
            return Collections.<String>emptyList();
        }
        final World.Environment[] values = World.Environment.values();
        final List<String> results = new ArrayList<String>(values.length);
        for (final World.Environment environment : values) {
            results.add(environment.name());
        }
        return (List<String>)BukkitUtils.getCompletions(args, (List)results);
    }
}
