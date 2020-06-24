package com.doctordark.hcf.listener.fixes;

import java.util.Set;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.entity.HumanEntity;
import net.minecraft.server.v1_7_R4.ItemArmor;
import net.minecraft.server.v1_7_R4.ItemSword;
import net.minecraft.server.v1_7_R4.ItemTool;
import org.bukkit.event.inventory.PrepareAnvilRepairEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import com.google.common.collect.Maps;

import net.minecraft.server.v1_7_R4.EnumArmorMaterial;
import net.minecraft.server.v1_7_R4.EnumToolMaterial;
import org.bukkit.Material;

import com.doctordark.hcf.ConfigurationService;
import com.google.common.collect.ImmutableMap;
import org.bukkit.event.Listener;

public class EnchantLimitListener implements Listener
{
    private final ImmutableMap<Material, EnumToolMaterial> ITEM_TOOL_MAPPING;
    private final ImmutableMap<Material, EnumArmorMaterial> ITEM_ARMOUR_MAPPING;
    
    public EnchantLimitListener() {
        this.ITEM_TOOL_MAPPING = (ImmutableMap<Material, EnumToolMaterial>)Maps.immutableEnumMap((Map)ImmutableMap.of((Object)Material.IRON_INGOT, (Object)EnumToolMaterial.IRON, (Object)Material.GOLD_INGOT, (Object)EnumToolMaterial.GOLD, (Object)Material.DIAMOND, (Object)EnumToolMaterial.DIAMOND));
        this.ITEM_ARMOUR_MAPPING = (ImmutableMap<Material, EnumArmorMaterial>)Maps.immutableEnumMap((Map)ImmutableMap.of((Object)Material.IRON_INGOT, (Object)EnumArmorMaterial.IRON, (Object)Material.GOLD_INGOT, (Object)EnumArmorMaterial.GOLD, (Object)Material.DIAMOND, (Object)EnumArmorMaterial.DIAMOND));
    }
    
    public int getMaxLevel(final Enchantment enchant) {
        return ConfigurationService.ENCHANTMENT_LIMITS.getOrDefault(enchant, enchant.getMaxLevel());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEnchantItem(final EnchantItemEvent event) {
        final Map adding = event.getEnchantsToAdd();
        final Iterator<Map.Entry<Enchantment, Integer>> iterator = adding.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Enchantment, Integer> entry = iterator.next();
            final Enchantment enchantment = entry.getKey();
            final int maxLevel = this.getMaxLevel(enchantment);
            if (entry.getValue() > maxLevel) {
                if (maxLevel > 0) {
                    adding.put(enchantment, maxLevel);
                }
                else {
                    iterator.remove();
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDeath(final EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            for (final ItemStack drop : event.getDrops()) {
                this.validateIllegalEnchants(drop);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerFishEvent(final PlayerFishEvent event) {
        final Entity caught = event.getCaught();
        if (caught instanceof Item) {
            this.validateIllegalEnchants(((Item)caught).getItemStack());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPrepareAnvilRepair(final PrepareAnvilRepairEvent event) {
        final ItemStack firstAssassinEffects = event.getInventory().getItem(0);
        final ItemStack second = event.getInventory().getItem(1);
        if (firstAssassinEffects != null && firstAssassinEffects.getType() != Material.AIR && second != null && second.getType() != Material.AIR) {
            final Object firstItemObj = net.minecraft.server.v1_7_R4.Item.REGISTRY.a(firstAssassinEffects.getTypeId());
            if (firstItemObj instanceof net.minecraft.server.v1_7_R4.Item) {
                final net.minecraft.server.v1_7_R4.Item nmsFirstItem = (net.minecraft.server.v1_7_R4.Item)firstItemObj;
                if (nmsFirstItem instanceof ItemTool) {
                    if (this.ITEM_TOOL_MAPPING.get((Object)second.getType()) == ((ItemTool)nmsFirstItem).i()) {
                        return;
                    }
                }
                else if (nmsFirstItem instanceof ItemSword) {
                    final EnumToolMaterial comparison = (EnumToolMaterial)this.ITEM_TOOL_MAPPING.get((Object)second.getType());
                    if (comparison != null && comparison.e() == nmsFirstItem.c()) {
                        return;
                    }
                }
                else if (nmsFirstItem instanceof ItemArmor && this.ITEM_ARMOUR_MAPPING.get((Object)second.getType()) == ((ItemArmor)nmsFirstItem).m_()) {
                    return;
                }
            }
        }
        final HumanEntity repairer = event.getRepairer();
        if (repairer instanceof Player) {
            this.validateIllegalEnchants(event.getResult());
        }
    }
    
    private boolean validateIllegalEnchants(final ItemStack stack) {
        boolean updated = false;
        if (stack != null && stack.getType() != Material.AIR) {
            final ItemMeta meta = stack.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta) {
                final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta)meta;
                final Set<Map.Entry<Enchantment, Integer>> entries = enchantmentStorageMeta.getStoredEnchants().entrySet();
                for (final Map.Entry<Enchantment, Integer> entry : entries) {
                    final Enchantment enchantment = entry.getKey();
                    final int maxLevel = this.getMaxLevel(enchantment);
                    if (entry.getValue() > maxLevel) {
                        updated = true;
                        if (maxLevel > 0) {
                            enchantmentStorageMeta.addStoredEnchant(enchantment, maxLevel, false);
                        }
                        else {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                    }
                }
                stack.setItemMeta(meta);
            }
            else {
                final Set<Map.Entry<Enchantment, Integer>> entries2 = stack.getEnchantments().entrySet();
                for (final Map.Entry<Enchantment, Integer> entry2 : entries2) {
                    final Enchantment enchantment2 = entry2.getKey();
                    final int maxLevel2 = this.getMaxLevel(enchantment2);
                    if (entry2.getValue() > maxLevel2) {
                        updated = true;
                        stack.removeEnchantment(enchantment2);
                        if (maxLevel2 <= 0) {
                            continue;
                        }
                        stack.addEnchantment(enchantment2, maxLevel2);
                    }
                }
            }
        }
        return updated;
    }
}
