package com.doctordark.hcf.command;

import net.md_5.bungee.api.ChatColor;
import java.util.concurrent.TimeUnit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.google.common.primitives.Ints;

import org.bukkit.command.CommandExecutor;

public class SOTWCommand implements CommandExecutor
{
    public boolean onCommand(final CommandSender cs, final Command cmd, final String s, final String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {           	
                HCF.getPlugin().getTimerManager().sotw.setRemaining(TimeUnit.MINUTES.toMillis(30L), true);
                HCF.getPlugin().getTimerManager().sotw.setPaused(false);
                cs.sendMessage(ChatColor.YELLOW + "SOTW started.");
            	
            }
            else if (args[0].equalsIgnoreCase("end")) {
                HCF.getPlugin().getTimerManager().sotw.clearCooldown();
                cs.sendMessage(ChatColor.YELLOW + "SOTW stopped.");
            }
            else if (args[0].equalsIgnoreCase("pause")) {
                HCF.getPlugin().getTimerManager().sotw.setPaused(true);
                cs.sendMessage(ChatColor.YELLOW + "SOTW paused.");
            }
            else {
                cs.sendMessage(ChatColor.RED + "I only know end, pause and start.");
            }
        } else if (args.length == 0) {
        	cs.sendMessage(ChatColor.RED + "I know end, pause and start.");
        }
        return false;
    }
}
