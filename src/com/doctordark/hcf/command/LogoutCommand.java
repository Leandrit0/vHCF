package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.type.LogoutTimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class LogoutCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public LogoutCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final LogoutTimer logoutTimer = this.plugin.getTimerManager().logoutTimer;
        if (!logoutTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your " + logoutTimer.getDisplayName() + ChatColor.RED + " timer is already active.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Your " + logoutTimer.getDisplayName() + ChatColor.RED + " timer has started.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
