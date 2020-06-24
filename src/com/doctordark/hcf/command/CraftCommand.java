package com.doctordark.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;

public class CraftCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
        	
        	return false;
        }
        Player p = (Player)sender;
        if (!(p.hasPermission(HCF.getPlugin().getConfig().getString("command.craft")))) {
        	p.sendMessage(ChatColor.BLUE + "No Permission.");
        	return false;
        } else {
        	p.openWorkbench(p.getLocation(), true);
        }
		return false;
	}

}
