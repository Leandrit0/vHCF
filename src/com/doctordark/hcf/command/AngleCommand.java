package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.util.JavaUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class AngleCommand implements CommandExecutor, TabCompleter
{
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Location location = ((Player)sender).getLocation();
        sender.sendMessage(ChatColor.GOLD + JavaUtils.format((Number)location.getYaw()) + " yaw" + ChatColor.WHITE + ", " + ChatColor.GOLD + JavaUtils.format((Number)location.getPitch()) + " pitch");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
