package com.doctordark.hcf.deathban.lives.argument;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.doctordark.hcf.DateTimeFormats;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.deathban.Deathban;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.util.command.CommandArgument;
import com.google.common.base.Strings;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LivesCheckDeathbanArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesCheckDeathbanArgument(final HCF plugin) {
        super("checkdeathban", "Check the deathban cause of player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final Deathban deathban = this.plugin.getUserManager().getUser(target.getUniqueId()).getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Deathban cause of " + target.getName() + '.');
        sender.sendMessage(ChatColor.AQUA + " Time: " + DateTimeFormats.HR_MIN.format(deathban.getCreationMillis()));
        sender.sendMessage(ChatColor.AQUA + " Duration: " + DurationFormatUtils.formatDurationWords(deathban.getExpiryMillis() - deathban.getCreationMillis(), true, true));
        final Location location = deathban.getDeathPoint();
        if (location != null) {
            sender.sendMessage(ChatColor.AQUA + " Location: (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ") - " + location.getWorld().getName());
        }
        sender.sendMessage(ChatColor.AQUA + " Reason: " + Strings.nullToEmpty(deathban.getReason()));
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.<String>emptyList();
        }
        final List<String> results = new ArrayList<String>();
        for (final FactionUser factionUser : this.plugin.getUserManager().getUsers().values()) {
            final Deathban deathban = factionUser.getDeathban();
            if (deathban != null && deathban.isActive()) {
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                final String name = offlinePlayer.getName();
                if (name == null) {
                    continue;
                }
                results.add(name);
            }
        }
        return results;
    }
}
