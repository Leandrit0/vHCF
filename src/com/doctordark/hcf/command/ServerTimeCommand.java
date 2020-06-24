package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.apache.commons.lang3.time.FastDateFormat;
import org.bukkit.command.TabCompleter;

import com.doctordark.hcf.ConfigurationService;

import org.bukkit.command.CommandExecutor;

public class ServerTimeCommand implements CommandExecutor, TabCompleter
{
    private static final FastDateFormat FORMAT;
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        sender.sendMessage(ChatColor.YELLOW + "The server time is " + ChatColor.AQUA + ServerTimeCommand.FORMAT.format(System.currentTimeMillis()) + ChatColor.AQUA + '.');
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
    
    static {
        FORMAT = FastDateFormat.getInstance("E MMM dd h:mm:ssa z yyyy", ConfigurationService.SERVER_TIME_ZONE);
    }
}
