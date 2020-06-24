package com.doctordark.hcf.faction.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionSetRallyArgument extends CommandArgument {
     public final HCF plugin;
	public FactionSetRallyArgument(HCF plugin) {
		super("setrally", "Set Rally Location.");
		this.plugin = plugin;
	
	}

	@Override
	public String getUsage(String label) {
		
		return '/' + label + ' ' + this.getName();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if (!(sender instanceof Player)) {
	            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
	            return true;
	        }

	        final Player player = (Player)sender;
	        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
	        if (playerFaction == null) {
	            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
	            return true;
	        }
	        final FactionMember factionMember = playerFaction.getMember(player);
	        if (factionMember.getRole() == Role.MEMBER) {
	            sender.sendMessage(ChatColor.RED + "You must be a faction officer to set the rally.");
	            return true;
	        }
			 if (args.length < 2) {
				 sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
				 return false;
			 } else {
				 Player named = Bukkit.getPlayer(args[1]);
				 PlayerFaction namedfaction = plugin.getFactionManager().getPlayerFaction(named);
				 if (namedfaction != null) {
				 plugin.getFactionManager().getPlayerFaction(player).setRally(namedfaction.getHome());
				 } else {
					 sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
				 }
				 }
		return false;
	}

}
