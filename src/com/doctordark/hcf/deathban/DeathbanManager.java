package com.doctordark.hcf.deathban;

import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;
import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public interface DeathbanManager
{
    public static final long MAX_DEATHBAN_TIME = TimeUnit.HOURS.toMillis(8L);
    
    TObjectIntMap<UUID> getLivesMap();
    
    int getLives(final UUID p0);
    
    int setLives(final UUID p0, final int p1);
    
    int addLives(final UUID p0, final int p1);
    
    int takeLives(final UUID p0, final int p1);
    
    double getDeathBanMultiplier(final Player p0);
    
    Deathban applyDeathBan(final Player p0, final String p1);
    
    Deathban applyDeathBan(final UUID p0, final Deathban p1);
    
    void reloadDeathbanData();
    
    void saveDeathbanData();
}
