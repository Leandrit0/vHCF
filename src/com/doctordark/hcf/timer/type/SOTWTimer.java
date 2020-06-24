package com.doctordark.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.GlobalTimer;

import java.util.concurrent.TimeUnit;

public class SOTWTimer extends GlobalTimer
{
    public SOTWTimer() {
        super("SOTW", TimeUnit.HOURS.toMillis(4L));
    }
    
    public void run() {
        if (this.getRemaining() % 30L == 0L) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "SOTW will start in " + ChatColor.RED + HCF.getRemaining(this.getRemaining(), true));
        }
    }
    
    @Override
    public String getScoreboardPrefix() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD;
    }
}
