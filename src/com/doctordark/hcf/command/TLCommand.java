package com.doctordark.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.PlayerFaction;

public class TLCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
		    sender.sendMessage(ChatColor.BLUE + "Need be a player.");
			return false;
		}
		Player p = (Player)sender;
		PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(p);
		if (playerFaction == null) {
			p.sendMessage(ChatColor.RED + "You don't have a faction.");
			return false;
		}
		playerFaction.broadcast(Color.color(HCF.getPlugin().getConfig().getString("chat.tl").replace("%player%", p.getName()).replace("%location%", String.valueOf(p.getLocation()))));
		
		return true;
	}

}
