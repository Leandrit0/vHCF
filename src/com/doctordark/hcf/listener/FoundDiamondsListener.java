package com.doctordark.hcf.listener;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.doctordark.hcf.HCF;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.event.Listener;

public class FoundDiamondsListener implements Listener
{
    private static final String NOTIFICATION_PERMISSION = "hcf.founddiamonds.alert";
    public static final Material SEARCH_TYPE;
    private static final int SEARCH_RADIUS = 3;
    public final Set<String> foundLocations;
    private final HCF plugin;
    
    public FoundDiamondsListener(final HCF plugin) {
        this.foundLocations = new HashSet<String>();
        this.plugin = plugin;
        this.foundLocations.addAll(plugin.getConfig().getStringList("registered-diamonds"));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonExtend(final BlockPistonExtendEvent event) {
        for (final Block block : event.getBlocks()) {
            if (block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
                this.foundLocations.add(block.getLocation().toString());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
            this.foundLocations.add(block.getLocation().toString());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        final Block block = event.getBlock();
        final Location blockLocation = block.getLocation();
        if (block.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(blockLocation.toString())) {
            int count = 1;
            for (int x = -5; x < 5; ++x) {
                for (int y = -5; y < 5; ++y) {
                    for (int z = -5; z < 5; ++z) {
                        final Block otherBlock = blockLocation.clone().add((double)x, (double)y, (double)z).getBlock();
                        if (!otherBlock.equals(block) && otherBlock.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(otherBlock.getLocation().toString())) {
                            ++count;
                        }
                    }
                }
            }
            this.plugin.getUserManager().getUser(player.getUniqueId()).setDiamondsMined(this.plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined() + count);
            for (final Player on : Bukkit.getOnlinePlayers()) {
                final String message = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getRelation((CommandSender)on).toChatColour() + player.getName() + ChatColor.GRAY + " has found" + ChatColor.AQUA + " Diamonds " + ChatColor.GRAY + '[' + ChatColor.AQUA + count + ChatColor.GRAY + ']';
                on.sendMessage(message);
            }
        }
    }
    
    public void saveConfig() {
        this.plugin.getConfig().set("registered-diamonds", (Object)new ArrayList(this.foundLocations));
        this.plugin.saveConfig();
    }
    
    static {
        SEARCH_TYPE = Material.DIAMOND_ORE;
    }
}
