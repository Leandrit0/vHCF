package com.doctordark.hcf.pvpclass.bard;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.pvpclass.PvpClass;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.chat.Lang;

import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import java.util.Iterator;
import java.util.Collection;

import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.gnu.trove.map.TObjectLongMap;

import org.bukkit.Material;
import java.util.UUID;
import java.util.Map;
import org.bukkit.event.Listener;

public class BardClass extends PvpClass implements Listener
{
    public static final int HELD_EFFECT_DURATION_TICKS = 100;
    private static final long BUFF_COOLDOWN_MILLIS;
    private static final int TEAMMATE_NEARBY_RADIUS = 25;
    private static final long HELD_REAPPLY_TICKS = 20L;
    private static final String MARK;
    private final Map<UUID, BardData> bardDataMap;
    private final Map<Material, BardEffect> bardEffects;
    private final BardRestorer bardRestorer;
    private final HCF plugin;
    private final TObjectLongMap<UUID> msgCooldowns;
    
    public BardClass(final HCF plugin) {
        super("Bard", TimeUnit.SECONDS.toMillis(1L));
        this.bardDataMap = new HashMap<UUID, BardData>();
        this.bardEffects = new EnumMap<Material, BardEffect>(Material.class);
        this.msgCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.plugin = plugin;
        this.bardRestorer = new BardRestorer(plugin);
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        this.bardEffects.put(Material.SUGAR, new BardEffect(45, new PotionEffect(PotionEffectType.SPEED, 120, 2), new PotionEffect(PotionEffectType.SPEED, 100, 1)));
        this.bardEffects.put(Material.BLAZE_POWDER, new BardEffect(50, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0)));
        this.bardEffects.put(Material.IRON_INGOT, new BardEffect(40, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 2), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 0)));
        this.bardEffects.put(Material.GHAST_TEAR, new BardEffect(45, new PotionEffect(PotionEffectType.REGENERATION, 60, 2), new PotionEffect(PotionEffectType.REGENERATION, 100, 0)));
        this.bardEffects.put(Material.FEATHER, new BardEffect(45, new PotionEffect(PotionEffectType.JUMP, 120, 2), new PotionEffect(PotionEffectType.JUMP, 100, 0)));
        this.bardEffects.put(Material.SPIDER_EYE, new BardEffect(50, new PotionEffect(PotionEffectType.WITHER, 100, 1), null));
        this.bardEffects.put(Material.MAGMA_CREAM, new BardEffect(10, new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
        this.bardEffects.put(Material.FERMENTED_SPIDER_EYE, new BardEffect(10, new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0), new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0)));
    }
    
    @Override
    public boolean onEquip(final Player player) {
        if (this.plugin.getTimerManager().pvpProtectionTimer.legible.contains(player.getUniqueId()) || this.plugin.getTimerManager().pvpProtectionTimer.getRemaining(player) > 0L) {
            player.sendMessage(ChatColor.RED + "You cannot equip classes that effect PvP while you are protected from pvp" + ChatColor.GRAY + " (" + this.getName() + ")");
            return false;
        }
        if (!super.onEquip(player)) {
            return false;
        }
        final BardData bardData = new BardData();
        this.bardDataMap.put(player.getUniqueId(), bardData);
        bardData.startEnergyTracking();
        bardData.heldTask = new BukkitRunnable() {
            int lastEnergy;
            
            public void run() {
                final ItemStack held = player.getItemInHand();
                if (held != null) {
                    final BardEffect bardEffect = BardClass.this.bardEffects.get(held.getType());
                    if (bardEffect != null && !BardClass.this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
                        final PlayerFaction playerFaction = BardClass.this.plugin.getFactionManager().getPlayerFaction(player);
                        if (playerFaction != null) {
                            final Collection<Entity> nearbyEntities = (Collection<Entity>)player.getNearbyEntities(25.0, 25.0, 25.0);
                            for (final Entity nearby : nearbyEntities) {
                                if (nearby instanceof Player && !player.equals(nearby)) {
                                    final Player target = (Player)nearby;
                                    if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
                                        continue;
                                    }
                                    BardClass.this.bardRestorer.setRestoreEffect(target, bardEffect.heldable);
                                }
                            }
                        }
                    }
                }
                final int energy = (int)BardClass.this.getEnergy(player);
                if (energy != 0 && energy != this.lastEnergy && (energy % 10 == 0 || this.lastEnergy - energy - 1 > 0 || energy == 120.0)) {
                    this.lastEnergy = energy;
                    player.sendMessage(ChatColor.AQUA + BardClass.this.name + " Energy: " + ChatColor.YELLOW + energy);
                }
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 20L);
        return true;
    }
    
    @Override
    public void onUnequip(final Player player) {
        super.onUnequip(player);
        this.clearBardData(player.getUniqueId());
    }
    
    private void clearBardData(final UUID uuid) {
        final BardData bardData = this.bardDataMap.remove(uuid);
        if (bardData != null && bardData.heldTask != null) {
            bardData.heldTask.cancel();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.clearBardData(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.clearBardData(event.getPlayer().getUniqueId());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
        if (equipped == null || !equipped.equals(this)) {
            return;
        }
        final UUID uuid = player.getUniqueId();
        final long lastMessage = this.msgCooldowns.get((Object)uuid);
        final long millis = System.currentTimeMillis();
        if (lastMessage != this.msgCooldowns.getNoEntryValue() && lastMessage - millis > 0L) {
            return;
        }
        final ItemStack newStack = player.getInventory().getItem(event.getNewSlot());
        if (newStack != null) {
            final BardEffect bardEffect = this.bardEffects.get(newStack.getType());
        }
    }
    
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (!event.hasItem()) {
            return;
        }
        final Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_AIR || (!event.isCancelled() && action == Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack stack = event.getItem();
            final BardEffect bardEffect = this.bardEffects.get(stack.getType());
            if (bardEffect == null || bardEffect.clickable == null) {
                return;
            }
            event.setUseItemInHand(Event.Result.DENY);
            final Player player = event.getPlayer();
            final BardData bardData = this.bardDataMap.get(player.getUniqueId());
            if (bardData != null) {
                if (!this.canUseBardEffect(player, bardData, bardEffect, true)) {
                    return;
                }
                if (stack.getAmount() > 1) {
                    stack.setAmount(stack.getAmount() - 1);
                }
                else {
                    player.setItemInHand(new ItemStack(Material.AIR, 1));
                }
                if (bardEffect != null && !this.plugin.getFactionManager().getFactionAt(player.getLocation()).isSafezone()) {
                    final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                    if (playerFaction != null && !bardEffect.clickable.getType().equals((Object)PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = (Collection<Entity>)player.getNearbyEntities(25.0, 25.0, 25.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                if (!playerFaction.getMembers().containsKey(target.getUniqueId())) {
                                    continue;
                                }
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                    else if (playerFaction != null && bardEffect.clickable.getType().equals((Object)PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = (Collection<Entity>)player.getNearbyEntities(25.0, 25.0, 25.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                if (playerFaction.getMembers().containsKey(target.getUniqueId())) {
                                    continue;
                                }
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                    else if (bardEffect.clickable.getType().equals((Object)PotionEffectType.WITHER)) {
                        final Collection<Entity> nearbyEntities = (Collection<Entity>)player.getNearbyEntities(25.0, 25.0, 25.0);
                        for (final Entity nearby : nearbyEntities) {
                            if (nearby instanceof Player && !player.equals(nearby)) {
                                final Player target = (Player)nearby;
                                this.bardRestorer.setRestoreEffect(target, bardEffect.clickable);
                            }
                        }
                    }
                }
                this.bardRestorer.setRestoreEffect(player, bardEffect.clickable);
                final double newEnergy = this.setEnergy(player, bardData.getEnergy() - bardEffect.energyCost);
                bardData.buffCooldown = System.currentTimeMillis() + BardClass.BUFF_COOLDOWN_MILLIS;
                player.sendMessage(ChatColor.YELLOW + "You have just used " + this.name + " buff " + ChatColor.AQUA + Lang.fromPotionEffectType(bardEffect.clickable.getType()) + ' ' + (bardEffect.clickable.getAmplifier() + 1) + ChatColor.YELLOW + " costing you " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.YELLOW + " energy. " + "Your energy is now " + ChatColor.GREEN + newEnergy * 10.0 / 10.0 + ChatColor.YELLOW + '.');
            }
        }
    }
    
    private boolean canUseBardEffect(final Player player, final BardData bardData, final BardEffect bardEffect, final boolean sendFeedback) {
        String errorFeedback = null;
        final double currentEnergy = bardData.getEnergy();
        if (bardEffect.energyCost > currentEnergy) {
            errorFeedback = ChatColor.RED + "You need at least " + ChatColor.BOLD + bardEffect.energyCost + ChatColor.RED + " energy to use this Bard buff, whilst you only have " + ChatColor.BOLD + currentEnergy + ChatColor.RED + '.';
        }
        final long remaining = bardData.getRemainingBuffDelay();
        if (remaining > 0L) {
            errorFeedback = ChatColor.RED + "You still have a cooldown on this " + ChatColor.GREEN + ChatColor.BOLD + "Bard" + ChatColor.RED + " buff for another " + HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.';
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
        if (factionAt.isSafezone()) {
            errorFeedback = ChatColor.RED + "You may not use Bard buffs in safe-zones.";
        }
        if (sendFeedback && errorFeedback != null) {
            player.sendMessage(errorFeedback);
        }
        return errorFeedback == null;
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final ItemStack helmet = player.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.GOLD_HELMET) {
            return false;
        }
        final ItemStack chestplate = player.getInventory().getChestplate();
        if (chestplate == null || chestplate.getType() != Material.GOLD_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = player.getInventory().getLeggings();
        if (leggings == null || leggings.getType() != Material.GOLD_LEGGINGS) {
            return false;
        }
        final ItemStack boots = player.getInventory().getBoots();
        return boots != null && boots.getType() == Material.GOLD_BOOTS;
    }
    
    public long getRemainingBuffDelay(final Player player) {
        synchronized (this.bardDataMap) {
            final BardData bardData = this.bardDataMap.get(player.getUniqueId());
            return (bardData == null) ? 0L : bardData.getRemainingBuffDelay();
        }
    }
    
    public double getEnergy(final Player player) {
        synchronized (this.bardDataMap) {
            final BardData bardData = this.bardDataMap.get(player.getUniqueId());
            return (bardData == null) ? 0.0 : bardData.getEnergy();
        }
    }
    
    public long getEnergyMillis(final Player player) {
        synchronized (this.bardDataMap) {
            final BardData bardData = this.bardDataMap.get(player.getUniqueId());
            return (bardData == null) ? 0L : bardData.getEnergyMillis();
        }
    }
    
    public double setEnergy(final Player player, final double energy) {
        final BardData bardData = this.bardDataMap.get(player.getUniqueId());
        if (bardData == null) {
            return 0.0;
        }
        bardData.setEnergy(energy);
        return bardData.getEnergy();
    }
    
    static {
        BUFF_COOLDOWN_MILLIS = TimeUnit.SECONDS.toMillis(8L);
        MARK = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 8);
    }
}
