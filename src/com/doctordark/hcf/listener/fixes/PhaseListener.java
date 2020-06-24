package com.doctordark.hcf.listener.fixes;

import org.bukkit.entity.Player;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.doctordark.hcf.HCF;


import org.bukkit.GameMode;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.EventHandler;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.Listener;

public class PhaseListener implements Listener
{
    long gravityBlock;
    long utilityBlock;
    
    public PhaseListener() {
        this.gravityBlock = TimeUnit.HOURS.toMillis(6L);
        this.utilityBlock = TimeUnit.HOURS.toMillis(3L);
    }
    
    @EventHandler
    public void onMove(final PlayerInteractEvent e) {
        if (e.getPlayer().getLocation().getBlock() != null && e.getPlayer().getLocation().getBlock().getType() == Material.TRAP_DOOR && !HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation()).equals(HCF.getPlugin().getFactionManager().getPlayerFaction(e.getPlayer().getUniqueId()))) {
            e.getPlayer().sendMessage(ChatColor.RED + "Glitch detected. Now reporting, and fixing.");
            e.getPlayer().teleport(e.getPlayer().getLocation().add(0.0, 1.0, 0.0));
        }
    }
    
   
           
        
    
}
