package com.doctordark.hcf.economy;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.configuration.MemorySection;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.util.Config;

public class FlatFileEconomyManager implements EconomyManager
{
    private final JavaPlugin plugin;
    private TObjectIntMap<UUID> balanceMap;
    private Config balanceConfig;
    
    public FlatFileEconomyManager(final JavaPlugin plugin) {
        this.balanceMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(10, 0.5f, 0);
        this.plugin = plugin;
        this.reloadEconomyData();
    }
    
    @Override
    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }
    
    @Override
    public int getBalance(final UUID uuid) {
        return this.balanceMap.get((Object)uuid);
    }
    
    @Override
    public int setBalance(final UUID uuid, final int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }
    
    @Override
    public int addBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }
    
    @Override
    public int subtractBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }
    
    @Override
    public void reloadEconomyData() {
        this.balanceConfig = new Config(this.plugin, "balances");
        final Object object = this.balanceConfig.get("balances");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            for (final String id : keys) {
                this.balanceMap.put(UUID.fromString(id), this.balanceConfig.getInt("balances." + id));
            }
        }
    }
    
    @Override
    public void saveEconomyData() {
        final Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.balanceMap.size());
        this.balanceMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.balanceConfig.set("balances", (Object)saveMap);
        this.balanceConfig.save();
    }
}
