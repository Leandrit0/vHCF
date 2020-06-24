package com.doctordark.hcf.command;

import java.util.Collection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.Bukkit;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.util.ItemBuilder;
import com.doctordark.util.chat.Lang;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import java.util.HashSet;

import org.bukkit.inventory.Inventory;
import java.util.Set;
import org.bukkit.event.Listener;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class MapKitCommand implements CommandExecutor, TabCompleter, Listener
{
    private final Set<Inventory> tracking;
    
    public MapKitCommand(final HCF plugin) {
        this.tracking = new HashSet<Inventory>();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command is only executable by players.");
            return true;
        }
        final List<ItemStack> items = new ArrayList<ItemStack>();
        for (final Map.Entry<Enchantment, Integer> entry : ConfigurationService.ENCHANTMENT_LIMITS.entrySet()) {
            items.add(new ItemBuilder(Material.ENCHANTED_BOOK).displayName(ChatColor.YELLOW + Lang.fromEnchantment((Enchantment)entry.getKey()) + ": " + ChatColor.GREEN + entry.getValue()).build());
        }
        for (final Map.Entry<PotionType, Integer> entry2 : ConfigurationService.POTION_LIMITS.entrySet()) {
            if (entry2.getValue() > 0) {
                items.add(new ItemBuilder(new Potion((PotionType)entry2.getKey()).toItemStack(1)).displayName(ChatColor.YELLOW + WordUtils.capitalizeFully(entry2.getKey().name().replace('_', ' ')) + ": " + ChatColor.GREEN + entry2.getValue()).build());
            }
        }
        final Player player = (Player)sender;
        final int inventorySize = (items.size() + 8) / 9 * 9;
        final Inventory inventory = Bukkit.createInventory((InventoryHolder)player, inventorySize, ChatColor.YELLOW + "Map " + 2.0 + " Kit");
        this.tracking.add(inventory);
        for (final ItemStack item : items) {
            inventory.addItem(new ItemStack[] { item });
        }
        player.openInventory(inventory);
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return Collections.<String>emptyList();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInventoryClick(final InventoryClickEvent event) {
        if (this.tracking.contains(event.getInventory())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        for (final Inventory inventory : this.tracking) {
            final Collection<HumanEntity> viewers = new HashSet<HumanEntity>(inventory.getViewers());
            for (final HumanEntity viewer : viewers) {
                viewer.closeInventory();
            }
        }
    }
}
