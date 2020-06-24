package com.doctordark.hcf.eventgame.faction;

import org.bukkit.entity.Player;
import org.bukkit.Location;
import java.util.Iterator;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class KothFaction extends CapturableFaction implements ConfigurationSerializable
{
    private CaptureZone captureZone;
    
    public KothFaction(final String name) {
        super(name);
        this.setDeathban(true);
    }
    
    public KothFaction(final Map<String, Object> map) {
        super(map);
        this.setDeathban(true);
        this.captureZone = (CaptureZone) map.get("captureZone");
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("captureZone", this.captureZone);
        return map;
    }
    
    public List<CaptureZone> getCaptureZones() {
        return ((this.captureZone == null) ? ImmutableList.of() : ImmutableList.of(this.captureZone));
    }
    
    public EventType getEventType() {
        return EventType.KOTH;
    }
    
    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender));
        for (final Claim claim : this.claims) {
            final Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.RED + '(' + (String)KothFaction.ENVIRONMENT_MAPPINGS.get((Object)location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ() + ')');
        }
        if (this.captureZone != null) {
            final long remainingCaptureMillis = this.captureZone.getRemainingCaptureMillis();
            final long defaultCaptureMillis = this.captureZone.getDefaultCaptureMillis();
            if (remainingCaptureMillis > 0L && remainingCaptureMillis != defaultCaptureMillis) {
                sender.sendMessage(ChatColor.YELLOW + "  Remaining Time: " + ChatColor.RED + DurationFormatUtils.formatDurationWords(remainingCaptureMillis, true, true));
            }
            sender.sendMessage(ChatColor.YELLOW + "  Capture Delay: " + ChatColor.RED + this.captureZone.getDefaultCaptureWords());
            if (this.captureZone.getCappingPlayer() != null && sender.hasPermission("hcf.koth.checkcapper")) {
                final Player capping = this.captureZone.getCappingPlayer();
                final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(capping);
                final String factionTag = "[" + ((playerFaction == null) ? "*" : playerFaction.getName()) + "]";
                sender.sendMessage(ChatColor.YELLOW + "  Current Capper: " + ChatColor.RED + capping.getName() + ChatColor.GOLD + factionTag);
            }
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public CaptureZone getCaptureZone() {
        return this.captureZone;
    }
    
    public void setCaptureZone(final CaptureZone captureZone) {
        this.captureZone = captureZone;
    }
}
