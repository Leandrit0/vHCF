package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.PlayerTimer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class GoppleCommand implements CommandExecutor, TabCompleter
{
    private final HCF plugin;
    
    public GoppleCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final PlayerTimer timer = this.plugin.getTimerManager().notchAppleTimer;
        final long remaining = timer.getRemaining(player);
        if (remaining <= 0L) {
            sender.sendMessage(ChatColor.RED + "Your " + timer.getDisplayName() + ChatColor.RED + " timer is no longer active.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Your " + timer.getDisplayName() + ChatColor.YELLOW + " timer is active for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.YELLOW + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
