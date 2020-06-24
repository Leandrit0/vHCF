package com.doctordark.hcf.economy;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockState;
import org.bukkit.block.Block;
import java.util.Map;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.ChatColor;
import java.util.Arrays;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.api.Crowbar;
import com.doctordark.util.InventoryUtils;

import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.regex.Pattern;
import org.bukkit.event.Listener;

public class ShopSignListener implements Listener
{
    private static final long SIGN_TEXT_REVERT_TICKS = 100L;
    private static final Pattern ALPHANUMERIC_REMOVER;
    private final HCF plugin;
    
    public ShopSignListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            final BlockState state = block.getState();
            if (state instanceof Sign) {
                final Sign sign = (Sign)state;
                final String[] lines = sign.getLines();
                final Integer quantity = Ints.tryParse(lines[2]);
                if (quantity == null) {
                    return;
                }
                final Integer price = Ints.tryParse(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
                if (price == null) {
                    return;
                }
                ItemStack stack;
                if (lines[1].equalsIgnoreCase("Crowbar")) {
                    stack = new Crowbar().getItemIfPresent();
                }
                else if ((stack = HCF.getPlugin().getItemDb().getItem(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[1]).replaceAll(""), (int)quantity)) == null) {
                    return;
                }
                final Player player = event.getPlayer();
                final String[] fakeLines = Arrays.<String>copyOf(sign.getLines(), 4);
                if ((lines[0].contains("Sell") && lines[0].contains(ChatColor.RED.toString())) || lines[0].contains(ChatColor.AQUA.toString())) {
                    final int sellQuantity = Math.min(quantity, InventoryUtils.countAmount((Inventory)player.getInventory(), stack.getType(), stack.getDurability()));
                    if (sellQuantity <= 0) {
                        fakeLines[0] = ChatColor.RED + "Not carrying any";
                        fakeLines[2] = ChatColor.RED + "on you.";
                        fakeLines[3] = "";
                    }
                    else {
                        final int newPrice = price / quantity * sellQuantity;
                        fakeLines[0] = ChatColor.GREEN + "Sold " + sellQuantity;
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + newPrice;
                        this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                        InventoryUtils.removeItem((Inventory)player.getInventory(), stack.getType(), (short)stack.getData().getData(), sellQuantity);
                        player.updateInventory();
                    }
                }
                else {
                    if (!lines[0].contains("Buy") || !lines[0].contains(ChatColor.GREEN.toString())) {
                        return;
                    }
                    if (price > this.plugin.getEconomyManager().getBalance(player.getUniqueId())) {
                        fakeLines[0] = ChatColor.RED + "Cannot afford";
                    }
                    else {
                        fakeLines[0] = ChatColor.GREEN + "Item bought";
                        fakeLines[3] = ChatColor.GREEN + "for " + '$' + price;
                        this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price);
                        final World world = player.getWorld();
                        final Location location = player.getLocation();
                        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>)player.getInventory().addItem(new ItemStack[] { stack });
                        for (final Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                            world.dropItemNaturally(location, (ItemStack)excessItemStack.getValue());
                        }
                        player.setItemInHand(player.getItemInHand());
                    }
                }
                event.setCancelled(true);
                HCF.getPlugin().getSignHandler().showLines(player, sign, fakeLines, 100L, true);
            }
        }
    }
    public void onJoin(PlayerJoinEvent e){
        if (!e.getPlayer().hasPlayedBefore()){
            HCF.getPlugin().getEconomyManager().addBalance(e.getPlayer().getUniqueId(),  500);
        }
    }
    static {
        ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    }
}
