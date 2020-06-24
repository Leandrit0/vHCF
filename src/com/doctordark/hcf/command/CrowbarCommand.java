package com.doctordark.hcf.command;

import java.util.Collections;

import net.minecraft.util.com.google.common.primitives.Ints;

import com.doctordark.hcf.api.Crowbar;
import com.doctordark.util.BukkitUtils;
import com.google.common.base.Optional;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class CrowbarCommand implements CommandExecutor, TabCompleter
{
    private final List<String> completions;
    
    public CrowbarCommand() {
        this.completions = Arrays.<String>asList("spawn", "setspawners", "setendframes");
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (!sender.hasPermission("")){
    		sender.sendMessage(ChatColor.GREEN + "No Permission.");
    		return false;
    	}
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
            return true;
        }
        final Player player = (Player)sender;
        if (args[0].equalsIgnoreCase("spawn")) {
        	
            final ItemStack stack = new Crowbar().getItemIfPresent();
            player.getInventory().addItem(new ItemStack[] { stack });
            sender.sendMessage(ChatColor.YELLOW + "You have given yourself a " + stack.getItemMeta().getDisplayName() + ChatColor.YELLOW + '.');
            return true;
        }
        final Optional<Crowbar> crowbarOptional = Crowbar.fromStack(player.getItemInHand());
        if (!crowbarOptional.isPresent()) {
            sender.sendMessage(ChatColor.RED + "You are not holding a Crowbar.");
            return true;
        }
        if (args[0].equalsIgnoreCase("setspawners")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set Spawner uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 1) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum Spawner uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar)crowbarOptional.get();
            crowbar.setSpawnerUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set Spawner uses of held Crowbar to " + amount + '.');
            return true;
        }
        else {
            if (!args[0].equalsIgnoreCase("setendframes")) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <spawn|setspawners|setendframes>");
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <amount>");
                return true;
            }
            final Integer amount = Ints.tryParse(args[1]);
            if (amount == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a number.");
                return true;
            }
            if (amount < 0) {
                sender.sendMessage(ChatColor.RED + "You cannot set End Frame uses to an amount less than " + 0 + '.');
                return true;
            }
            if (amount > 5) {
                sender.sendMessage(ChatColor.RED + "Crowbars have maximum End Frame uses of " + 1 + '.');
                return true;
            }
            final Crowbar crowbar = (Crowbar)crowbarOptional.get();
            crowbar.setEndFrameUses(amount);
            player.setItemInHand(crowbar.getItemIfPresent());
            sender.sendMessage(ChatColor.YELLOW + "Set End Frame uses of held Crowbar to " + amount + '.');
            return true;
        }
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)this.completions) : Collections.<String>emptyList();
    }
}
