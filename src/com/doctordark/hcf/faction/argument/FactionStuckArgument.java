package com.doctordark.hcf.faction.argument;

import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.type.StuckTimer;
import com.doctordark.util.command.CommandArgument;

public class FactionStuckArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionStuckArgument(final HCF plugin) {
        super("stuck", "Teleport to a safe position.", new String[] { "trap", "trapped" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final Player player = (Player)sender;
        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return true;
        }
        final StuckTimer stuckTimer = this.plugin.getTimerManager().stuckTimer;
        if (!stuckTimer.setCooldown(player, player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Your " + stuckTimer.getDisplayName() + ChatColor.RED + " timer is already active.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + stuckTimer.getDisplayName() + ChatColor.YELLOW + " timer has started. " + "Teleportation will commence in " + ChatColor.LIGHT_PURPLE + HCF.getRemaining(stuckTimer.getRemaining(player), true, false) + ChatColor.YELLOW + ". " + "This will cancel if you move more than " + 5 + " blocks.");
        return true;
    }
}
