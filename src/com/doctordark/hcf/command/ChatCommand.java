package com.doctordark.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.listener.ChatListener;

public class ChatCommand implements CommandExecutor {
	static HCF plugin;
    public static ChatListener chat  = new ChatListener(plugin);
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	    if (args.length < 1) {
	    	sender.sendMessage(ChatColor.GREEN + "Usage /chat clear");
	    	sender.sendMessage(ChatColor.GREEN + "Usage /chat mute");
	    	return false;
	    }
		if (!(sender.hasPermission(HCF.getPlugin().getConfig().getString("command.chat")))){
	    	sender.sendMessage(ChatColor.GREEN + "You don't have permission to execute this command.");
	    	return false;
	    }
		if (args[0].equalsIgnoreCase("mute")) {
			if (chat.isMute() == true) {
				sender.sendMessage(ChatColor.YELLOW + "Now the chat is on.");
			    chat.setMute(false);
			    for (Player online : Bukkit.getOnlinePlayers()) {
			    	online.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("chat.muteoff")));
			    }
			} else if (chat.isMute() == false) {
				 sender.sendMessage(ChatColor.YELLOW + "Now the chat is off.");
				 chat.setMute(true);
				 for (Player online : Bukkit.getOnlinePlayers()) {
				    	online.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("chat.muteon")));
				    }
			}
		}
		if (args[0].equalsIgnoreCase("clear")){
			for(Player online : Bukkit.getOnlinePlayers()){
				if (!online.hasPermission("chat.bypass.clear")){
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage("    ");
					online.sendMessage(ChatColor.GREEN + "Chat was cleared by " + sender.getName());
					
				}
				sender.sendMessage(ChatColor.GREEN + "Chat was cleared.");
			}
			return false;
		}

	    
		return false;
	}

}
