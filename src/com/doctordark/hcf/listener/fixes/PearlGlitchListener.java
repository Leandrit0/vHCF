package com.doctordark.hcf.listener.fixes;

import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import com.google.common.collect.Sets;

import org.bukkit.Material;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.Listener;

public class PearlGlitchListener implements Listener
{
    private final ImmutableSet<Material> blockedPearlTypes;
    private final HCF plugin;
    
    public PearlGlitchListener(final HCF plugin) {
        this.blockedPearlTypes = (ImmutableSet<Material>)Sets.immutableEnumSet((Enum)Material.THIN_GLASS, (Enum[])new Material[] { Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS });
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.getItem().getType() == Material.ENDER_PEARL) {
            final Block block = event.getClickedBlock();
            if (block.getType().isSolid() && !(block.getState() instanceof InventoryHolder)) {
                final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(block.getLocation());
                if (!(factionAt instanceof ClaimableFaction)) {
                    return;
                }
                event.setCancelled(true);
                final Player player = event.getPlayer();
                player.setItemInHand(event.getItem());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPearlClip(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Location to = event.getTo();
            if (this.blockedPearlTypes.contains((Object)to.getBlock().getType())) {
                final Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "Pearl glitching detected.");
                this.plugin.getTimerManager().enderPearlTimer.refund(player);
                event.setCancelled(true);
                return;
            }
            to.setX(to.getBlockX() + 0.5);
            to.setZ(to.getBlockZ() + 0.5);
            event.setTo(to);
        }
    }
}
