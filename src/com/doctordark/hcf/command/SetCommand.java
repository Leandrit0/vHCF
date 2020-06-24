package com.doctordark.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;

public class SetCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
         if (args.length < 1){
        	 for (String s : HCF.getPlugin().getConfig().getStringList("set.command")){
        		 sender.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        	 }
        	 return false;
         }
         if (!sender.hasPermission(HCF.getPlugin().getConfig().getString("command.set"))) {
         	sender.sendMessage(ChatColor.DARK_RED + "No permission.");
         }
        	
         if (!(sender instanceof Player)){
        	 sender.sendMessage("Need be a player.");
        	 return false;
         } else {
        	 Player p = (Player)sender;
        	 double x = p.getLocation().getX();
        	 double y = p.getLocation().getY();
        	 double z = p.getLocation().getZ();
        	 HCF.getPlugin().getConfig().set("end-exit.x", x);
        	 HCF.getPlugin().getConfig().set("end-exit.y", y);
        	 HCF.getPlugin().getConfig().set("end-exit.z", z);
        	 HCF.getPlugin().saveConfig();
        	 p.sendMessage(ChatColor.GREEN + "Succesfully set end-exit.");
         }
         
		return false;
	}

}
