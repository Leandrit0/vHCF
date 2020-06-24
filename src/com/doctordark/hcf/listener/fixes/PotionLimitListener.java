package com.doctordark.hcf.listener.fixes;

import org.bukkit.potion.Potion;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.potion.PotionType;

import com.doctordark.hcf.ConfigurationService;

import org.bukkit.event.Listener;

public class PotionLimitListener implements Listener
{
    private static final int EMPTY_BREW_TIME = 400;
    
    public int getMaxLevel(final PotionType type) {
        return ConfigurationService.POTION_LIMITS.getOrDefault(type, type.getMaxLevel());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(final BrewEvent event) {
        for (final ItemStack stack : event.getContents().getContents()) {
            if (stack.getType() == Material.FERMENTED_SPIDER_EYE) {
                event.setCancelled(true);
            }
        }
        if (!this.testValidity(event.getResults())) {
            event.setCancelled(true);
        }
    }
    
    private boolean testValidity(final ItemStack[] contents) {
        for (final ItemStack stack : contents) {
            if (stack != null && stack.getType() == Material.POTION && stack.getDurability() != 0) {
                final Potion potion = Potion.fromItemStack(stack);
                if (potion != null) {
                    final PotionType type = potion.getType();
                    if (type != null) {
                        if ((type != PotionType.POISON || potion.hasExtendedDuration() || potion.getLevel() != 1) && potion.getLevel() > this.getMaxLevel(type)) {
                            return false;
                        }
                        if (type == PotionType.POISON && potion.hasExtendedDuration()) {
                            return false;
                        }
                        if (type == PotionType.SLOWNESS && potion.hasExtendedDuration()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
