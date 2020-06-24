package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.World;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.util.BukkitUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class HelpCommand implements CommandExecutor, TabCompleter
{
        List<String> help = Color.color(HCF.getPlugin().getConfig().getStringList("HELP"));
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
         
        for (String s : this.help) {
        	sender.sendMessage(s);
        }
         return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
}
