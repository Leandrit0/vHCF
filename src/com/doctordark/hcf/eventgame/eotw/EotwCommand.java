package com.doctordark.hcf.eventgame.eotw;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.StringPrompt;
import java.util.Collections;
import java.util.List;
import org.bukkit.conversations.Conversable;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.plugin.Plugin;

import com.doctordark.hcf.HCF;

import org.bukkit.conversations.ConversationFactory;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class EotwCommand implements CommandExecutor, TabCompleter
{
    private final ConversationFactory factory;
    
    public EotwCommand(final HCF plugin) {
        this.factory = new ConversationFactory((Plugin)plugin).withFirstPrompt((Prompt)new EotwPrompt()).withEscapeSequence("/no").withTimeout(10).withModality(false).withLocalEcho(true);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "This command can be only executed from console.");
            return true;
        }
        final Conversable conversable = (Conversable)sender;
        conversable.beginConversation(this.factory.buildConversation(conversable));
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
    
    private static final class EotwPrompt extends StringPrompt
    {
        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "Are you sure you want to do this? The server will be in EOTW mode, If EOTW mode is active, all claims whilst making Spawn a KOTH. " + "You will still have " + EotwHandler.EOTW_WARMUP_WAIT_SECONDS + " seconds to cancel this using the same command though. " + "Type " + ChatColor.GREEN + "yes" + ChatColor.YELLOW + " to confirm or " + ChatColor.RED + "no" + ChatColor.YELLOW + " to deny.";
        }
        
        public Prompt acceptInput(final ConversationContext context, final String string) {
            if (string.equalsIgnoreCase("yes")) {
                final boolean newStatus = !HCF.getPlugin().getEotwHandler().isEndOfTheWorld(false);
                final Conversable conversable = context.getForWhom();
                if (conversable instanceof CommandSender) {
                    Command.broadcastCommandMessage((CommandSender)conversable, ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                }
                else {
                    conversable.sendRawMessage(ChatColor.GOLD + "Set EOTW mode to " + newStatus + '.');
                }
                HCF.getPlugin().getEotwHandler().setEndOfTheWorld(newStatus);
            }
            else if (string.equalsIgnoreCase("no")) {
                context.getForWhom().sendRawMessage(ChatColor.BLUE + "Cancelled the process of setting EOTW mode.");
            }
            else {
                context.getForWhom().sendRawMessage(ChatColor.RED + "Unrecognized response. Process of toggling EOTW mode has been cancelled.");
            }
            return Prompt.END_OF_CONVERSATION;
        }
    }
}
