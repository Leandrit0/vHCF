package com.doctordark.hcf.listener.fixes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.event.Listener;

public class BeaconStrengthFixListener implements Listener
{
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionEffectAdd(final PotionEffectAddEvent event) {
        final LivingEntity entity = event.getEntity();
        if (entity instanceof Player && event.getCause() == PotionEffectAddEvent.EffectCause.BEACON) {
            final PotionEffect effect = event.getEffect();
            if (effect.getAmplifier() > 1 && effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
                entity.addPotionEffect(new PotionEffect(effect.getType(), effect.getDuration(), 0, effect.isAmbient()));
                event.setCancelled(true);
            }
        }
    }
}
