package com.doctordark.hcf.listener;

import com.google.common.collect.Sets;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.entity.Hanging;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Cauldron;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Horse;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.TravelAgent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;

import java.util.Iterator;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import java.util.Objects;

import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import com.google.common.collect.ImmutableSet;
import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.faction.CapturableFaction;
import com.doctordark.hcf.faction.event.CaptureZoneEnterEvent;
import com.doctordark.hcf.faction.event.CaptureZoneLeaveEvent;
import com.doctordark.hcf.faction.event.PlayerClaimEnterEvent;
import com.doctordark.hcf.faction.struct.Raidable;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.faction.type.WarzoneFaction;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.cuboid.Cuboid;
import com.google.common.collect.ImmutableMultimap;
import org.bukkit.event.Listener;

public class ProtectionListener implements Listener
{
    public static final String PROTECTION_BYPASS_PERMISSION = "hcf.faction.protection.bypass";
    private static final ImmutableMultimap<Object, Object> ITEM_BLOCK_INTERACTABLES;
    private static final ImmutableSet<Material> BLOCK_INTERACTABLES;
    private final HCF plugin;
    
    public ProtectionListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }
    
    public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage, final boolean isInteraction) {
        boolean result = false;
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            if (player != null && player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.faction.protection.bypass")) {
                return true;
            }
            if (player != null && player.getWorld().getEnvironment() == World.Environment.THE_END) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("faction.break_message_end")));
                return false;
            }
            final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
            if (!(factionAt instanceof ClaimableFaction)) {
                result = true;
            }
            else if (factionAt instanceof Raidable && ((Raidable)factionAt).isRaidable()) {
                result = true;
            }
            if (player != null && factionAt instanceof PlayerFaction) {
                final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
                if (playerFaction != null && playerFaction.equals(factionAt)) {
                    result = true;
                }
            }
            if (result) {
                if (!isInteraction && Math.abs(location.getBlockX()) <= 150 && Math.abs(location.getBlockZ()) <= 150) {
                    if (denyMessage != null && player != null) {
                        player.sendMessage(ChatColor.YELLOW + "You cannot build within " + ChatColor.WHITE + 150 + ChatColor.YELLOW + " blocks from spawn.");
                    }
                    return false;
                }
            }
            else if (denyMessage != null && player != null) {
                player.sendMessage(String.format(denyMessage, factionAt.getDisplayName((CommandSender)player)));
            }
        }
        return result;
    }
    
    public static boolean canBuildAt(final Location from, final Location to) {
        final Faction toFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(to);
        if (toFactionAt instanceof Raidable && !((Raidable)toFactionAt).isRaidable()) {
            final Faction fromFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(from);
            if (!toFactionAt.equals(fromFactionAt)) {
                return false;
            }
        }
        return true;
    }
    
    private void handleMove(final PlayerMoveEvent event, final PlayerClaimEnterEvent.EnterCause enterCause) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        final Player player = event.getPlayer();
        boolean cancelled = false;
        final Faction fromFaction = this.plugin.getFactionManager().getFactionAt(from);
        final Faction toFaction = this.plugin.getFactionManager().getFactionAt(to);
        if (!Objects.equals(fromFaction, toFaction)) {
            final PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            Bukkit.getPluginManager().callEvent((Event)calledEvent);
            cancelled = calledEvent.isCancelled();
        }
        else if (toFaction instanceof CapturableFaction) {
            final CapturableFaction capturableFaction = (CapturableFaction)toFaction;
            for (final CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                final Cuboid cuboid = captureZone.getCuboid();
                if (cuboid != null) {
                    final boolean containsFrom = cuboid.contains(from);
                    final boolean containsTo = cuboid.contains(to);
                    if (containsFrom && !containsTo) {
                        final CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event)calledEvent2);
                        cancelled = calledEvent2.isCancelled();
                        break;
                    }
                    if (!containsFrom && containsTo) {
                        final CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event)calledEvent3);
                        cancelled = calledEvent3.isCancelled();
                        break;
                    }
                    continue;
                }
            }
        }
        if (cancelled) {
            if (enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                event.setCancelled(true);
            }
            else {
                from.add(0.5, 0.0, 0.5);
                event.setTo(from);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerTeleportEvent event) {
        this.handleMove((PlayerMoveEvent)event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        switch (event.getCause()) {
            default: {
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
                if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonExtend(final BlockPistonExtendEvent event) {
        final Block block = event.getBlock();
        final Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        if (targetBlock.isEmpty() || targetBlock.isLiquid()) {
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation());
            if (targetFaction instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(final BlockPistonRetractEvent event) {
        if (!event.isSticky()) {
            return;
        }
        final Location retractLocation = event.getRetractLocation();
        final Block retractBlock = retractLocation.getBlock();
        if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            final Block block = event.getBlock();
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(retractLocation);
            if (targetFaction instanceof Raidable && !((Raidable)targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block toBlock = event.getToBlock();
        final Block fromBlock = event.getBlock();
        final Material fromType = fromBlock.getType();
        final Material toType = toBlock.getType();
        if ((toType == Material.REDSTONE_WIRE || toType == Material.TRIPWIRE) && (fromType == Material.AIR || fromType == Material.STATIONARY_LAVA || fromType == Material.LAVA)) {
            toBlock.setType(Material.AIR);
        }
        if ((toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER || toBlock.getType() == Material.LAVA || toBlock.getType() == Material.STATIONARY_LAVA) && !canBuildAt(fromBlock.getLocation(), toBlock.getLocation())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Faction toFactionAt = this.plugin.getFactionManager().getFactionAt(event.getTo());
            if (toFactionAt.isSafezone() && !this.plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
                final Player player = event.getPlayer();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("faction.enderpearl_safezone")));
                this.plugin.getTimerManager().enderPearlTimer.refund(player);
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            final Location from = event.getFrom();
            final Location to = event.getTo();
            final Player player = event.getPlayer();
            final Faction fromFac = this.plugin.getFactionManager().getFactionAt(from);
            if (fromFac.isSafezone()) {
                event.setTo(to.getWorld().getSpawnLocation().add(0.5, 0.0, 0.5));
                event.useTravelAgent(false);
                player.sendMessage(ChatColor.YELLOW + "You were teleported to the spawn of target world as you were in a safe-zone.");
                return;
            }
            if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                final TravelAgent travelAgent = event.getPortalTravelAgent();
                if (!travelAgent.getCanCreatePortal()) {
                    return;
                }
                final Location foundPortal = travelAgent.findPortal(to);
                if (foundPortal != null) {
                    return;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(to);
                if (factionAt instanceof ClaimableFaction) {
                    final Faction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    if (playerFaction != null && playerFaction.equals(factionAt)) {
                        return;
                    }
                    player.sendMessage(ChatColor.YELLOW + "Portal would have created portal in territory of " + factionAt.getDisplayName((CommandSender)player) + ChatColor.YELLOW + '.');
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        final CreatureSpawnEvent.SpawnReason reason = event.getSpawnReason();
        if (reason == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            return;
        }
        final Location location = event.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (factionAt.isSafezone() && reason == CreatureSpawnEvent.SpawnReason.SPAWNER) {
            return;
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            final Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            final EntityDamageEvent.DamageCause cause = event.getCause();
            if (playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE) {
                event.setCancelled(true);
            }
            final Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if (attacker != null) {
                final Faction attackerFactionAt = this.plugin.getFactionManager().getFactionAt(attacker.getLocation());
                if (attackerFactionAt.isSafezone()) {
                    event.setCancelled(true);
                    attacker.sendMessage(ChatColor.RED + "You cannot attack players whilst in safe-zones.");
                    return;
                }
                if (playerFactionAt.isSafezone()) {
                    attacker.sendMessage(ChatColor.RED + "You cannot attack players that are in safe-zones.");
                    return;
                }
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                final PlayerFaction attackerFaction;
                if (playerFaction != null && (attackerFaction = this.plugin.getFactionManager().getPlayerFaction(attacker)) != null) {
                    final Role role = playerFaction.getMember(player).getRole();
                    final String astrix = role.getAstrix();
                    if (attackerFaction.equals(playerFaction) && playerFaction.isFriendlyfire() == false) {
                        attacker.sendMessage(ConfigurationService.TEAMMATE_COLOUR + astrix + player.getName() + ChatColor.YELLOW + " is in your faction.");
                        event.setCancelled(true);
                    } else if (attackerFaction.equals(playerFaction) && playerFaction.isFriendlyfire() == true) {
                    	attacker.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("faction_friendlyfire").replace("%victim%", (CharSequence) player)));
                    }
                    else if (attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
                        attacker.sendMessage(ChatColor.YELLOW + "Careful! " + ConfigurationService.TEAMMATE_COLOUR + astrix + player.getName() + ChatColor.YELLOW + " is an ally.");
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Entity entered = event.getEntered();
        if (entered instanceof Player) {
            final Vehicle vehicle = event.getVehicle();
            if (vehicle instanceof Horse) {
                final Horse horse = (Horse)event.getVehicle();
                final AnimalTamer owner = horse.getOwner();
                if (owner != null && !owner.equals(entered)) {
                    ((Player)entered).sendMessage(ChatColor.YELLOW + "You cannot enter a Horse that belongs to " + ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final Entity entity = (Entity)event.getEntity();
        if (entity instanceof Player && ((Player)entity).getFoodLevel() < event.getFoodLevel() && this.plugin.getFactionManager().getFactionAt(entity.getLocation()).isSafezone()) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getEntity();
        if (!BukkitUtils.isDebuff(potion)) {
            return;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(potion.getLocation());
        if (factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }
        final ProjectileSource source = potion.getShooter();
        if (source instanceof Player) {
            final Player player = (Player)source;
            for (final LivingEntity affected : event.getAffectedEntities()) {
                if (affected instanceof Player && !player.equals(affected)) {
                    final Player target = (Player)affected;
                    if (target.equals(source)) {
                        continue;
                    }
                    if (!this.plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
                        continue;
                    }
                    event.setIntensity(affected, 0.0);
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityTarget(final EntityTargetEvent event) {
        switch (event.getReason()) {
            case CLOSEST_PLAYER:
            case RANDOM_TARGET: {
                final Entity target = event.getTarget();
                if (!(event.getEntity() instanceof LivingEntity) || !(target instanceof Player)) {
                    break;
                }
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(target.getLocation());
                final Faction playerFaction;
                if (factionAt.isSafezone() || ((playerFaction = this.plugin.getFactionManager().getPlayerFaction((Player)target)) != null && factionAt.equals(playerFaction))) {
                    event.setCancelled(true);
                    break;
                }
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        final Block block = event.getClickedBlock();
        final Action action = event.getAction();
        if (action == Action.PHYSICAL && !attemptBuild((Entity)event.getPlayer(), block.getLocation(), null)) {
            event.setCancelled(true);
        }
        if (action == Action.RIGHT_CLICK_BLOCK) {
            boolean canBuild = !ProtectionListener.BLOCK_INTERACTABLES.contains((Object)block.getType());
            if (canBuild) {
                final Material itemType = event.hasItem() ? event.getItem().getType() : null;
                if (itemType != null && ProtectionListener.ITEM_BLOCK_INTERACTABLES.containsKey((Object)itemType) && ProtectionListener.ITEM_BLOCK_INTERACTABLES.get((Object)itemType).contains((Object)event.getClickedBlock().getType())) {
                    canBuild = false;
                }
                else {
                    final MaterialData materialData = block.getState().getData();
                    if (materialData instanceof Cauldron) {
                        final Cauldron cauldron = (Cauldron)materialData;
                        if (!cauldron.isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
                            canBuild = false;
                        }
                    }
                }
            }
            if (!canBuild && !attemptBuild((Entity)event.getPlayer(), block.getLocation(), ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBurn(final BlockBurnEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable)factionAt).isRaidable())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeavesDelay(final LeavesDecayEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockForm(final BlockFormEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if (factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlock().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(final PlayerBucketFillEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();
        if (remover instanceof Player && !attemptBuild(remover, event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingPlace(final HangingPlaceEvent event) {
        if (!attemptBuild((Entity)event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Hanging) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false);
            if (!attemptBuild((Entity)attacker, entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if (entity instanceof Hanging && !attemptBuild((Entity)event.getPlayer(), entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }
    
    static {
        ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.builder().put((Object)Material.DIAMOND_HOE, (Object)Material.GRASS).put((Object)Material.GOLD_HOE, (Object)Material.GRASS).put((Object)Material.IRON_HOE, (Object)Material.GRASS).put((Object)Material.STONE_HOE, (Object)Material.GRASS).put((Object)Material.WOOD_HOE, (Object)Material.GRASS).build();
        BLOCK_INTERACTABLES = Sets.immutableEnumSet((Enum)Material.BED, (Enum[])new Material[] { Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE });
    }
}
