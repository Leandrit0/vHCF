package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.user.FactionUser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.command.CommandExecutor;

public class ToggleLightningCommand implements CommandExecutor, TabExecutor
{
    private final HCF plugin;
    
    public ToggleLightningCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final FactionUser factionUser = this.plugin.getUserManager().getUser(((Player)sender).getUniqueId());
        final boolean newShowLightning = !factionUser.isShowLightning();
        factionUser.setShowLightning(newShowLightning);
        sender.sendMessage(ChatColor.AQUA + "You will now " + (newShowLightning ? (ChatColor.GREEN + "able") : (ChatColor.RED + "unable")) + ChatColor.AQUA + " to see lightning strikes on death.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
