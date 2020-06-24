package com.doctordark.hcf.timer.argument;

import java.util.Collections;
import java.util.ArrayList;
import javax.annotation.Nullable;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.util.List;
import org.bukkit.OfflinePlayer;
import java.util.Iterator;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class TimerSetArgument extends CommandArgument
{
    private static final Pattern WHITESPACE_TRIMMER;
    private final HCF plugin;
    
    public TimerSetArgument(final HCF plugin) {
        super("set", "Set remaining timer time");
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <timerName> <all|playerName> <remaining>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final long duration = JavaUtils.parse(args[3]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        PlayerTimer playerTimer = null;
        for (final Timer timer : this.plugin.getTimerManager().getTimers()) {
            if (timer instanceof PlayerTimer && TimerSetArgument.WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("").equalsIgnoreCase(args[1])) {
                playerTimer = (PlayerTimer)timer;
                break;
            }
        }
        if (playerTimer == null) {
            sender.sendMessage(ChatColor.RED + "Timer '" + args[1] + "' not found.");
            return true;
        }
        if (args[2].equalsIgnoreCase("all")) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                playerTimer.setCooldown(player, player.getUniqueId(), duration, true);
            }
            sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + " for all to " + DurationFormatUtils.formatDurationWords(duration, true, true) + '.');
        }
        else {
            final OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            Player targetPlayer = null;
            if (target == null || (sender instanceof Player && (targetPlayer = target.getPlayer()) != null && !((Player)sender).canSee(targetPlayer))) {
                sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
                return true;
            }
            playerTimer.setCooldown(targetPlayer, target.getUniqueId(), duration, true);
            sender.sendMessage(ChatColor.BLUE + "Set timer " + playerTimer.getName() + " duration to " + DurationFormatUtils.formatDurationWords(duration, true, true) + " for " + target.getName() + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 2) {
            return (List<String>)FluentIterable.from((Iterable)this.plugin.getTimerManager().getTimers()).filter((Predicate)new Predicate<Timer>() {
                public boolean apply(final Timer timer) {
                    return timer instanceof PlayerTimer;
                }
            }).transform((Function)new Function<Timer, String>() {
                @Nullable
                public String apply(final Timer timer) {
                    return TimerSetArgument.WHITESPACE_TRIMMER.matcher(timer.getName()).replaceAll("");
                }
            }).toList();
        }
        if (args.length == 3) {
            final List<String> list = new ArrayList<String>();
            list.add("ALL");
            final Player player = (Player)sender;
            for (final Player target : Bukkit.getOnlinePlayers()) {
                if (player == null || player.canSee(target)) {
                    list.add(target.getName());
                }
            }
            return list;
        }
        return Collections.<String>emptyList();
    }
    
    static {
        WHITESPACE_TRIMMER = Pattern.compile("\\s");
    }
}
