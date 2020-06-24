package com.doctordark.hcf.pvpclass.archer;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import java.util.Iterator;
import org.bukkit.projectiles.ProjectileSource;
import java.util.Collection;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.doctordark.hcf.Cooldowns;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.PvpClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.entity.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionEffectType;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import java.util.concurrent.TimeUnit;

import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.Listener;

public class ArcherClass extends PvpClass implements Listener
{
    private static final PotionEffect ARCHER_CRITICAL_EFFECT;
    private static final int MARK_TIMEOUT_SECONDS = 15;
    private static final int MARK_EXECUTION_LEVEL = 3;
    private static final float MINIMUM_FORCE = 0.5f;
    private static final String ARROW_FORCE_METADATA = "ARROW_FORCE";
    public static final HashMap<UUID, UUID> tagged;
    private static final PotionEffect ARCHER_SPEED_EFFECT;
    private static final HashMap<UUID, Long> ARCHER_COOLDOWN;
    private static final long ARCHER_SPEED_COOLDOWN_DELAY;
    private final TObjectLongMap<UUID> archerSpeedCooldowns;
    private final HCF plugin;
    
    public ArcherClass(final HCF plugin) {
        super("Archer", TimeUnit.SECONDS.toMillis(1L));
        this.archerSpeedCooldowns = (TObjectLongMap<UUID>)new TObjectLongHashMap();
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityShootBow(final EntityShootBowEvent event) {
        final Entity projectile = event.getProjectile();
        if (projectile instanceof Arrow) {
            projectile.setMetadata("ARROW_FORCE", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)event.getForce()));
        }
    }
    
    @EventHandler
    public void onPlayerClickSugar(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (this.plugin.getPvpClassManager().getEquippedClass(p) != null && this.plugin.getPvpClassManager().getEquippedClass(p).equals(this) && p.getItemInHand().getType() == Material.SUGAR) {
            if (Cooldowns.isOnCooldown("Archer_item_cooldown", p)) {
                p.sendMessage(ChatColor.RED + "You are still on a cooldown for another: " + ChatColor.DARK_RED.toString() + Cooldowns.getCooldownForPlayerInt("Archer_item_cooldown", p) + ChatColor.RED.toString() + " seconds");
                e.setCancelled(true);
                return;
            }
            Cooldowns.addCooldown("Archer_item_cooldown", p, 25);
            p.sendMessage(ChatColor.AQUA.toString() + ChatColor.BOLD + "Speed 5 now active");
            if (p.getItemInHand().getAmount() == 1) {
                p.getInventory().remove(p.getItemInHand());
            }
            else {
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
            }
            p.removePotionEffect(PotionEffectType.SPEED);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 120, 4));
            new BukkitRunnable() {
                public void run() {
                    if (ArcherClass.this.isApplicableFor(p)) {
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 120L);
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        if (ArcherClass.tagged.containsKey(e.getPlayer().getUniqueId())) {
            ArcherClass.tagged.remove(e.getPlayer().getUniqueId());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow)damager;
            final ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                final Player damaged = (Player)event.getEntity();
                final Player shooter = (Player)source;
                final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }
                if (this.plugin.getTimerManager().archerTimer.getRemaining((Player)entity) == 0L) {
                    if (this.plugin.getPvpClassManager().getEquippedClass(damaged) != null && this.plugin.getPvpClassManager().getEquippedClass(damaged).equals(this)) {
                        return;
                    }
                    this.plugin.getTimerManager().archerTimer.setCooldown((Player)entity, entity.getUniqueId());
                    ArcherClass.tagged.put(damaged.getUniqueId(), shooter.getUniqueId());
                    Player[] arrayOfPlayer;
                    int j = (arrayOfPlayer = Bukkit.getOnlinePlayers()).length;
                    for (int i = 0; i < j; i++) {
                        Player localPlayer1 = arrayOfPlayer[i];
                    }
                    shooter.sendMessage(ChatColor.YELLOW + "You have hit " + ChatColor.AQUA + damaged.getName() + ChatColor.YELLOW + " and have archer tagged");
                    damaged.sendMessage(ChatColor.YELLOW + "You have been archer tagged by " + ChatColor.AQUA + shooter.getName());
                }
            }
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.LEATHER_BOOTS;
    }
    
    static {
        tagged = new HashMap<UUID, UUID>();
        ARCHER_COOLDOWN = new HashMap<UUID, Long>();
        ARCHER_CRITICAL_EFFECT = new PotionEffect(PotionEffectType.WITHER, 60, 0);
        ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 160, 3);
        ARCHER_SPEED_COOLDOWN_DELAY = TimeUnit.MINUTES.toMillis(1L);
    }
}
