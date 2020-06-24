package com.doctordark.hcf.faction.argument;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.command.CommandArgument;
import com.google.common.collect.ImmutableList;

import net.minecraft.util.com.google.common.primitives.Ints;

public class FactionDepositArgument extends CommandArgument
{
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public FactionDepositArgument(final HCF plugin) {
        super("deposit", "Deposits money to the faction balance.", new String[] { "d" });
        this.plugin = plugin;
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <all|amount>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player)sender;
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        final UUID uuid = player.getUniqueId();
        final int playerBalance = this.plugin.getEconomyManager().getBalance(uuid);
        Integer amount;
        if (args[1].equalsIgnoreCase("all")) {
            amount = playerBalance;
        }
        else if ((amount = Ints.tryParse(args[1])) == null) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
            return true;
        }
        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "Amount must be positive.");
            return true;
        }
        if (playerBalance < amount) {
            sender.sendMessage(ChatColor.RED + "You need at least " + '$' + JavaUtils.format((Number)amount) + " to do this, you only have " + '$' + JavaUtils.format((Number)playerBalance) + '.');
            return true;
        }
        this.plugin.getEconomyManager().subtractBalance(uuid, amount);
        playerFaction.setBalance(playerFaction.getBalance() + amount);
        playerFaction.broadcast(Relation.MEMBER.toChatColour() + playerFaction.getMember(player).getRole().getAstrix() + sender.getName() + ChatColor.YELLOW + " has deposited " + ChatColor.GREEN + '$' + JavaUtils.format((Number)amount) + ChatColor.YELLOW + " into the faction balance.");
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (List<String>)((args.length == 2) ? FactionDepositArgument.COMPLETIONS : Collections.<Object>emptyList());
    }
    
    static {
        COMPLETIONS = ImmutableList.of("all");
    }
}
