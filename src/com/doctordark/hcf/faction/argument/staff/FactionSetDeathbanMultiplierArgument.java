package com.doctordark.hcf.faction.argument.staff;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

import net.minecraft.util.com.google.common.primitives.Doubles;

public class FactionSetDeathbanMultiplierArgument extends CommandArgument
{
    private static final double MIN_MULTIPLIER = 0.0;
    private static final double MAX_MULTIPLIER = 5.0;
    private final HCF plugin;
    
    public FactionSetDeathbanMultiplierArgument(final HCF plugin) {
        super("setdeathbanmultiplier", "Sets the deathban multiplier of a faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName|factionName> <newMultiplier>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if (faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        final Double multiplier = Doubles.tryParse(args[2]);
        if (multiplier == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid number.");
            return true;
        }
        if (multiplier < 0.0) {
            sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be less than " + 0.0 + '.');
            return true;
        }
        if (multiplier > 5.0) {
            sender.sendMessage(ChatColor.RED + "Deathban multipliers may not be more than " + 5.0 + '.');
            return true;
        }
        final double previousMultiplier = faction.getDeathbanMultiplier();
        faction.setDeathbanMultiplier(multiplier);
        Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "Set deathban multiplier of " + faction.getName() + " from " + previousMultiplier + " to " + multiplier + '.');
        return true;
    }
}
