package com.doctordark.hcf.command;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;



public class EndPlayersCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
	    if (!(sender instanceof Player)){
	    	sender.sendMessage(ChatColor.RED + "Not a player.");
	    }
	    if (!sender.hasPermission(HCF.getPlugin().getConfig().getString("command.endplayers"))) {
	    	sender.sendMessage(ChatColor.RED + "Not Permission.");
	    }
	    
	    Player p = (Player)sender;	    
	    int endplayers = Bukkit.getWorld("world_the_end").getPlayers().size();	
	    for (String s : HCF.getPlugin().getConfig().getStringList("endplayers-command")){
	    p.sendMessage(Color.color(s.replace("%players%", String.valueOf(endplayers))));
	    }
	    return true;	
	}

}
