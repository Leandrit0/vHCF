package com.doctordark.hcf.economy;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public interface EconomyManager
{
    public static final char ECONOMY_SYMBOL = '$';
    
    TObjectIntMap<UUID> getBalanceMap();
    
    int getBalance(final UUID p0);
    
    int setBalance(final UUID p0, final int p1);
    
    int addBalance(final UUID p0, final int p1);
    
    int subtractBalance(final UUID p0, final int p1);
    
    void reloadEconomyData();
    
    void saveEconomyData();
}
