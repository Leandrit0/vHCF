package com.doctordark.hcf.deathban;

import org.bukkit.Location;

import com.doctordark.util.PersistableLocation;
import com.google.common.collect.Maps;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Deathban implements ConfigurationSerializable
{
    private final String reason;
    private final long creationMillis;
    private final long expiryMillis;
    private final PersistableLocation deathPoint;
    
    public Deathban(final String reason, final long duration, final PersistableLocation deathPoint) {
        this.reason = reason;
        final long millis = System.currentTimeMillis();
        this.creationMillis = millis;
        this.expiryMillis = millis + duration;
        this.deathPoint = deathPoint;
    }
    
    public Deathban(final Map map) {
        this.reason = (String) map.get("reason");
        this.creationMillis = Long.parseLong((String) map.get("creationMillis"));
        this.expiryMillis = Long.parseLong((String) map.get("expiryMillis"));
        final Object object = map.get("deathPoint");
        if (object != null) {
            this.deathPoint = (PersistableLocation)object;
        }
        else {
            this.deathPoint = null;
        }
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("reason", this.reason);
        map.put("creationMillis", Long.toString(this.creationMillis));
        map.put("expiryMillis", Long.toString(this.expiryMillis));
        if (this.deathPoint != null) {
            map.put("deathPoint", this.deathPoint);
        }
        return map;
    }
    
    public boolean isActive() {
        return this.getRemaining() > 0L;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public long getCreationMillis() {
        return this.creationMillis;
    }
    
    public long getExpiryMillis() {
        return this.expiryMillis;
    }
    
    public long getRemaining() {
        return this.expiryMillis - System.currentTimeMillis();
    }
    
    public Location getDeathPoint() {
        return (this.deathPoint == null) ? null : this.deathPoint.getLocation();
    }
}
