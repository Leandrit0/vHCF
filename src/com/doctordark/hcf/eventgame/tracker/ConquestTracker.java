package com.doctordark.hcf.eventgame.tracker;

import java.util.concurrent.TimeUnit;
import org.bukkit.event.entity.PlayerDeathEvent;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.Iterator;
import java.util.List;
import com.google.common.collect.Ordering;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventTimer;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.eventgame.faction.ConquestFaction;
import com.doctordark.hcf.eventgame.faction.EventFaction;
import com.doctordark.hcf.faction.event.FactionRemoveEvent;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Comparator;
import org.bukkit.event.Listener;

@Deprecated
public class ConquestTracker implements EventTracker, Listener
{
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private static final Comparator<Map.Entry<PlayerFaction, Integer>> POINTS_COMPARATOR;
    private final Map<PlayerFaction, Integer> factionPointsMap;
    private final HCF plugin;
    
    public ConquestTracker(final HCF ins) {
        this.factionPointsMap = Collections.<PlayerFaction, Integer>synchronizedMap(new LinkedHashMap<PlayerFaction, Integer>());
        this.plugin = ins;
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)this.plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRemove(final FactionRemoveEvent event) {
        final Faction faction = event.getFaction();
        if (faction instanceof PlayerFaction) {
            synchronized (this.factionPointsMap) {
                this.factionPointsMap.remove(faction);
            }
        }
    }
    
    public Map<PlayerFaction, Integer> getFactionPointsMap() {
        return (Map<PlayerFaction, Integer>)ImmutableMap.copyOf((Map)this.factionPointsMap);
    }
    
    public int getPoints(final PlayerFaction faction) {
        synchronized (this.factionPointsMap) {
            return (int)MoreObjects.firstNonNull((Object)this.factionPointsMap.get(faction), (Object)0);
        }
    }
    
    public int setPoints(final PlayerFaction faction, final int amount) {
        if (amount < 0) {
            return amount;
        }
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.put(faction, amount);
            final List<Map.Entry<PlayerFaction, Integer>> entries = (List<Map.Entry<PlayerFaction, Integer>>)Ordering.from((Comparator)ConquestTracker.POINTS_COMPARATOR).sortedCopy((Iterable)this.factionPointsMap.entrySet());
            this.factionPointsMap.clear();
            for (final Map.Entry<PlayerFaction, Integer> entry : entries) {
                this.factionPointsMap.put(entry.getKey(), entry.getValue());
            }
        }
        return amount;
    }
    
    public int takePoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) - amount);
    }
    
    public int addPoints(final PlayerFaction faction, final int amount) {
        return this.setPoints(faction, this.getPoints(faction) + amount);
    }
    
    @Override
    public EventType getEventType() {
        return EventType.CONQUEST;
    }
    
    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final ConquestFaction conquestFaction = (ConquestFaction)eventFaction;
        final List<CaptureZone> captureZones = conquestFaction.getCaptureZones();
        for (final CaptureZone captureZone : captureZones) {
            final Player cappingPlayer = captureZone.getCappingPlayer();
            if (cappingPlayer == null) {
                continue;
            }
            final long remainingMillis = captureZone.getRemainingCaptureMillis();
            if (remainingMillis <= 0L) {
                final UUID uuid = cappingPlayer.getUniqueId();
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(uuid);
                if (playerFaction != null) {
                    final int newPoints = this.addPoints(playerFaction, 1);
                    if (newPoints >= 300) {
                        synchronized (this.factionPointsMap) {
                            this.factionPointsMap.clear();
                        }
                        this.plugin.getTimerManager().eventTimer.handleWinner(cappingPlayer);
                        return;
                    }
                    captureZone.setRemainingCaptureMillis(captureZone.getDefaultCaptureMillis());
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.GOLD + " gained " + 1 + " point for capturing " + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.AQUA + '(' + newPoints + '/' + 300 + ')');
                }
                return;
            }
            final int remainingSeconds = (int)Math.round(remainingMillis / 1000.0);
            if (remainingSeconds % 5 != 0) {
                continue;
            }
            cappingPlayer.sendMessage(ChatColor.YELLOW + "[" + eventFaction.getName() + "] " + ChatColor.GOLD + "Attempting to control " + ChatColor.YELLOW + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.YELLOW + '(' + remainingSeconds + "s)");
        }
    }
    
    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + "Conquest" + "] " + ChatColor.GOLD + eventFaction.getName() + " can now be contested.");
    }
    
    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if (this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture for Conquest.");
            return false;
        }
        return true;
    }
    
    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > ConquestTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + "Conquest" + "] " + ChatColor.GOLD + player.getName() + " was knocked off " + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
        synchronized (this.factionPointsMap) {
            this.factionPointsMap.clear();
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Faction currentEventFac = this.plugin.getTimerManager().eventTimer.getEventFaction();
        if (currentEventFac instanceof ConquestFaction) {
            final Player player = event.getEntity();
            final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
            if (playerFaction != null) {
                final int oldPoints = this.getPoints(playerFaction);
                if (oldPoints == 0) {
                    return;
                }
                if (this.getPoints(playerFaction) <= 20) {
                    this.setPoints(playerFaction, 0);
                }
                else {
                    this.takePoints(playerFaction, 20);
                }
                Bukkit.broadcastMessage(this.getPoints(playerFaction) + "");
                event.setDeathMessage(ChatColor.YELLOW + "[" + "Conquest" + "] " + ChatColor.LIGHT_PURPLE + playerFaction.getName() + ChatColor.GOLD + " lost " + ChatColor.BOLD + Math.min(20, oldPoints) + ChatColor.GOLD + " points because " + player.getName() + " died." + ChatColor.AQUA + " (" + this.getPoints(playerFaction) + '/' + 300 + ')' + ChatColor.YELLOW + '.');
            }
        }
    }
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(5L);
        DEFAULT_CAP_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        POINTS_COMPARATOR = ((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
    }
}
