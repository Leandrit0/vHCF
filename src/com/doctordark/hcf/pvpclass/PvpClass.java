package com.doctordark.hcf.pvpclass;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import lombok.Getter;

public abstract class PvpClass
{
    public static final long DEFAULT_MAX_DURATION;
    protected final Set<PotionEffect> passiveEffects;
   @Getter protected final String name;
   @Getter protected final long warmupDelay;
    
    public PvpClass(final String name, final long warmupDelay) {
        this.passiveEffects = new HashSet<PotionEffect>();
        this.name = name;
        this.warmupDelay = warmupDelay;
    }
    


    
    public boolean onEquip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            player.addPotionEffect(effect, true);
        }
        player.sendMessage(ChatColor.YELLOW + "Class " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " has been equipped.");
        return true;
    }
    
    public void onUnequip(final Player player) {
        for (final PotionEffect effect : this.passiveEffects) {
            for (final PotionEffect active : player.getActivePotionEffects()) {
                if (active.getDuration() > PvpClass.DEFAULT_MAX_DURATION && active.getType().equals((Object)effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
        player.sendMessage(ChatColor.YELLOW + "Class " + ChatColor.LIGHT_PURPLE + this.name + ChatColor.YELLOW + " has been un-equipped.");
    }
    
    public abstract boolean isApplicableFor(final Player p0);
    
    static {
        DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
    }
}
