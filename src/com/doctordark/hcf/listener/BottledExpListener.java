package com.doctordark.hcf.listener;

import com.doctordark.util.ExperienceManager;
import com.google.common.collect.Lists;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Sound;

import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.event.Listener;

public class BottledExpListener implements Listener
{
    private static final String BOTTLED_EXP_DISPLAY_NAME;
    
    public BottledExpListener() {
        Bukkit.addRecipe((Recipe)new ShapelessRecipe(this.createExpBottle(1)).addIngredient(Material.GLASS_BOTTLE));
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Action action = event.getAction();
        if (event.hasItem() && (action == Action.RIGHT_CLICK_AIR || (action == Action.RIGHT_CLICK_BLOCK && !event.isCancelled()))) {
            final ItemStack stack = event.getItem();
            if (!this.isBottledExperience(stack)) {
                return;
            }
            final ItemMeta meta = stack.getItemMeta();
            final List<String> lore = (List<String>)meta.getLore();
            Integer amount = null;
            for (final String loreLine : lore) {
                if ((amount = Ints.tryParse(ChatColor.stripColor(loreLine).split(" ")[0])) != null) {
                    break;
                }
            }
            if (amount != null) {
                event.setCancelled(true);
                final Player player = event.getPlayer();
                final int previousLevel = player.getLevel();
                new ExperienceManager(player).changeExp((int)amount);
                if (player.getLevel() - previousLevel > 5) {
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                }
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                }
                else {
                    player.setItemInHand(new ItemStack(Material.GLASS_BOTTLE, 1));
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareCraft(final PrepareItemCraftEvent event) {
        if (event.getInventory().getHolder() instanceof Player) {
            final CraftingInventory inventory = event.getInventory();
            final Player player = (Player)inventory.getHolder();
            if (player.hasPermission("hcf.createbottle")) {
                if (this.isBottledExperience(inventory.getResult())) {
                    final int exp = new ExperienceManager(player).getCurrentExp();
                    inventory.setResult((exp > 0) ? this.createExpBottle(exp) : new ItemStack(Material.AIR, 1));
                }
            }
            else {
                inventory.setResult(new ItemStack(Material.AIR, 1));
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftItem(final CraftItemEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if (humanEntity instanceof Player) {
            final Player player = (Player)humanEntity;
            if (event.getSlotType() == InventoryType.SlotType.RESULT && this.isBottledExperience(event.getCurrentItem())) {
                player.setLevel(0);
                player.setExp(0.0f);
            }
        }
    }
    
    private ItemStack createExpBottle(final int experience) {
        final ItemStack stack = new ItemStack(Material.EXP_BOTTLE, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(BottledExpListener.BOTTLED_EXP_DISPLAY_NAME);
        meta.setLore((List)Lists.newArrayList((Object[])new String[] { ChatColor.WHITE.toString() + experience + ChatColor.GOLD + " Experience" }));
        stack.setItemMeta(meta);
        return stack;
    }
    
    private boolean isBottledExperience(final ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return false;
        }
        final ItemMeta meta = stack.getItemMeta();
        return meta.hasDisplayName() && meta.getDisplayName().equals(BottledExpListener.BOTTLED_EXP_DISPLAY_NAME);
    }
    
    static {
        BOTTLED_EXP_DISPLAY_NAME = ChatColor.AQUA.toString() + "Bottled Exp";
    }
}
