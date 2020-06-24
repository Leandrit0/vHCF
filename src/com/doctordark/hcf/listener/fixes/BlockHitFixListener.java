package com.doctordark.hcf.listener.fixes;

import com.google.common.collect.Sets;
import java.util.UUID;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.concurrent.TimeUnit;

import com.doctordark.util.BukkitUtils;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Material;
import com.google.common.collect.ImmutableSet;
import org.bukkit.event.Listener;

public class BlockHitFixListener implements Listener
{
    private static final long THRESHOLD = 850L;
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_BREAK_TYPES;
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_INTERACT_TYPES;
    private final ConcurrentMap<Object, Object> lastInteractTimes;
    
    public BlockHitFixListener() {
        this.lastInteractTimes = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(850L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() != Action.PHYSICAL && BlockHitFixListener.NON_TRANSPARENT_ATTACK_INTERACT_TYPES.contains((Object)event.getClickedBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled() && BlockHitFixListener.NON_TRANSPARENT_ATTACK_BREAK_TYPES.contains((Object)event.getBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), 850L);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker(event, true);
        if (attacker != null) {
            final Long lastInteractTime = (Long) this.lastInteractTimes.get(attacker.getUniqueId());
            if (lastInteractTime != null && lastInteractTime - System.currentTimeMillis() > 0L) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }
    
    public void cancelAttackingMillis(final UUID uuid, final long delay) {
        this.lastInteractTimes.put(uuid, System.currentTimeMillis() + delay);
    }
    
    static {
        NON_TRANSPARENT_ATTACK_BREAK_TYPES = Sets.immutableEnumSet((Enum)Material.GLASS, (Enum[])new Material[] { Material.STAINED_GLASS, Material.STAINED_GLASS_PANE });
        NON_TRANSPARENT_ATTACK_INTERACT_TYPES = Sets.immutableEnumSet((Enum)Material.IRON_DOOR_BLOCK, (Enum[])new Material[] { Material.IRON_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR, Material.FENCE_GATE });
    }
}
