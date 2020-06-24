package com.doctordark.hcf.listener;

import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.Listener;

public class ExpMultiplierListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onEntityDeath(final EntityDeathEvent event) {
        final double amount = event.getDroppedExp();
        final Player killer = event.getEntity().getKiller();
        if (killer != null && amount > 0.0) {
            final ItemStack stack = killer.getItemInHand();
            if (stack != null && stack.getType() != Material.AIR) {
                final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                if (enchantmentLevel > 0L) {
                    final double multiplier = enchantmentLevel * 1.5;
                    final int result = (int)Math.ceil(amount * multiplier);
                    event.setDroppedExp(result);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        final double amount = event.getExpToDrop();
        final Player player = event.getPlayer();
        final ItemStack stack = player.getItemInHand();
        if (stack != null && stack.getType() != Material.AIR && amount > 0.0) {
            final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            if (enchantmentLevel > 0) {
                final double multiplier = enchantmentLevel * 1.5;
                final int result = (int)Math.ceil(amount * multiplier);
                event.setExpToDrop(result);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerPickupExp(final PlayerExpChangeEvent event) {
        final double amount = event.getAmount();
        if (amount > 0.0) {
            final int result = (int)Math.ceil(amount * 2.0);
            event.setAmount(result);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFish(final PlayerFishEvent event) {
        double amount = event.getExpToDrop();
        if (amount > 0.0) {
            amount = Math.ceil(amount * 2.0);
            final ProjectileSource projectileSource = event.getHook().getShooter();
            if (projectileSource instanceof Player) {
                final ItemStack stack = ((Player)projectileSource).getItemInHand();
                final int enchantmentLevel = stack.getEnchantmentLevel(Enchantment.LUCK);
                if (enchantmentLevel > 0L) {
                    amount = Math.ceil(amount * (enchantmentLevel * 1.5));
                }
            }
            event.setExpToDrop((int)amount);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onFurnaceExtract(final FurnaceExtractEvent event) {
        final double amount = event.getExpToDrop();
        if (amount > 0.0) {
            final double multiplier = 2.0;
            final int result = (int)Math.ceil(amount * 2.0);
            event.setExpToDrop(result);
        }
    }
}
