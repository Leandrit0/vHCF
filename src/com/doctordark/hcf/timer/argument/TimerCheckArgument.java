package com.doctordark.hcf.timer.argument;

import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import java.util.UUID;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.UUIDFetcher;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.doctordark.util.command.CommandArgument;

public class TimerCheckArgument extends CommandArgument
{
    private final HCF plugin;
    
    public TimerCheckArgument(final HCF plugin) {
        super("check", "Check remaining timer time");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timerName> <playerName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        PlayerTimer temporaryTimer = null;
        for (final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer && timer.getName().equalsIgnoreCase(args[1])) {
                temporaryTimer = (PlayerTimer)timer;
                break;
            }
        }
        if (temporaryTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }
        final PlayerTimer playerTimer = temporaryTimer;
        new BukkitRunnable() {
            public void run() {
                UUID uuid;
                try {
                    uuid = UUIDFetcher.getUUIDOf(args[2]);
                }
                catch (Exception ex) {
                    sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[2] + ChatColor.GOLD + "' not found.");
                    return;
                }
                final long remaining = playerTimer.getRemaining(uuid);
                sender.sendMessage(ChatColor.YELLOW + args[2] + " has timer " + playerTimer.getName() + " for another " + DurationFormatUtils.formatDurationWords(remaining, true, true));
            }
        }.runTaskAsynchronously((Plugin)this.plugin);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.<String>emptyList();
    }
}
