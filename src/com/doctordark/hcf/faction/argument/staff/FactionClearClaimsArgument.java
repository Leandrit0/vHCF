package com.doctordark.hcf.faction.argument.staff;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import java.util.Iterator;
import org.bukkit.Bukkit;
import java.util.ArrayList;
import java.util.Collections;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Collection;

import org.bukkit.conversations.Conversable;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;
import org.bukkit.conversations.ConversationFactory;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionClearClaimsArgument extends CommandArgument
{
    private final ConversationFactory factory;
    private final HCF plugin;
    
    public FactionClearClaimsArgument(final HCF plugin) {
        super("clearclaims", "Clears the claims of a faction.");
        this.plugin = plugin;
        this.permission = "hcf.command.faction.argument." + this.getName();
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new ClaimClearAllPrompt(plugin)).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName|factionName|all>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        if (args[1].equalsIgnoreCase("all")) {
            if (!(sender instanceof ConsoleCommandSender)) {
                sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
                return true;
            }
            final Conversable conversable = (Conversable)sender;
            conversable.beginConversation(this.factory.buildConversation(conversable));
            return true;
        }
        else {
            final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
                return true;
            }
            if (faction instanceof ClaimableFaction) {
                final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                claimableFaction.removeClaims(claimableFaction.getClaims(), sender);
                if (claimableFaction instanceof PlayerFaction) {
                    ((PlayerFaction)claimableFaction).broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + "Your claims have been forcefully wiped by " + sender.getName() + '.');
                }
            }
            sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Claims belonging to " + faction.getName() + " have been forcefully wiped.");
            return true;
        }
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2 || !(sender instanceof Player)) {
            return Collections.<String>emptyList();
        }
        if (args[1].isEmpty()) {
            return null;
        }
        final Player player = (Player)sender;
        final List<String> results = new ArrayList<String>(this.plugin.getFactionManager().getFactionNameMap().keySet());
        for (final Player target : Bukkit.getOnlinePlayers()) {
            if (player.canSee(target) && !results.contains(target.getName())) {
                results.add(target.getName());
            }
        }
        return results;
    }
    
    private static class ClaimClearAllPrompt extends StringPrompt
    {
        private final HCF plugin;
        
        public ClaimClearAllPrompt(final HCF plugin) {
            this.plugin = plugin;
        }
        
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? " + ChatColor.RED + ChatColor.BOLD + "All claims" + ChatColor.YELLOW + " will be cleared. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }
        
        public Prompt acceptInput(final ConversationContext context, final String string) {
            final String lowerCase2;
            final String lowerCase = lowerCase2 = string.toLowerCase();
            switch (lowerCase2) {
                case "yes": {
                    for (final Faction faction : this.plugin.getFactionManager().getFactions()) {
                        if (faction instanceof ClaimableFaction) {
                            final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
                            claimableFaction.removeClaims(claimableFaction.getClaims(), (CommandSender)Bukkit.getConsoleSender());
                        }
                    }
                    final Conversable conversable = context.getForWhom();
                    Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "All claims have been cleared" + ((conversable instanceof CommandSender) ? (" by " + ((CommandSender)conversable).getName()) : "") + '.');
                    return Prompt.END_OF_CONVERSATION;
                }
                case "no": {
                    context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of clearing all faction claims.");
                    return Prompt.END_OF_CONVERSATION;
                }
                default: {
                    context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of clearing all faction claims cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }
            }
        }
    }
}
