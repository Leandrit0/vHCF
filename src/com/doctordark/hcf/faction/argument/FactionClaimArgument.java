package com.doctordark.hcf.faction.argument;

import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.ClaimHandler;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionClaimArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionClaimArgument(final HCF plugin) {
        super("claim", "Claim land in the Wilderness.", new String[] { "claimland" });
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
        final UUID uuid = player.getUniqueId();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        if (playerFaction.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You cannot claim land for your faction while raidable.");
            return true;
        }
        final PlayerInventory inventory = player.getInventory();
        if (inventory.contains(ClaimHandler.CLAIM_WAND)) {
            sender.sendMessage(ChatColor.RED + "You already have a claiming wand in your inventory.");
            return true;
        }
        if (!inventory.addItem(new ItemStack[] { ClaimHandler.CLAIM_WAND }).isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Your inventory is full.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "Claiming wand added to inventory, read the item to understand how to claim. You can also" + ChatColor.YELLOW + " use " + ChatColor.AQUA + '/' + label + " claimchunk" + ChatColor.YELLOW + '.');
        return true;
    }
}
