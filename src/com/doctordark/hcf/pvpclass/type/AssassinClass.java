package com.doctordark.hcf.pvpclass.type;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;

import com.doctordark.hcf.Cooldowns;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.PvpClass;
import com.doctordark.hcf.pvpclass.event.PvpClassUnequipEvent;
import com.doctordark.hcf.timer.PlayerTimer;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Sound;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import java.util.Iterator;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffectType;
import java.util.concurrent.TimeUnit;

import org.bukkit.potion.PotionEffect;
import java.util.HashMap;

import org.bukkit.event.Listener;

public class AssassinClass extends PvpClass implements Listener
{
    private final HCF plugin;
    public HashMap<String, Integer> firstAssassinEffects;
    public HashMap<Integer, PotionEffect> modes;
    private PlayerTimer pt;
    
    public AssassinClass(final HCF plugin) {
        super("Reaper", TimeUnit.SECONDS.toMillis(3L));
        this.firstAssassinEffects = new HashMap<String, Integer>();
        this.modes = new HashMap<Integer, PotionEffect>();
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    
    @EventHandler
    public void onUnEquip(final PvpClassUnequipEvent e) {
        final Player p = e.getPlayer();
        for (final Player on : Bukkit.getOnlinePlayers()) {
            if (!on.canSee(p) && !on.hasPermission("base.command.vanish")) {
                on.showPlayer(p);
            }
        }
        this.firstAssassinEffects.remove(p);
    }
    
    @EventHandler
    public void onDamageSelf(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player)e.getEntity();
            if (this.plugin.getPvpClassManager().getEquippedClass(p) == null || !this.plugin.getPvpClassManager().getEquippedClass(p).equals(this)) {
                return;
            }
            if (this.firstAssassinEffects.containsKey(p.getName()) && this.firstAssassinEffects.get(p.getName()) == 1) {
                for (final Entity entity : p.getNearbyEntities(20.0, 20.0, 20.0)) {
                    if (entity instanceof Player) {
                        final Player players = (Player)entity;
                        players.sendMessage(ChatColor.YELLOW + "An reaper has taken damage in stealth mode near you: " + ChatColor.GRAY + ChatColor.ITALIC + "(20 x 20)");
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onHitOtherPlayers(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player p = (Player)e.getDamager();
            final Player ent = (Player)e.getEntity();
            if (this.firstAssassinEffects.containsKey(p.getName()) && this.firstAssassinEffects.get(p.getName()) == 1) {
                this.afterFiveSeconds(p, true);
            }
        }
    }
    
    @EventHandler
    public void onClickItem(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(p);
            if (equipped == null || !equipped.equals(this)) {
                return;
            }
            if (p.getItemInHand().getType() == Material.QUARTZ) {
                if (Cooldowns.isOnCooldown("Assassin_item_cooldown", p)) {
                    p.sendMessage(ChatColor.RED + "You still have an " + ChatColor.GREEN + ChatColor.BOLD + "Reaper" + ChatColor.RED + " cooldown for another " + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Assassin_item_cooldown", p), true) + ChatColor.RED + '.');
                    return;
                }
                if (p.getItemInHand().getAmount() == 1) {
                    p.getInventory().remove(p.getItemInHand());
                }
                p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GRAY + "Stealth" + ChatColor.YELLOW + " Mode");
                for (final Player on : Bukkit.getOnlinePlayers()) {
                    on.playEffect(p.getLocation().add(0.5, 2.0, 0.5), Effect.ENDER_SIGNAL, 5);
                    on.playEffect(p.getLocation().add(0.5, 1.5, 0.5), Effect.ENDER_SIGNAL, 5);
                    on.playEffect(p.getLocation().add(0.5, 1.0, 0.5), Effect.ENDER_SIGNAL, 5);
                    on.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
                    if (!on.hasPermission("base.command.vanish")) {
                        on.hidePlayer(p);
                    }
                }
                Cooldowns.addCooldown("Assassin_item_cooldown", p, 60);
                p.removePotionEffect(PotionEffectType.SPEED);
                this.firstAssassinEffects.put(p.getName(), 1);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0), true);
                new BukkitRunnable() {
                    public void run() {
                        if (AssassinClass.this.isApplicableFor(p) && AssassinClass.this.firstAssassinEffects.containsKey(p.getName()) && AssassinClass.this.firstAssassinEffects.get(p.getName()) == 1) {
                            AssassinClass.this.afterFiveSeconds(p, false);
                        }
                    }
                }.runTaskLater((Plugin)this.plugin, 100L);
            }
        }
    }
    
    public void afterFiveSeconds(final Player p, final boolean force) {
        if (this.firstAssassinEffects.containsKey(p.getName()) && this.isApplicableFor(p)) {
            for (final Player on : Bukkit.getOnlinePlayers()) {
                if (!on.canSee(p) && !on.hasPermission("base.command.vanish")) {
                    on.showPlayer(p);
                }
                on.playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.ENDER_SIGNAL, 3);
                on.playEffect(p.getLocation().add(0.0, 1.5, 0.0), Effect.ENDER_SIGNAL, 3);
                on.playEffect(p.getLocation().add(0.0, 1.0, 0.0), Effect.ENDER_SIGNAL, 3);
                on.playEffect(p.getLocation().add(0.0, 2.0, 0.0), Effect.BLAZE_SHOOT, 5);
                on.playEffect(p.getLocation().add(0.0, 1.5, 0.0), Effect.BLAZE_SHOOT, 5);
                on.playEffect(p.getLocation().add(0.0, 1.0, 0.0), Effect.BLAZE_SHOOT, 5);
            }
            final BukkitTask task1 = new BukkitRunnable() {
                public void run() {
                    if (AssassinClass.this.firstAssassinEffects.containsKey(p.getName()) && AssassinClass.this.firstAssassinEffects.get(p.getName()) == 2) {
                        AssassinClass.this.firstAssassinEffects.remove(p.getName());
                        p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GREEN + "Normal" + ChatColor.YELLOW + " Mode");
                        if (AssassinClass.this.isApplicableFor(p)) {
                            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
                        }
                    }
                }
            }.runTaskLater((Plugin)this.plugin, 100L);
            if (force) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
                this.firstAssassinEffects.remove(p.getName());
                this.firstAssassinEffects.put(p.getName(), 2);
                p.sendMessage(ChatColor.YELLOW + "You have been forced into " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
                return;
            }
            this.firstAssassinEffects.remove(p.getName());
            this.firstAssassinEffects.put(p.getName(), 2);
            p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
            p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
            p.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
    
    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if (helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if (leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
}
