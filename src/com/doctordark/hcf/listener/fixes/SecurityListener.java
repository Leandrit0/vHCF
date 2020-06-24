package com.doctordark.hcf.listener.fixes;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.doctordark.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class SecurityListener implements CommandExecutor, Listener {
     private List<String> protection;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	   Player p = (Player)sender;
	   Player target = Bukkit.getPlayer(args[1]);
	   if (!p.isOp()){
		   p.sendMessage(ChatColor.AQUA + "need be have op");
		   return false;
	   }
	   if (args.length == 0 || args[0] == null){
		   sender.sendMessage( ChatColor.translateAlternateColorCodes('&', "&a/protection add <player>"));
		   sender.sendMessage( ChatColor.translateAlternateColorCodes('&', "&a/protection remove <player>"));
	   }
	  
	   if (args[0].equalsIgnoreCase("add")) {
		   if (args[1] == null){
			   p.sendMessage(ChatColor.AQUA + "Please add a name");
			   return false;
		   }
		   p.sendMessage(ChatColor.GREEN + "added " + target.getName() + " to allowed ops.");
		   HCF.getPlugin().getConfig().getStringList("protection.alloweds").add(target.getName());
	       HCF.getPlugin().saveConfig();
	       return true;
	   }
	   if (args[0].equalsIgnoreCase("remove")){
		   if (args[1] == null){
			   p.sendMessage(ChatColor.AQUA + "Please add a name");
			   return false;
		   }
		   p.sendMessage(ChatColor.GREEN + "Removed " + target.getName() + " to allowed ops.");
		   HCF.getPlugin().getConfig().getStringList("protection.alloweds").remove(target.getName());
		   HCF.getPlugin().saveConfig();
		   return true;
	   }
	   
		return false;
	}
	
	public void security(PlayerJoinEvent e){
		Player p = e.getPlayer();
	List<String> alloweds = HCF.getPlugin().getConfig().getStringList("protection.alloweds");
	if (p.isOp() || !alloweds.contains(p.getName())){
		p.setOp(false);
		p.kickPlayer(ChatColor.GREEN + "Please add in console or contact a staff to add in the allowed op list.");
	    for (Player staff : Bukkit.getOnlinePlayers()){
	    	if (staff.hasPermission("staff.alerts")){
	    		staff.sendMessage(ChatColor.GREEN + p.getName() + " has removed from the server because have op and is not allowed to that.");
	    	}
	    	
	    }
	}
	}

}
