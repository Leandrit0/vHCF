package com.doctordark.hcf.combatlog;

import org.bukkit.scheduler.BukkitTask;

public class CombatLogEntry
{
    public final LoggerEntity loggerEntity;
    public final BukkitTask task;
    
    public CombatLogEntry(final LoggerEntity loggerEntity, final BukkitTask task) {
        this.loggerEntity = loggerEntity;
        this.task = task;
    }
}
