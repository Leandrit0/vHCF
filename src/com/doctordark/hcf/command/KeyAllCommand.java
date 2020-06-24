package com.doctordark.hcf.command;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;

public class KeyAllCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender.hasPermission("hcf.admin"))){
			sender.sendMessage(ChatColor.GREEN + "No permission.");
			return false;
		} else {
			if (args[0].equalsIgnoreCase("start")){
				sender.sendMessage(ChatColor.GREEN + "Key All Starting");
				HCF.getPlugin().getTimerManager().keyall.setRemaining(TimeUnit.MINUTES.toMillis(60L), true);
			} else if (args[0].equalsIgnoreCase("stop")){
				sender.sendMessage(ChatColor.GREEN + "Key All stopped.");
				HCF.getPlugin().getTimerManager().keyall.setRemaining(TimeUnit.MINUTES.toMillis(0L), true);
			} 
		return false;
	}
  }
}
