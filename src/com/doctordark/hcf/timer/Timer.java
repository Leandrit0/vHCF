package com.doctordark.hcf.timer;

import com.doctordark.util.Config;

import lombok.Getter;

public abstract class Timer
{
   @Getter protected final String name;
    protected final long defaultCooldown;
    
    public Timer(final String name, final long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }
    
    public abstract String getScoreboardPrefix();
    

    
    public final String getDisplayName() {
        return this.getScoreboardPrefix() + this.name;
    }
    
    public void load(final Config config) {
    }
    
    public void onDisable(final Config config) {
    }
}
