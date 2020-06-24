package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.type.PvpProtectionTimer;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class PvpTimerCommand implements CommandExecutor, TabCompleter
{
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public PvpTimerCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PvpProtectionTimer pvpTimer = this.plugin.getTimerManager().pvpProtectionTimer;
        if (args.length < 1) {
            this.printUsage(sender, label, pvpTimer);
            return true;
        }
        if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("off")) {
            if (pvpTimer.getRemaining(player) > 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is now off.");
                pvpTimer.clearCooldown(player);
                return true;
            }
            if (pvpTimer.getLegible().remove(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "You will no longer be legible for your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " when you leave spawn.");
                return true;
            }
            sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
            return true;
        }
        else {
            if (!args[0].equalsIgnoreCase("remaining") && !args[0].equalsIgnoreCase("time") && !args[0].equalsIgnoreCase("left") && !args[0].equalsIgnoreCase("check")) {
                this.printUsage(sender, label, pvpTimer);
                return true;
            }
            final long remaining = pvpTimer.getRemaining(player);
            if (remaining <= 0L) {
                sender.sendMessage(ChatColor.RED + "Your " + pvpTimer.getDisplayName() + ChatColor.RED + " timer is currently not active.");
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Your " + pvpTimer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + (pvpTimer.isPaused(player) ? " and is currently paused" : "") + '.');
            return true;
        }
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)PvpTimerCommand.COMPLETIONS) : Collections.<String>emptyList();
    }
    
    private void printUsage(final CommandSender sender, final String label, final PvpProtectionTimer pvpTimer) {
        sender.sendMessage(ChatColor.AQUA + pvpTimer.getName() + " Help");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " enable - Removes your " + pvpTimer.getDisplayName() + ChatColor.GRAY + " timer.");
        sender.sendMessage(ChatColor.GRAY + "/" + label + " time - Check remaining " + pvpTimer.getDisplayName() + ChatColor.GRAY + " time.");
        sender.sendMessage(ChatColor.GRAY + "/lives - Life and deathban related commands.");
    }
    
    static {
        COMPLETIONS = ImmutableList.of("enable", "time");
    }
}
