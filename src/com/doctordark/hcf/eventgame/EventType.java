package com.doctordark.hcf.eventgame;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.tracker.ConquestTracker;
import com.doctordark.hcf.eventgame.tracker.EventTracker;
import com.doctordark.hcf.eventgame.tracker.KothTracker;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

public enum EventType
{
    CONQUEST("Conquest", (EventTracker)new ConquestTracker(HCF.getPlugin())), 
    KOTH("KOTH", (EventTracker)new KothTracker(HCF.getPlugin()));
    
    private static final ImmutableMap<String, EventType> byDisplayName;
    private final EventTracker eventTracker;
    private final String displayName;
    
    private EventType(final String displayName, final EventTracker eventTracker) {
        this.displayName = displayName;
        this.eventTracker = eventTracker;
    }
    
    @Deprecated
    public static EventType getByDisplayName(final String name) {
        return (EventType)EventType.byDisplayName.get((Object)name.toLowerCase());
    }
    
    public EventTracker getEventTracker() {
        return this.eventTracker;
    }
    
    public String getDisplayName() {
        return this.displayName;
    }
    
    static {
        final ImmutableMap.Builder<String, EventType> builder = (ImmutableMap.Builder<String, EventType>)new ImmutableBiMap.Builder();
        for (final EventType eventType : values()) {
            builder.put(eventType.displayName.toLowerCase(), eventType);
        }
        byDisplayName = builder.build();
    }
}
