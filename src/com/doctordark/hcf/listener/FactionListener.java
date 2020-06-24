package com.doctordark.hcf.listener;

import java.util.concurrent.TimeUnit;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.faction.KothFaction;
import com.doctordark.hcf.faction.event.CaptureZoneEnterEvent;
import com.doctordark.hcf.faction.event.CaptureZoneLeaveEvent;
import com.doctordark.hcf.faction.event.FactionCreateEvent;
import com.doctordark.hcf.faction.event.FactionRemoveEvent;
import com.doctordark.hcf.faction.event.FactionRenameEvent;
import com.doctordark.hcf.faction.event.PlayerClaimEnterEvent;
import com.doctordark.hcf.faction.event.PlayerJoinFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeaveFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeftFactionEvent;
import com.doctordark.hcf.faction.struct.RegenStatus;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.google.common.base.Optional;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.Listener;

public class FactionListener implements Listener
{
    private static final long FACTION_JOIN_WAIT_MILLIS;
    private static final String FACTION_JOIN_WAIT_WORDS;
    private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;
    private final HCF plugin;
    
    public FactionListener(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionCreate(final FactionCreateEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("faction.create_message").replace("%faction%", event.getFaction().getName()).replace("%player%", sender.getName())));
           
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final CommandSender sender = event.getSender();
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("faction.disband_message").replace("%faction%", event.getFaction().getName()).replace("%player%", sender.getName())));

        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            Bukkit.broadcastMessage(ChatColor.RED + event.getOriginalName() + ChatColor.YELLOW + " has been renamed to " + ChatColor.RED + "" + event.getNewName() + ChatColor.YELLOW + " by " + ChatColor.WHITE + event.getSender().getName());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRenameMonitor(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof KothFaction) {
            ((KothFaction)faction).getCaptureZone().setName(event.getNewName());
        }
    }
    
    private long getLastLandChangedMeta(final Player player) {
        final MetadataValue value = player.getMetadata("landChangedMessage", (Plugin)this.plugin);
        final long millis = System.currentTimeMillis();
        final long remaining = (value == null) ? 0L : (value.asLong() - millis);
        if (remaining <= 0L) {
            player.setMetadata("landChangedMessage", (MetadataValue)new FixedMetadataValue((Plugin)this.plugin, (Object)(millis + 225L)));
        }
        return remaining;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneEnter(final CaptureZoneEnterEvent event) {
        final Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.YELLOW + "Now entering capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCaptureZoneLeave(final CaptureZoneLeaveEvent event) {
        final Player player = event.getPlayer();
        if (this.getLastLandChangedMeta(player) <= 0L && this.plugin.getUserManager().getUser(player.getUniqueId()).isCapzoneEntryAlerts()) {
            player.sendMessage(ChatColor.YELLOW + "Now leaving capture zone: " + event.getCaptureZone().getDisplayName() + ChatColor.YELLOW + '(' + event.getFaction().getName() + ChatColor.YELLOW + ')');
        }
    }
    
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event)
    {
      Faction toFaction = event.getToFaction();
      Faction fromFaction = event.getFromFaction();
      
      if (toFaction.isSafezone())
      {
        Player player = event.getPlayer();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setSaturation(4.0F);
      }
      Player player = event.getPlayer();
      if (getLastLandChangedMeta(player) > 0L) {
        return;
      }
      player.sendMessage(ChatColor.YELLOW + "Now leaving: " + fromFaction.getDisplayName((CommandSender)player) + ChatColor.YELLOW + '(' + (fromFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.YELLOW + ')');
      player.sendMessage(ChatColor.YELLOW + "Now entering: " + toFaction.getDisplayName((CommandSender)player) + ChatColor.YELLOW + '(' + (toFaction.isDeathban() ? (ChatColor.RED + "Deathban") : (ChatColor.GREEN + "Non-Deathban")) + ChatColor.YELLOW + ')');
    }  

    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        final Optional<Player> optionalPlayer = event.getPlayer();
        if (optionalPlayer.isPresent()) {
            this.plugin.getUserManager().getUser(((Player)optionalPlayer.get()).getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPreFactionJoin(final PlayerJoinFactionEvent event) {
        final Faction faction = event.getFaction();
        final Optional optionalPlayer = event.getPlayer();
        if (faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            final Player player = (Player)optionalPlayer.get();
            final PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
                return;
            }
            final long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FactionListener.FACTION_JOIN_WAIT_MILLIS;
            if (difference > 0L && !player.hasPermission("hcf.faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within " + FactionListener.FACTION_JOIN_WAIT_WORDS + ". " + "You gotta wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(final PlayerLeaveFactionEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            final Optional optional = event.getPlayer();
            if (optional.isPresent()) {
                final Player player = (Player)optional.get();
                if (this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst you remain in its' territory.");
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.printDetails((CommandSender)player);
            playerFaction.broadcast(ChatColor.GOLD + "Member Online: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.', player.getUniqueId());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction != null) {
            playerFaction.broadcast(ChatColor.GOLD + "Member Offline: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
        }
    }
    
    static {
        FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FactionListener.FACTION_JOIN_WAIT_MILLIS, true, true);
    }
}
