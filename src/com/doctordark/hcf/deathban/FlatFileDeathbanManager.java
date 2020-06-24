package com.doctordark.hcf.deathban;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.Config;
import com.doctordark.util.PersistableLocation;

import java.util.UUID;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public class FlatFileDeathbanManager implements DeathbanManager
{
    private static final int MAX_DEATHBAN_MULTIPLIER = 300;
    private final HCF plugin;
    private TObjectIntMap<UUID> livesMap;
    private Config livesConfig;
    
    public FlatFileDeathbanManager(final HCF plugin) {
        this.plugin = plugin;
        this.reloadDeathbanData();
    }
    
    @Override
    public TObjectIntMap<UUID> getLivesMap() {
        return this.livesMap;
    }
    
    @Override
    public int getLives(final UUID uuid) {
        return this.livesMap.get((Object)uuid);
    }
    
    @Override
    public int setLives(final UUID uuid, final int lives) {
        this.livesMap.put(uuid, lives);
        return lives;
    }
    
    @Override
    public int addLives(final UUID uuid, final int amount) {
        return this.livesMap.adjustOrPutValue(uuid, amount, amount);
    }
    
    @Override
    public int takeLives(final UUID uuid, final int amount) {
        return this.setLives(uuid, this.getLives(uuid) - amount);
    }
    
    @Override
    public double getDeathBanMultiplier(final Player player) {
        if (player.hasPermission("hcf.deathban.extra")) {
            for (int i = 5; i < 21600; --i) {
                if (player.hasPermission("hcf.deathban.seconds." + i)) {
                    return i / 1000;
                }
            }
        }
        return ConfigurationService.DEFAULT_DEATHBAN_DURATION;
    }
    
    @Override
    public Deathban applyDeathBan(final Player player, final String reason) {
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        long duration = ConfigurationService.DEFAULT_DEATHBAN_DURATION;
        if (!factionAt.isDeathban()) {
            duration /= 2L;
        }
        if (player.hasPermission("Basic")) {
            duration = 9000000L;
        }
        if (player.hasPermission("Ultra")) {
            duration = 7200000L;
        }
        if (player.hasPermission("Advanced")) {
            duration = 5400000L;
        }
        if (player.hasPermission("Elite")) {
            duration = 2700000L;
        }
        if (player.hasPermission("Exo")) {
            duration = 1800000L;
        }
        return this.applyDeathBan(player.getUniqueId(), new Deathban(reason, Math.min(FlatFileDeathbanManager.MAX_DEATHBAN_TIME, duration), new PersistableLocation(location)));
    }
    
    @Override
    public Deathban applyDeathBan(final UUID uuid, final Deathban deathban) {
        this.plugin.getUserManager().getUser(uuid).setDeathban(deathban);
        return deathban;
    }
    
    @Override
    public void reloadDeathbanData() {
        this.livesConfig = new Config((JavaPlugin)this.plugin, "lives");
        final Object object = this.livesConfig.get("lives");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Set<String> keys = (Set<String>)section.getKeys(false);
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(keys.size(), 0.5f, 0);
            for (final String id : keys) {
                this.livesMap.put(UUID.fromString(id), this.livesConfig.getInt(section.getCurrentPath() + "." + id));
            }
        }
        else {
            this.livesMap = (TObjectIntMap<UUID>)new TObjectIntHashMap(10, 0.5f, 0);
        }
    }
    
    @Override
    public void saveDeathbanData() {
        final Map<String, Integer> saveMap = new LinkedHashMap<String, Integer>(this.livesMap.size());
        this.livesMap.forEachEntry((uuid, i) -> {
            saveMap.put(uuid.toString(), i);
            return true;
        });
        this.livesConfig.set("lives", (Object)saveMap);
        this.livesConfig.save();
    }
}
