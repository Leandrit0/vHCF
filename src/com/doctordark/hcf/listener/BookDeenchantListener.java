package com.doctordark.hcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.Listener;

public class BookDeenchantListener implements Listener
{
    private static final ItemStack EMPTY_BOOK;
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem()) {
            final Player player = event.getPlayer();
            if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && player.getGameMode() != GameMode.CREATIVE) {
                final ItemStack stack = event.getItem();
                if (stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
                    final ItemMeta meta = stack.getItemMeta();
                    if (meta instanceof EnchantmentStorageMeta) {
                        final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                        for (final Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                        event.setCancelled(true);
                        player.setItemInHand(BookDeenchantListener.EMPTY_BOOK);
                        player.sendMessage(ChatColor.YELLOW + "You have cleared all enchantments from this book.");
                    }
                }
            }
        }
    }
    
    static {
        EMPTY_BOOK = new ItemStack(Material.BOOK, 1);
    }
}
