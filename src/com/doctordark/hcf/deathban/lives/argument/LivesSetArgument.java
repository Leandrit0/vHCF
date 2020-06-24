package com.doctordark.hcf.deathban.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;

import com.doctordark.hcf.HCF;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.command.CommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.minecraft.util.com.google.common.primitives.Ints;

public class LivesSetArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesSetArgument(final HCF plugin) {
        super("set", "Set how much lives a player has");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName> <amount>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Integer amount = Ints.tryParse(args[2]);
        if (amount == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        final OfflinePlayer target = BukkitUtils.offlinePlayerWithNameOrUUID(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.YELLOW + args[1] + " is not founded.");
            return true;
        }
        this.plugin.getDeathbanManager().setLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + target.getName() + " now has " + ChatColor.GOLD + amount + ChatColor.YELLOW + " lives.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.<String>emptyList();
    }
}
