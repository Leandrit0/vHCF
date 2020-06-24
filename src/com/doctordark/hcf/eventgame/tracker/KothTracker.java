package com.doctordark.hcf.eventgame.tracker;

import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

import com.doctordark.hcf.DateTimeFormats;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventTimer;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.eventgame.faction.EventFaction;
import com.doctordark.hcf.eventgame.faction.KothFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Deprecated
public class KothTracker implements EventTracker
{
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;
    private final HCF plugin;
    
    public KothTracker(final HCF plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final CaptureZone captureZone = ((KothFaction)eventFaction).getCaptureZone();
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if (remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        final int remainingSeconds = (int)(remainingMillis / 1000L);
        if (remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.GOLD + "Someone is controlling " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }
    
    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + eventFaction.getName() + ChatColor.GOLD + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }
    
    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        player.sendMessage(ChatColor.GOLD + "You are now in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        return true;
    }
    
    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        player.sendMessage(ChatColor.GOLD + "You are no longer in control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.');
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if (remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.GOLD + " has lost control of " + ChatColor.LIGHT_PURPLE + captureZone.getDisplayName() + ChatColor.GOLD + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
        }
        return true;
    }
    
    @Override
    public void stopTiming() {
    }
    
    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
    }
}
