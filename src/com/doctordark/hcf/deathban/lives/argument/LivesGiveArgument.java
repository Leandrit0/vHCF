package com.doctordark.hcf.deathban.lives.argument;

import java.util.Collections;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.util.command.CommandArgument;

import net.minecraft.util.com.google.common.primitives.Ints;

public class LivesGiveArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesGiveArgument(final HCF plugin) {
        super("give", "Give lives to a player");
        this.plugin = plugin;
        this.aliases = new String[] { "transfer", "send", "pay", "add" };
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
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "The amount of lives must be positive.");
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final Player onlineTarget = target.getPlayer();
        if (sender instanceof Player) {
            final Player player = (Player)sender;
            final int ownedLives = this.plugin.getDeathbanManager().getLives(player.getUniqueId());
            if (amount > ownedLives) {
                sender.sendMessage(ChatColor.RED + "You tried to give " + target.getName() + ' ' + amount + " lives, but you only have " + ownedLives + '.');
                return true;
            }
            this.plugin.getDeathbanManager().takeLives(player.getUniqueId(), amount);
        }
        this.plugin.getDeathbanManager().addLives(target.getUniqueId(), amount);
        sender.sendMessage(ChatColor.YELLOW + "You have sent " + ChatColor.GOLD + target.getName() + ChatColor.YELLOW + ' ' + amount + ' ' + ((amount > 1) ? "life" : "lives") + '.');
        if (onlineTarget != null) {
            onlineTarget.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.YELLOW + " has sent you " + ChatColor.GOLD + amount + ' ' + ((amount > 1) ? "life" : "lives") + '.');
        }
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.<String>emptyList();
    }
}
