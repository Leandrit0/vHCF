package com.doctordark.hcf.deathban.lives.argument;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.util.command.CommandArgument;

public class LivesClearDeathbansArgument extends CommandArgument
{
    private final HCF plugin;
    
    public LivesClearDeathbansArgument(final HCF plugin) {
        super("cleardeathbans", "Clears the global deathbans");
        this.plugin = plugin;
        this.aliases = new String[] { "resetdeathbans" };
        this.permission = "hcf.command.lives.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (sender instanceof ConsoleCommandSender || (sender instanceof Player && sender.getName().equalsIgnoreCase("CommandoNanny"))) {
            for (final FactionUser user : this.plugin.getUserManager().getUsers().values()) {
                user.removeDeathban();
            }
            Command.broadcastCommandMessage(sender, ChatColor.YELLOW + "All death-bans have been cleared.");
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Must be console");
        return false;
    }
}
