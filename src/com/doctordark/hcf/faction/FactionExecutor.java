package com.doctordark.hcf.faction;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
// this is better.
import com.doctordark.hcf.faction.argument.*;
import com.doctordark.hcf.faction.argument.staff.*;
import com.doctordark.util.command.ArgumentExecutor;
import com.doctordark.util.command.CommandArgument;

public class FactionExecutor extends ArgumentExecutor
{
    private final CommandArgument helpArgument;
    
    public FactionExecutor(final HCF plugin) {
        super("faction");
        this.addArgument((CommandArgument)new FactionSetRallyArgument(plugin));
        this.addArgument((CommandArgument)new FactionTopArgument(plugin));
        this.addArgument((CommandArgument)new FactionAcceptArgument(plugin));
        this.addArgument((CommandArgument)new FactionAllyArgument(plugin));
        this.addArgument((CommandArgument)new FactionChatArgument(plugin));
        
        this.addArgument((CommandArgument)new FactionClaimArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimChunkArgument(plugin));

        this.addArgument((CommandArgument)new FactionClaimsArgument(plugin));
       
        this.addArgument((CommandArgument)new FactionCreateArgument(plugin));
        this.addArgument((CommandArgument)new FactionDemoteArgument(plugin));
        this.addArgument((CommandArgument)new FactionDepositArgument(plugin));
        this.addArgument((CommandArgument)new FactionDisbandArgument(plugin));
        // Staff Commands Start.
        this.addArgument((CommandArgument)new FactionSystemCreateArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDtrArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetPointsArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDeathbanMultiplierArgument(plugin));
        this.addArgument((CommandArgument)new FactionClearClaimsArgument(plugin));
        this.addArgument((CommandArgument)new FactionClaimForArgument(plugin));
        this.addArgument((CommandArgument)new FactionChatSpyArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetDtrRegenArgument(plugin));
        // Staff Commands end.
        this.addArgument((CommandArgument)new FactionForceUnclaimHereArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceJoinArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceKickArgument(plugin));
        this.addArgument((CommandArgument)new FactionForceLeaderArgument(plugin));
        this.addArgument((CommandArgument)new FactionForcePromoteArgument(plugin));
        this.addArgument(this.helpArgument = new FactionHelpArgument(this));
        this.addArgument((CommandArgument)new FactionHomeArgument(this, plugin));
        this.addArgument((CommandArgument)new FactionInviteArgument(plugin));
        this.addArgument((CommandArgument)new FactionInvitesArgument(plugin));
        this.addArgument((CommandArgument)new FactionKickArgument(plugin));
        this.addArgument((CommandArgument)new FactionLeaderArgument(plugin));
        this.addArgument((CommandArgument)new FactionLeaveArgument(plugin));
        this.addArgument((CommandArgument)new FactionListArgument(plugin));
        this.addArgument((CommandArgument)new FactionMapArgument(plugin));
        this.addArgument((CommandArgument)new FactionMessageArgument(plugin));
        this.addArgument((CommandArgument)new FactionOpenArgument(plugin));
        this.addArgument((CommandArgument)new FactionRemoveArgument(plugin));
        this.addArgument((CommandArgument)new FactionRenameArgument(plugin));
        this.addArgument((CommandArgument)new FactionPromoteArgument(plugin));
        this.addArgument((CommandArgument)new FactionSetHomeArgument(plugin));
        this.addArgument((CommandArgument)new FactionShowArgument(plugin));
        this.addArgument((CommandArgument)new FactionStuckArgument(plugin));
        this.addArgument((CommandArgument)new FactionUnclaimArgument(plugin));
        this.addArgument((CommandArgument)new FactionUnallyArgument(plugin));
        this.addArgument((CommandArgument)new FactionUninviteArgument(plugin));
        this.addArgument((CommandArgument)new FactionWithdrawArgument(plugin));
        this.addArgument((CommandArgument)new FactionFriendlyFireArgument(plugin));
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }

        final CommandArgument argument = this.getArgument(args[0]);
        final String permission = (argument == null) ? null : argument.getPermission();
        if (argument != null) {

            if (permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }

        if (argument == null || (permission != null && !sender.hasPermission(permission))) {
          sender.sendMessage(new String[] { ChatColor.AQUA + "Faction sub-command " + ChatColor.YELLOW + args[0] + ChatColor.AQUA + " not found.", ChatColor.AQUA + "Use " + ChatColor.YELLOW + '/' + label + ChatColor.AQUA + " for more information about factions." });
          return true;
        }  
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }
}
