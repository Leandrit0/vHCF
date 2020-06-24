package com.doctordark.hcf.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.util.FancyMessage;
import com.doctordark.util.chat.ClickAction;
import com.doctordark.util.chat.Text;
import com.google.common.base.Joiner;

import net.md_5.bungee.api.chat.ClickEvent.Action;



public class HelpOpCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
       if (!(sender instanceof Player)){
    	   
    	   return false;
       }
       Player p = (Player)sender;
		if (args.length == 0){
			sender.sendMessage(ChatColor.GREEN + "Please give a message.");
        } else {
        	for (Player online : Bukkit.getOnlinePlayers()){
        		if (online.hasPermission("hcf.mod")){
        			for (String h : HCF.getPlugin().getConfig().getStringList("helpop_format")){
        		    String message = Joiner.on(' ').join(args); 
        				new Text(ChatColor.translateAlternateColorCodes('&', h.replace("%player%", p.getName()).replace("%message%", message))).setClick(ClickAction.RUN_COMMAND, "/tp " + p.getName()).send(online);;     
        				  
        			}
        		}
        	}
        }
		return false;
	}

}
