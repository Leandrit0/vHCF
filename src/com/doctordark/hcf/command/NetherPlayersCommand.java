package com.doctordark.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class NetherPlayersCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)){
        	sender.sendMessage(ChatColor.GREEN + "You need be a player.");
        	return false;
        }
        if (!sender.hasPermission(HCF.getPlugin().getConfig().getString("command.netherplayers"))) {
        	sender.sendMessage(ChatColor.DARK_RED + "No permission.");
        }
        Player p = (Player)sender;
        int netherplayers = Bukkit.getWorld("world_nether").getPlayers().size();		
        for (String s : HCF.getPlugin().getConfig().getStringList("netherplayers-command")){
    	    p.sendMessage(Color.color(s.replace("%players%", String.valueOf(netherplayers))));
    	    }
        return true;
	}

}
