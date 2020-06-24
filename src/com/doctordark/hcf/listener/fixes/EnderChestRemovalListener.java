package com.doctordark.hcf.listener.fixes;

import java.util.Iterator;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.material.EnderChest;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.Listener;

public class EnderChestRemovalListener implements Listener
{
    public EnderChestRemovalListener() {
        this.removeRecipe();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEnderChestOpen(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory() instanceof EnderChest) {
            event.setCancelled(true);
        }
    }
    
    private void removeRecipe() {
        final Iterator<Recipe> iterator = (Iterator<Recipe>)Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getResult().getType() == Material.ENDER_CHEST) {
                iterator.remove();
            }
        }
    }
}
