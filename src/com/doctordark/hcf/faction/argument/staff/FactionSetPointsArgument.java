package com.doctordark.hcf.faction.argument.staff;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;

public class FactionSetPointsArgument extends CommandArgument {
 
	public FactionSetPointsArgument(final HCF plugin) {
        super("setpoints", "Sets the points of a faction", new String[] { "setfactionpoints" });
        this.permission = "*";
    }
    
    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Color.color("&cCorrect Usage: /f setpoints <factionName> <points>."));
            return true;
        }
        final int newPoints = Integer.valueOf(args[2]);
        if (newPoints < 0) {
            sender.sendMessage(Color.color("&cInvalid number"));
            return true;
        }
        final Faction faction = HCF.getPlugin().getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(Color.color("&cFaction not found."));
            return true;
        }
        if (!(faction instanceof PlayerFaction)) {
            sender.sendMessage(Color.color("&cThis type of faction does not use points."));
            return true;
        }
        final PlayerFaction playerFaction = (PlayerFaction)faction;
        final int previousPoints = playerFaction.getPoints();
        playerFaction.setPoints(newPoints);
        sender.sendMessage(Color.color("&esuccessfully set " + newPoints + " &afrom " + previousPoints + " &eto the faction &f" + faction.getName() + "&e."));
        return true;
    }

	@Override
	public String getUsage(String p0) {

		return null;
	}
	
}
