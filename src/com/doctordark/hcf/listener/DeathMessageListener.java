package com.doctordark.hcf.listener;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.google.common.base.Preconditions;

import org.bukkit.ChatColor;
import net.minecraft.server.v1_7_R4.EntityLiving;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEntity;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.Listener;

public class DeathMessageListener implements Listener
{
    private final HCF plugin;
    
    public DeathMessageListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public static String replaceLast(final String text, final String regex, final String replacement) {
        return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ')', replacement);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final String message = event.getDeathMessage();
        if (message == null || message.isEmpty()) {
            return;
        }
        event.setDeathMessage(this.getDeathMessage(message, (Entity)event.getEntity(), (Entity)this.getKiller(event)));
    }
    
    private CraftEntity getKiller(final PlayerDeathEvent event) {
        final EntityLiving lastAttacker = ((CraftPlayer)event.getEntity()).getHandle().aX();
        return (lastAttacker == null) ? null : lastAttacker.getBukkitEntity();
    }
    
    private String getDeathMessage(String input, final Entity entity, final Entity killer) {
        input = input.replaceFirst("\\[", ChatColor.GOLD + "[" + ChatColor.WHITE);
        input = replaceLast(input, "]", ChatColor.GOLD + "]" + ChatColor.WHITE);
        if (entity != null) {
            input = input.replaceFirst("(?i)" + this.getEntityName(entity), ChatColor.RED + this.getDisplayName(entity) + ChatColor.YELLOW);
        }
        if (killer != null && (entity == null || !killer.equals(entity))) {
            input = input.replaceFirst("(?i)" + this.getEntityName(killer), ChatColor.RED + this.getDisplayName(killer) + ChatColor.YELLOW);
        }
        return input;
    }
    
    private String getEntityName(final Entity entity) {
        Preconditions.checkNotNull((Object)entity, (Object)"Entity cannot be null");
        return (entity instanceof Player) ? ((Player)entity).getName() : ((CraftEntity)entity).getHandle().getName();
    }
    
    private String getDisplayName(final Entity entity) {
        Preconditions.checkNotNull((Object)entity, (Object)"Entity cannot be null");
        if (entity instanceof Player) {
            final Player player = (Player)entity;
            return player.getName() + ChatColor.GOLD + '[' + ChatColor.WHITE + this.plugin.getUserManager().getUser(player.getUniqueId()).getKills() + ChatColor.GOLD + ']';
        }
        return WordUtils.capitalizeFully(entity.getType().name().replace('_', ' '));
    }
}
