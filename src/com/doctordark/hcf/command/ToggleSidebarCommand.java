package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;


import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandExecutor;

public class ToggleSidebarCommand { /*implements CommandExecutor, TabExecutor

	
    private final HCF plugin;
    
    public ToggleSidebarCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final PlayerBoard playerBoard = this.plugin.getScoreboardHandler().getPlayerBoard(((Player)sender).getUniqueId());
        final boolean newVisibile = !playerBoard.isSidebarVisible();
        playerBoard.setSidebarVisible(newVisibile);
        sender.sendMessage(ChatColor.YELLOW + "Scoreboard sidebar is " + (newVisibile ? (ChatColor.GREEN + "now") : (ChatColor.RED + "no longer")) + ChatColor.YELLOW + " visible.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }*/
}
