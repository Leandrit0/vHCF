package com.doctordark.hcf.eventgame.koth.argument;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.faction.KothFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

public class KothCreateArgument extends CommandArgument {
    public  HCF plugin;
	public KothCreateArgument(final HCF plugin) {		
		super("create", "define a new koth.");
		 this.plugin = plugin;
	     this.permission = "hcf.command.event.argument." + this.getName();	
	}

	@Override
	public String getUsage(String label) {
		
		return "/koth " + label + " <kothname>";
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if (args.length < 2) {
	            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
	            return true;
	        }
		 Faction faction = this.plugin.getFactionManager().getFaction(args[1]);
	        if (faction != null) {
	            sender.sendMessage(ChatColor.RED + "There is already a faction named " + args[1] + '.');
	            return true;
	        }
	      
	        faction = new KothFaction(args[1]);
	        this.plugin.getFactionManager().createFaction(faction, sender);
	        sender.sendMessage(ChatColor.YELLOW + "Created event faction " + ChatColor.WHITE + faction.getDisplayName(sender) + ChatColor.YELLOW + " with type " + WordUtils.capitalizeFully(args[2]) + '.');
		return false;
	}

}
