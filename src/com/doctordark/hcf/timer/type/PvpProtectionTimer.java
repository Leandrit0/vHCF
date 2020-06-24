package com.doctordark.hcf.timer.type;

import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;

import javax.annotation.Nullable;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.command.CommandSender;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.Location;
import java.util.Iterator;
import java.util.Collection;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.event.FactionClaimChangedEvent;
import com.doctordark.hcf.faction.event.PlayerClaimEnterEvent;
import com.doctordark.hcf.faction.event.cause.ClaimChangeCause;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.faction.type.RoadFaction;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.TimerRunnable;
import com.doctordark.hcf.timer.event.TimerClearEvent;
import com.doctordark.hcf.visualise.VisualBlock;
import com.doctordark.hcf.visualise.VisualType;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.Config;
import com.doctordark.util.GenericUtils;
import com.google.common.base.Optional;

import org.bukkit.entity.Player;

import com.google.common.base.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import com.google.common.cache.CacheBuilder;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentMap;
import java.util.UUID;
import java.util.Set;
import org.bukkit.event.Listener;

public class PvpProtectionTimer extends PlayerTimer implements Listener
{
    private static final String PVP_COMMAND = "/pvp enable";
    private static final long ITEM_PICKUP_DELAY;
    private static final long ITEM_PICKUP_MESSAGE_DELAY = 1250L;
    private static final String ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";
    public final Set<UUID> legible;
    private final ConcurrentMap<Object, Object> itemUUIDPickupDelays;
    private final HCF plugin;
    
    public PvpProtectionTimer(final HCF plugin) {
        super("PvP Timer", TimeUnit.MINUTES.toMillis(30L));
        this.legible = new HashSet<UUID>();
        this.plugin = plugin;
        this.itemUUIDPickupDelays = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(PvpProtectionTimer.ITEM_PICKUP_DELAY + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }
    
    public String getScoreboardPrefix() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD;
    }
    
    @Override
    public void onExpire(final UUID userUUID) {
        final Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }
        if (this.getRemaining(player) <= 0L) {
            this.plugin.getVisualiseHandler().clearVisualBlocks(player, VisualType.CLAIM_BORDER, null);
            player.sendMessage(ChatColor.RED.toString() + "You no longer have " + this.getDisplayName() + ChatColor.RED + '.');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerStop(final TimerClearEvent event) {
        if (event.getTimer().equals(this)) {
            final Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                this.onExpire((UUID)optionalUserUUID.get());
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClaimChange(final FactionClaimChangedEvent event) {
        if (event.getCause() != ClaimChangeCause.CLAIM) {
            return;
        }
        final Collection<Claim> claims = event.getAffectedClaims();
        for (final Claim claim : claims) {
            final Collection<Player> players = (Collection<Player>)claim.getPlayers();
            for (final Player player : players) {
                if (this.getRemaining(player) > 0L) {
                    Location location = player.getLocation();
                    location.setX(claim.getMinimumX() - 1);
                    location.setY(0);
                    location.setZ(claim.getMinimumZ() - 1);
                    location = BukkitUtils.getHighestLocation(location, location);
                    if (!player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN)) {
                        continue;
                    }
                    player.sendMessage(ChatColor.RED + "Land was claimed where you were standing. As you still have your " + this.getName() + " timer, you were teleported away.");
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        this.clearCooldown(player);
        if (this.legible.add(player.getUniqueId())) {
            player.sendMessage(ChatColor.AQUA + "Once you leave Spawn your 30 minutes of " + this.getDisplayName() + ChatColor.AQUA + " will start.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final World world = player.getWorld();
        final Location location = player.getLocation();
        final Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            this.itemUUIDPickupDelays.put(world.dropItemNaturally(location, (ItemStack)iterator.next()).getUniqueId(), System.currentTimeMillis() + PvpProtectionTimer.ITEM_PICKUP_DELAY);
            iterator.remove();
        }
        this.clearCooldown(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot empty buckets as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot ignite blocks as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onItemPickup(final PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final long remaining = this.getRemaining(player);
        if (remaining > 0L) {
            final UUID itemUUID = event.getItem().getUniqueId();
            final Long delay = (Long) this.itemUUIDPickupDelays.get(itemUUID);
            if (delay == null) {
                return;
            }
            final long millis = System.currentTimeMillis();
            if (delay - millis > 0L) {
                event.setCancelled(true);
                final MetadataValue value = player.getMetadata("pickupMessageDelay", (Plugin)this.plugin);
                if (value != null && value.asLong() - millis <= 0L) {
                    player.setMetadata("pickupMessageDelay", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)(millis + 1250L)));
                    player.sendMessage(ChatColor.RED + "You cannot pick this item up for another " + ChatColor.BOLD + DurationFormatUtils.formatDurationWords(remaining, true, true) + ChatColor.RED + " as your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]");
                }
            }
            else {
                this.itemUUIDPickupDelays.remove(itemUUID);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final TimerRunnable runnable = this.cooldowns.get(player.getUniqueId());
        if (runnable != null && runnable.getRemaining() > 0L) {
            runnable.setPaused(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerSpawnLocation(final PlayerSpawnLocationEvent event) {
        final Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && this.legible.add(player.getUniqueId())) {
                player.sendMessage(ChatColor.AQUA + "Once you leave Spawn your 30 minutes of " + this.getName() + ChatColor.AQUA + " will start.");
            }
        }
        else if (this.isPaused(player) && this.getRemaining(player) > 0L && !this.plugin.getFactionManager().getFactionAt(event.getSpawnLocation()).isSafezone()) {
            this.setPaused(player, player.getUniqueId(), false);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerClaimEnterMonitor(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            this.clearCooldown(player);
            return;
        }
        final Faction toFaction = event.getToFaction();
        final Faction fromFaction = event.getFromFaction();
        if (fromFaction.isSafezone() && !toFaction.isSafezone()) {
            if (this.legible.remove(player.getUniqueId())) {
                this.setCooldown(player, player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Your " + this.getDisplayName() + ChatColor.GREEN + " timer has started.");
                return;
            }
            if (this.getRemaining(player) > 0L) {
                this.setPaused(player, player.getUniqueId(), false);
                player.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " timer is no longer paused.");
            }
        }
        else if (!fromFaction.isSafezone() && toFaction.isSafezone() && this.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.GREEN + "Your " + this.getDisplayName() + ChatColor.GREEN + " timer is now paused.");
            this.setPaused(player, player.getUniqueId(), true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerClaimEnter(final PlayerClaimEnterEvent event) {
        final Player player = event.getPlayer();
        final Faction toFaction = event.getToFaction();
        final long remaining;
        if (toFaction instanceof ClaimableFaction && (remaining = this.getRemaining(player)) > 0L) {
            final PlayerFaction playerFaction;
            if (event.getEnterCause() == PlayerClaimEnterEvent.EnterCause.TELEPORT && toFaction instanceof PlayerFaction && (playerFaction = this.plugin.getFactionManager().getPlayerFaction(player)) != null && playerFaction.equals(toFaction)) {
                player.sendMessage(ChatColor.AQUA + "You have entered your own claim, therefore your " + this.getDisplayName() + ChatColor.AQUA + " has been removed.");
                this.clearCooldown(player);
                return;
            }
            if (!toFaction.isSafezone() && !(toFaction instanceof RoadFaction)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot enter " + toFaction.getDisplayName((CommandSender)player) + ChatColor.RED + " whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. " + "Use '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + "' to remove this timer.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, true);
            if (attacker == null) {
                return;
            }
            final Player player = (Player)entity;
            long remaining;
            if ((remaining = this.getRemaining(player)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + player.getName() + " has their " + this.getDisplayName() + ChatColor.RED + " timer for another " + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                return;
            }
            if ((remaining = this.getRemaining(attacker)) > 0L) {
                event.setCancelled(true);
                attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst your " + this.getDisplayName() + ChatColor.RED + " timer is active [" + ChatColor.BOLD + HCF.getRemaining(remaining, true, false) + ChatColor.RED + " remaining]. Use '" + ChatColor.GOLD + "/pvp enable" + ChatColor.RED + "' to allow pvp.");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getPotion();
        if (potion.getShooter() instanceof Player && BukkitUtils.isDebuff(potion)) {
            for (final LivingEntity livingEntity : event.getAffectedEntities()) {
                if (livingEntity instanceof Player && this.getRemaining((Player)livingEntity) > 0L) {
                    event.setIntensity(livingEntity, 0.0);
                }
            }
        }
    }
    
    public Set<UUID> getLegible() {
        return this.legible;
    }
    
    @Override
    public long getRemaining(final UUID playerUUID) {
        return this.plugin.getEotwHandler().isEndOfTheWorld() ? 0L : super.getRemaining(playerUUID);
    }
    
    @Override
    public boolean setCooldown(@Nullable final Player player, final UUID playerUUID, final long duration, final boolean overwrite) {
        return !this.plugin.getEotwHandler().isEndOfTheWorld() && super.setCooldown(player, playerUUID, duration, overwrite);
    }
    
    @Override
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = super.clearCooldown(playerUUID);
        if (runnable != null) {
            this.legible.remove(playerUUID);
            return runnable;
        }
        return null;
    }
    
    @Override
    public void load(final Config config) {
        super.load(config);
        final Object object = config.get("pvp-timer-legible");
        if (object instanceof List) {
            this.legible.addAll(GenericUtils.createList(object, String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        }
    }
    
    @Override
    public void onDisable(final Config config) {
        super.onDisable(config);
        config.set("pvp-timer-legible", new ArrayList<>(legible).toArray(new UUID[legible.size()]));
    }
    
    static {
        ITEM_PICKUP_DELAY = TimeUnit.SECONDS.toMillis(30L);
    }
}
