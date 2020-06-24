package com.doctordark.hcf.eventgame.koth.argument;

import java.util.Iterator;
import java.util.List;

import com.doctordark.hcf.DateTimeFormats;
import com.doctordark.hcf.HCF;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;

import org.apache.commons.lang3.time.DurationFormatUtils;
import java.time.temporal.TemporalUnit;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import java.util.Locale;
import java.time.format.TextStyle;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDateTime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.time.format.DateTimeFormatter;

public class KothScheduleArgument extends CommandArgument
{
    private static final String TIME_UNTIL_PATTERN = "d'd' H'h' mm'm'";
    private static final DateTimeFormatter HHMMA;
    private final HCF plugin;
    
    public KothScheduleArgument(final HCF plugin) {
        super("schedule", "View the schedule for KOTH arenas");
        this.plugin = plugin;
        this.aliases = new String[] { "info", "i", "time" };
        this.permission = "hcf.command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);
        final int currentDay = now.getDayOfYear();
        final Map<LocalDateTime, String> scheduleMap = this.plugin.eventScheduler.getScheduleMap();
        final List<String> shownEvents = new ArrayList<String>();
        for (final Map.Entry<LocalDateTime, String> entry : scheduleMap.entrySet()) {
            final LocalDateTime scheduleDateTime = entry.getKey();
            if (scheduleDateTime.isAfter(now)) {
                final int dayDifference = scheduleDateTime.getDayOfYear() - currentDay;
                if (dayDifference > 1) {
                    continue;
                }
                final String eventName = entry.getValue();
                final String monthName = scheduleDateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                final String weekName = scheduleDateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                final ChatColor colour = (dayDifference == 0) ? ChatColor.GREEN : ChatColor.AQUA;
                shownEvents.add("  " + colour + WordUtils.capitalizeFully(eventName) + ": " + ChatColor.YELLOW + weekName + ' ' + scheduleDateTime.getDayOfMonth() + ' ' + monthName + ChatColor.RED + " (" + KothScheduleArgument.HHMMA.format(scheduleDateTime) + ')' + ChatColor.GRAY + " - " + ChatColor.GOLD + DurationFormatUtils.formatDuration(now.until(scheduleDateTime, ChronoUnit.MILLIS), "d'd' H'h' mm'm'"));
            }
        }
        if (shownEvents.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are no event schedules defined.");
            return true;
        }
        final String monthName2 = WordUtils.capitalizeFully(now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        final String weekName2 = WordUtils.capitalizeFully(now.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(ChatColor.GRAY + "Server time is currently " + ChatColor.WHITE + weekName2 + ' ' + now.getDayOfMonth() + ' ' + monthName2 + ' ' + KothScheduleArgument.HHMMA.format(now) + ChatColor.GRAY + '.');
        sender.sendMessage((String[])shownEvents.<String>toArray(new String[shownEvents.size()]));
        sender.sendMessage(ChatColor.GRAY + "For more info about King of the Hill, use " + ChatColor.WHITE + '/' + label + " help" + ChatColor.GRAY + '.');
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        return true;
    }
    
    static {
        HHMMA = DateTimeFormatter.ofPattern("h:mma");
    }
}
