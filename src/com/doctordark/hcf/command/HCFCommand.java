package com.doctordark.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.doctordark.hcf.HCF;

public class HCFCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
       if (args.length < 1){
    	   sender.sendMessage(ChatColor.GREEN + "Usage /HCF savedata");
    	   return false;
       }
       if (!sender.hasPermission("hcf.admin")){
    	   sender.sendMessage(ChatColor.GREEN + "You don't have permission to execute this command.");
    	   return false;
       }
       if (args[0].equalsIgnoreCase("help")){
    	   sender.sendMessage(ChatColor.GREEN + "Usage /HCF savedata");
       }
       if (args[0].equalsIgnoreCase("savedata")){
    	   sender.sendMessage(ChatColor.GREEN + "Saving data wait a minute.");
    	   HCF.getPlugin().saveConfig();
    	   HCF.getPlugin().saveData();
       } else {
    	   sender.sendMessage(ChatColor.GREEN + "Usage /HCF help");
       }
		return false;
	}

}
