package com.doctordark.hcf.listener;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import com.doctordark.hcf.HCF;

public class SoupListener implements Listener {
  private static final int MAX_FOOD_LEVEL = 20;
  private static final float SOUP_EAT_SATURATION = 4.0F;
  private static final ItemStack SOUP_EAT_RESULT = new ItemStack(Material.BOWL);
  
  private final HCF plugin;
  
  public SoupListener(HCF plugin) {
    this.plugin = plugin;
    Bukkit.addRecipe((new ShapelessRecipe(new ItemStack(Material.MUSHROOM_SOUP)))
        .addIngredient((new ItemStack(Material.INK_SACK, 3)).getData())
        .addIngredient(Material.BOWL));
  }
  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerInteract(PlayerInteractEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    ItemStack stack;
    switch (event.getAction()) {
      case RIGHT_CLICK_AIR:
      case RIGHT_CLICK_BLOCK:
        if (!event.hasItem() || !(this.plugin.getConfig().getBoolean("instant-soup.enabled") == true)) {
          return;
        }
        
        stack = event.getItem();
        if (stack.getType() == Material.MUSHROOM_SOUP) {
          Player player = event.getPlayer();
          int health_in_int=(int)player.getClass().getMethod("getHealth", double.class).invoke(player);
          double current=(double)player.getClass().getMethod("getHealth", double.class).invoke(player);
          double maximum = (double) player.getClass().getMethod("getMaxHealth", double.class).invoke(player);
          
          if (current < maximum) {
            player.setHealth(Math.min(maximum, current + (this.plugin.getConfig().getInt("instant-soup.health-regain"))));
          } else if ((current = player.getFoodLevel()) < 20.0D) {
            player.setFoodLevel(Math.min(20, (int)current + (this.plugin.getConfig().getInt("instant-soup.hunger-regain"))));
          } else {
            return;
          } 
          
          event.setCancelled(true);
          player.setSaturation(4.0F);
          player.setItemInHand(SOUP_EAT_RESULT);
        } 
        break;
    } 
  }
}
