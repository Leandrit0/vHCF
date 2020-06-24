package com.doctordark.hcf.eventgame;

import javax.annotation.Nullable;
import org.apache.commons.lang3.time.DurationFormatUtils;

import com.doctordark.util.cuboid.Cuboid;
import com.google.common.collect.Maps;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class CaptureZone implements ConfigurationSerializable
{
    private String name;
    private String prefix;
    private Cuboid cuboid;
    private Player cappingPlayer;
    private long defaultCaptureMillis;
    private String defaultCaptureWords;
    private long endMillis;
    
    public CaptureZone(final String name, final Cuboid cuboid, final long defaultCaptureMillis) {
        this(name, "", cuboid, defaultCaptureMillis);
    }
    
    public CaptureZone(final String name, final String prefix, final Cuboid cuboid, final long defaultCaptureMillis) {
        this.name = name;
        this.prefix = prefix;
        this.cuboid = cuboid;
        this.setDefaultCaptureMillis(defaultCaptureMillis);
    }
    
    public CaptureZone(final Map<String, Object> map) {
        this.name = (String) map.get("name");
        Object obj = map.get("prefix");
        if (obj instanceof String) {
            this.prefix = (String)obj;
        }
        obj = map.get("cuboid");
        if (obj instanceof Cuboid) {
            this.cuboid = (Cuboid)obj;
        }
        this.setDefaultCaptureMillis(Long.parseLong((String) map.get("captureMillis")));
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("name", this.name);
        if (this.prefix != null) {
            map.put("prefix", this.prefix);
        }
        if (this.cuboid != null) {
            map.put("cuboid", this.cuboid);
        }
        map.put("captureMillis", Long.toString(this.defaultCaptureMillis));
        return map;
    }
    
    public boolean isActive() {
        return this.getRemainingCaptureMillis() > 0L;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPrefix() {
        if (this.prefix == null) {
            this.prefix = "";
        }
        return this.prefix;
    }
    
    public String getDisplayName() {
        return this.getPrefix() + this.name;
    }
    
    public Cuboid getCuboid() {
        return this.cuboid;
    }
    
    public long getRemainingCaptureMillis() {
        if (this.endMillis == Long.MIN_VALUE) {
            return -1L;
        }
        if (this.cappingPlayer == null) {
            return this.defaultCaptureMillis;
        }
        return this.endMillis - System.currentTimeMillis();
    }
    
    public void setRemainingCaptureMillis(final long millis) {
        this.endMillis = System.currentTimeMillis() + millis;
    }
    
    public long getDefaultCaptureMillis() {
        return this.defaultCaptureMillis;
    }
    
    public void setDefaultCaptureMillis(final long millis) {
        if (this.defaultCaptureMillis != millis) {
            this.defaultCaptureMillis = millis;
            this.defaultCaptureWords = DurationFormatUtils.formatDurationWords(millis, true, true);
        }
    }
    
    public String getDefaultCaptureWords() {
        return this.defaultCaptureWords;
    }
    
    public Player getCappingPlayer() {
        return this.cappingPlayer;
    }
    
    public void setCappingPlayer(@Nullable final Player player) {
        this.cappingPlayer = player;
        if (player == null) {
            this.endMillis = this.defaultCaptureMillis;
        }
        else {
            this.endMillis = System.currentTimeMillis() + this.defaultCaptureMillis;
        }
    }
}
