package com.doctordark.hcf.timer.type;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.timer.PlayerTimer;

import org.bukkit.event.Listener;

public class NotchAppleTimer extends PlayerTimer implements Listener
{
    public NotchAppleTimer(final JavaPlugin plugin) {
        super("Gopple", TimeUnit.HOURS.toMillis(6L));
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerConsume(final PlayerItemConsumeEvent event) {
        final ItemStack stack = event.getItem();
        if (stack != null && stack.getType() == Material.GOLDEN_APPLE && stack.getDurability() == 1) {
            final Player player = event.getPlayer();
            if (!this.setCooldown(player, player.getUniqueId(), this.defaultCooldown, false)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You still have a " + this.getDisplayName() + ChatColor.RED + " cooldown for another " + ChatColor.BOLD + HCF.getRemaining(this.getRemaining(player), true, false) + ChatColor.RED + '.');
            }
        }
    }
}
