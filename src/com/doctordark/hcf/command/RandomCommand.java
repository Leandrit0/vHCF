package com.doctordark.hcf.command;

import java.util.Iterator;
import org.bukkit.entity.Entity;
import java.util.Random;
import java.util.List;
import java.util.Collections;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class RandomCommand implements CommandExecutor
{
    private final HCF plugin;
    
    public RandomCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        final List<Player> players = new ArrayList<Player>();
        for (final Player players2 : Bukkit.getOnlinePlayers()) {
            players.add(players2);
        }
        Collections.shuffle(players);
        final Random random = new Random();
        final Integer randoms = random.nextInt(Bukkit.getOnlinePlayers().length) + 1;
        final Player p = players.get(randoms);
        if (player.canSee(p) && player.hasPermission(command.getPermission() + ".teleport")) {
            player.teleport((Entity)p);
            player.sendMessage(ChatColor.YELLOW + "You've teleported to " + p.getName());
        }
        else if (player.canSee(p)) {
            player.sendMessage(ChatColor.YELLOW + "You've found " + p.getName());
        } else if (Bukkit.getOnlinePlayers().length == 1) {
        	player.sendMessage(ChatColor.DARK_RED + "Not enough players.");
        }
        else {
            player.sendMessage(ChatColor.RED + "Player not found");
        }
        return true;
    }
}
