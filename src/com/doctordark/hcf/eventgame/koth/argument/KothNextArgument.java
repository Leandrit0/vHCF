package com.doctordark.hcf.eventgame.koth.argument;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.text.WordUtils;
import java.util.Locale;
import java.time.format.TextStyle;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;
import java.time.LocalDateTime;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.DateTimeFormats;
import com.doctordark.hcf.HCF;
import com.doctordark.util.command.CommandArgument;

public class KothNextArgument extends CommandArgument
{
    private final HCF plugin;
    
    public KothNextArgument(final HCF plugin) {
        super("next", "View the next scheduled KOTH");
        this.plugin = plugin;
        this.permission = "hcf.command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final long millis = System.currentTimeMillis();
        sender.sendMessage(ChatColor.GOLD + "The server time is currently " + ChatColor.YELLOW + DateTimeFormats.DAY_MTH_HR_MIN_AMPM.format(millis) + ChatColor.GOLD + '.');
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        if (scheduleMap.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There is not an event schedule for after now.");
            return true;
        }
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        for (final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if (now.isAfter(scheduleDateTime)) {
                continue;
            }
            final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            sender.sendMessage(ChatColor.DARK_AQUA + WordUtils.capitalizeFully((String)entry.getValue()) + ChatColor.GRAY + " is the next event: " + ChatColor.AQUA + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.DARK_AQUA + " (" + DateTimeFormats.HR_MIN_AMPM.format(TimeUnit.HOURS.toMillis(scheduleDateTime.getHour()) + TimeUnit.MINUTES.toMillis(scheduleDateTime.getMinute())) + ')');
            return true;
        }
        sender.sendMessage(ChatColor.RED + "There is not an event scheduled after now.");
        return true;
    }
}
