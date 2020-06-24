package com.doctordark.hcf.listener;

import java.util.Iterator;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Location;
import org.bukkit.Sound;
import net.minecraft.server.v1_7_R4.Packet;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_7_R4.World;
import net.minecraft.server.v1_7_R4.EntityLightning;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.Bukkit;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.util.JavaUtils;

import org.bukkit.ChatColor;
import java.util.concurrent.TimeUnit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import java.util.UUID;
import java.util.HashMap;
import org.bukkit.event.Listener;

public class DeathListener implements Listener
{
    private static final long BASE_REGEN_DELAY;
    public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
    public static HashMap<UUID, ItemStack[]> PlayerArmorContents;
    private final HCF plugin;
    
    public DeathListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeathKillIncrement(final PlayerDeathEvent event) {
    	
        final Player killer = event.getEntity().getKiller();
        if (killer != null) {
            final FactionUser user = this.plugin.getUserManager().getUser(killer.getUniqueId());
            user.setKills(user.getKills() + 1);
            HCF.getPlugin().getFactionManager().getPlayerFaction(user.getUserUUID()).setPoints(HCF.getPlugin().getFactionManager().getPlayerFaction(killer.getUniqueId()).getPoints() + 10);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if (playerFaction != null) {
            final Faction factionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            final Role role = playerFaction.getMember(player.getUniqueId()).getRole();
            playerFaction.removePoints(10);
            if (playerFaction.getDeathsUntilRaidable() >= -5.0) {
                playerFaction.setDeathsUntilRaidable(playerFaction.getDeathsUntilRaidable() - factionAt.getDtrLossMultiplier());
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.YELLOW + ". DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format((Number)playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
               
            }
            else {
                playerFaction.setRemainingRegenerationTime(DeathListener.BASE_REGEN_DELAY + playerFaction.getOnlinePlayers().size() * TimeUnit.MINUTES.toMillis(2L));
                playerFaction.broadcast(ChatColor.YELLOW + "Member Death: " + ConfigurationService.TEAMMATE_COLOUR + role.getAstrix() + player.getName() + ChatColor.YELLOW + ". DTR:" + ChatColor.GRAY + " [" + playerFaction.getDtrColour() + JavaUtils.format((Number)playerFaction.getDeathsUntilRaidable()) + ChatColor.WHITE + '/' + ChatColor.WHITE + playerFaction.getMaximumDeathsUntilRaidable() + ChatColor.GRAY + "].");
        
            }
        }
        if (Bukkit.spigot().getTPS()[0] > 15.0) {
            DeathListener.PlayerInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
            DeathListener.PlayerArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
            final Location location = player.getLocation();
            final WorldServer worldServer = ((CraftWorld)location.getWorld()).getHandle();
            final EntityLightning entityLightning = new EntityLightning((World)worldServer, location.getX(), location.getY(), location.getZ(), false);
            final PacketPlayOutSpawnEntityWeather packet = new PacketPlayOutSpawnEntityWeather((Entity)entityLightning);
            for (final Player target : Bukkit.getOnlinePlayers()) {
                if (this.plugin.getUserManager().getUser(target.getUniqueId()).isShowLightning()) {
                    ((CraftPlayer)target).getHandle().playerConnection.sendPacket((Packet)packet);
                    target.playSound(target.getLocation(), Sound.AMBIENCE_THUNDER, 1.0f, 1.0f);
                }
            }
        }
    }
    
    static {
        DeathListener.PlayerInventoryContents = new HashMap<UUID, ItemStack[]>();
        DeathListener.PlayerArmorContents = new HashMap<UUID, ItemStack[]>();
        BASE_REGEN_DELAY = TimeUnit.MINUTES.toMillis(40L);
    }
}
