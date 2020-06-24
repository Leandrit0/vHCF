package com.doctordark.hcf.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;

public class CoordsCommand implements CommandExecutor {

	public List<String> coords = Color.color(HCF.getPlugin().getConfig().getStringList("coords"));
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        for (String c: coords){
        	sender.sendMessage(c);
        }
		return true;
	}

}
