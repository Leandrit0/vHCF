package com.doctordark.hcf.deathban.lives.argument;

import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesSetDeathbanTimeArgument extends CommandArgument
{
    public LivesSetDeathbanTimeArgument() {
        super("setdeathbantime", "Sets the base deathban time");
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <time>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final long duration = JavaUtils.parse(args[1]);
        if (duration == -1L) {
            sender.sendMessage(ChatColor.RED + "Invalid duration, use the correct format: 10m 1s");
            return true;
        }
        ConfigurationService.DEFAULT_DEATHBAN_DURATION = duration;
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Base death-ban time set to " + DurationFormatUtils.formatDurationWords(duration, true, true) + " (not including multipliers, etc).");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
