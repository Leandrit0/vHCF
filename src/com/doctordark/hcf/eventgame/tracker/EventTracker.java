package com.doctordark.hcf.eventgame.tracker;

import org.bukkit.entity.Player;

import com.doctordark.hcf.eventgame.CaptureZone;
import com.doctordark.hcf.eventgame.EventTimer;
import com.doctordark.hcf.eventgame.EventType;
import com.doctordark.hcf.eventgame.faction.EventFaction;

@Deprecated
public interface EventTracker
{
    EventType getEventType();
    
    void tick(final EventTimer p0, final EventFaction p1);
    
    void onContest(final EventFaction p0, final EventTimer p1);
    
    boolean onControlTake(final Player p0, final CaptureZone p1);
    
    boolean onControlLoss(final Player p0, final CaptureZone p1, final EventFaction p2);
    
    void stopTiming();
}
