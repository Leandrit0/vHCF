package com.doctordark.hcf.deathban.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.util.command.CommandArgument;

public class LivesCheckArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesCheckArgument(final HCF plugin) {
        super("check", "Check how much lives a player has");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " [playerName]";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        OfflinePlayer target;
        if (args.length > 1) {
            target = Bukkit.getOfflinePlayer(args[1]);
        }
        else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
                return true;
            }
            target = (OfflinePlayer)sender;
        }
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final int targetLives = this.plugin.getDeathbanManager().getLives(target.getUniqueId());
        sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.YELLOW + " has " + ChatColor.LIGHT_PURPLE + targetLives + ChatColor.YELLOW + ' ' + ((targetLives == 1) ? "life" : "lives") + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.<String>emptyList();
    }
}
