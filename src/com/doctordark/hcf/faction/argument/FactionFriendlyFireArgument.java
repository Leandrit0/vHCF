package com.doctordark.hcf.faction.argument;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

import net.md_5.bungee.api.ChatColor;

public class FactionFriendlyFireArgument extends CommandArgument {

	public FactionFriendlyFireArgument(final HCF plugin) {
		super("friendlyfire", "toggle on/off the friendly fire.", new String[]{ "ff", "damage"  });
		this.plugin = plugin;
	}

	public static HCF plugin;
	@Override
	public String getUsage(final String label) { 
		return '/' + label + ' ';
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	   if (!(sender instanceof Player)){
		   sender.sendMessage(ChatColor.RED + "Need be a player");
		   return false;
	   }
	   final Player player = (Player)sender;
       final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
       if (playerFaction == null) {
           sender.sendMessage(ChatColor.RED + "You are not in a faction.");
           return true;
       }
	   if (playerFaction.getMember(player.getUniqueId()).getRole() == Role.MEMBER){
		   player.sendMessage(ChatColor.GREEN + "Need be captain or leader to execute this argument.");
	   }
        if (playerFaction.isFriendlyfire() == true){
        	playerFaction.setFriendlyfire(false);
        	playerFaction.broadcast(ChatColor.RED + player.getName() + " has toggled off the friendly fire.");
        	return true;
        }
        playerFaction.setFriendlyfire(true);
        playerFaction.broadcast(ChatColor.GREEN + player.getName() + " has toggled on the friendly fire.");;
        
	   return false;
	}



}
