package com.doctordark.hcf.eventgame.koth.argument;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.eventgame.koth.KothExecutor;
import com.doctordark.util.command.CommandArgument;

public class KothHelpArgument extends CommandArgument
{
    private final KothExecutor kothExecutor;
    
    public KothHelpArgument(final KothExecutor kothExecutor) {
        super("help", "View help about how KOTH's work");
        this.kothExecutor = kothExecutor;
        this.permission = "hcf.command.koth.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(ChatColor.AQUA + "*** KotH Help ***");
        for (final CommandArgument argument : this.kothExecutor.getArguments()) {
            if (!argument.equals((Object)this)) {
                final String permission = argument.getPermission();
                if (permission != null && !sender.hasPermission(permission)) {
                    continue;
                }
                sender.sendMessage(ChatColor.GRAY + argument.getUsage(label) + " - " + argument.getDescription() + '.');
            }
        }
        sender.sendMessage(ChatColor.GRAY + "/fac show <kothName> - View information about a KOTH.");
        return true;
    }
}
