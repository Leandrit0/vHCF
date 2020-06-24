package com.doctordark.hcf.timer.type;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.archer.ArcherClass;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.event.TimerExpireEvent;

import org.bukkit.event.EventHandler;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

public class ArcherTimer extends PlayerTimer implements Listener
{
    private final HCF plugin;
    
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }
    
    public ArcherTimer(final HCF plugin) {
        super("Archer Mark", TimeUnit.SECONDS.toMillis(7L));
        this.plugin = plugin;
    }
    
    public void run() {
    }
    
    @EventHandler
    public void onExpire(final TimerExpireEvent e) {
        if (e.getUserUUID().isPresent() && e.getTimer().equals(this)) {
            final UUID userUUID = (UUID)e.getUserUUID().get();
            final Player player = Bukkit.getPlayer(userUUID);
            if (player == null) {
                return;
            }
            Bukkit.getPlayer((UUID)ArcherClass.tagged.get(userUUID)).sendMessage(ChatColor.YELLOW + "Your archer mark on " + ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has expired.");
            player.sendMessage(ChatColor.YELLOW + "You're no longer archer marked.");
            ArcherClass.tagged.remove(player.getUniqueId());
           
        }
    }
    
    @EventHandler
    public void onHit(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player entity = (Player)e.getEntity();
            final Entity damager = e.getDamager();
            if (this.getRemaining(entity) > 0L) {
                final Double damage = e.getDamage() * 0.3;
                e.setDamage(e.getDamage() + damage);
            }
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
            final Player entity = (Player)e.getEntity();
            final Entity damager = (Entity)((Arrow)e.getDamager()).getShooter();
            if (damager instanceof Player && this.getRemaining(entity) > 0L) {
                if (ArcherClass.tagged.get(entity.getUniqueId()).equals(damager.getUniqueId())) {
                    this.setCooldown(entity, entity.getUniqueId());
                }
                final Double damage = e.getDamage() * 0.3;
                e.setDamage(e.getDamage() + damage);
            }
        }
    }
}
