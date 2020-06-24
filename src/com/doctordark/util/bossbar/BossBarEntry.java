package com.doctordark.util.bossbar;

import org.bukkit.scheduler.BukkitTask;

public class BossBarEntry
{
    private final BossBar bossBar;
    private final BukkitTask cancelTask;
    
    public BossBarEntry(final BossBar bossBar, final BukkitTask cancelTask) {
        this.bossBar = bossBar;
        this.cancelTask = cancelTask;
    }
    
    public BossBar getBossBar() {
        return this.bossBar;
    }
    
    public BukkitTask getCancelTask() {
        return this.cancelTask;
    }
}
