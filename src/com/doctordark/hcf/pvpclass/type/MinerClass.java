package com.doctordark.hcf.pvpclass.type;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.PvpClass;
import com.doctordark.hcf.pvpclass.event.PvpClassEquipEvent;
import com.doctordark.util.BukkitUtils;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import java.util.concurrent.TimeUnit;

import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;

public class MinerClass extends PvpClass implements Listener
{
    private static final int INVISIBILITY_HEIGHT_LEVEL = 30;
    private static final PotionEffect HEIGHT_INVISIBILITY;
    private final HCF plugin;
    
    public MinerClass(final HCF plugin) {
        super("Miner", TimeUnit.SECONDS.toMillis(10L));
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }
    
    private void removeInvisibilitySafely(final Player player) {
        for (final PotionEffect active : player.getActivePotionEffects()) {
            if (active.getType().equals((Object)PotionEffectType.INVISIBILITY) && active.getDuration() > MinerClass.DEFAULT_MAX_DURATION) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste disabled.");
                player.removePotionEffect(active.getType());
                break;
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if (entity instanceof Player && BukkitUtils.getFinalAttacker((EntityDamageEvent)event, false) != null) {
            final Player player = (Player)entity;
            if (this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
                this.removeInvisibilitySafely(player);
            }
        }
    }
    
    @Override
    public void onUnequip(final Player player) {
        super.onUnequip(player);
        this.removeInvisibilitySafely(player);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onClassEquip(final PvpClassEquipEvent event) {
        final Player player = event.getPlayer();
        if (event.getPvpClass().equals(this)) {
            player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
            player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
        }
    }
    
    private void conformMinerInvisibility(final Player player, final Location from, final Location to) {
        final int fromY = from.getBlockY();
        final int toY = to.getBlockY();
        if (fromY != toY && this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
            final boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if (toY > 30) {
                if (fromY <= 30 && isInvisible) {
                    this.removeInvisibilitySafely(player);
                }
            }
            else if (!isInvisible) {
                player.addPotionEffect(MinerClass.HEIGHT_INVISIBILITY, true);
                player.sendMessage(ChatColor.LIGHT_PURPLE + this.getName() + ChatColor.YELLOW + " invisibility and haste enabled.");
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.IRON_HELMET || !helmet.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE || !chestplate.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.IRON_LEGGINGS || !leggings.getEnchantments().isEmpty()) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS && boots.getEnchantments().isEmpty();
    }
    
    static {
        HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
    }
}
