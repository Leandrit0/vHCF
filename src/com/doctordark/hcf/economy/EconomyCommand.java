package com.doctordark.hcf.economy;

import java.util.UUID;

import net.minecraft.util.com.google.common.primitives.Ints;


import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.JavaUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandExecutor;

public class EconomyCommand implements CommandExecutor
{
    private static final int MAX_ENTRIES = 10;
    private static final ImmutableList<String> COMPLETIONS_SECOND;
    private final HCF plugin;
    
    public EconomyCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final boolean hasStaffPermission = sender.hasPermission(command.getPermission() + ".staff");
        OfflinePlayer target;
        if (args.length > 0 && hasStaffPermission) {
            target = BukkitUtils.offlinePlayerWithNameOrUUID(args[0]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <playerName>");
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(String.format(ChatColor.YELLOW + args[0] + "not founded."));
            return true;
        }
        final UUID uuid = target.getUniqueId();
        final int balance = this.plugin.getEconomyManager().getBalance(uuid);
        if (args.length < 2 || !hasStaffPermission) {
            sender.sendMessage(ChatColor.YELLOW + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.GREEN + '$' + balance + ChatColor.GOLD + '.');
            return true;
        }
        if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().addBalance(uuid, amount);
            sender.sendMessage(new String[] { ChatColor.YELLOW + "Added " + '$' + JavaUtils.format((Number)amount) + " to balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + '$' + newBalance + '.' });
            return true;
        }
        else if (args[1].equalsIgnoreCase("take") || args[1].equalsIgnoreCase("negate") || args[1].equalsIgnoreCase("minus") || args[1].equalsIgnoreCase("subtract")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().subtractBalance(uuid, amount);
            sender.sendMessage(new String[] { ChatColor.YELLOW + "Taken " + '$' + JavaUtils.format((Number)amount) + " from balance of " + target.getName() + '.', ChatColor.YELLOW + "Balance of " + target.getName() + " is now " + '$' + newBalance + '.' });
            return true;
        }
        else {
            if (!args[1].equalsIgnoreCase("set")) {
                sender.sendMessage(ChatColor.GOLD + (sender.equals(target) ? "Your balance" : ("Balance of " + target.getName())) + " is " + ChatColor.WHITE + '$' + balance + ChatColor.GOLD + '.');
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + target.getName() + ' ' + args[1] + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[2]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
                return true;
            }
            final int newBalance = this.plugin.getEconomyManager().setBalance(uuid, amount);
            sender.sendMessage(ChatColor.YELLOW + "Set balance of " + target.getName() + " to " + '$' + JavaUtils.format((Number)newBalance) + '.');
            return true;
        }
    }
    
    static {
        COMPLETIONS_SECOND = ImmutableList.of("add", "set", "take");
    }
}
