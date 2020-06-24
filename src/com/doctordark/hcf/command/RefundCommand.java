package com.doctordark.hcf.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.inventory.ItemStack;

import com.doctordark.hcf.listener.DeathListener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;

public class RefundCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        final String Usage = ChatColor.RED + "/" + s + " <playerName> <reason>";
        if (!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "You must be a player");
            return true;
        }
        final Player p = (Player)cs;
        if (args.length < 2) {
            cs.sendMessage(Usage);
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "Player must be online");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        if (DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())) {
            target.getInventory().setContents((ItemStack[])DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
            target.getInventory().setArmorContents((ItemStack[])DeathListener.PlayerArmorContents.get(target.getUniqueId()));
            final String reason = StringUtils.join((Object[])args, ' ', 2, args.length);
            Command.broadcastCommandMessage((CommandSender)p, ChatColor.YELLOW + "Refunded " + target.getName() + "'s items for: " + reason);
            DeathListener.PlayerArmorContents.remove(target.getUniqueId());
            DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
            return true;
        }
        p.sendMessage(ChatColor.RED + "Player was already refunded items");
        return false;
    }
}
