package com.doctordark.hcf.user;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.MemorySection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.hcf.HCF;
import com.doctordark.util.Config;
import com.google.common.base.MoreObjects;

import lombok.Getter;
import lombok.Setter;

public class UserManager implements Listener
{
    private final HCF plugin;
  @Getter @Setter private  Map<UUID, FactionUser> users;
    private Config userConfig;
    
    public UserManager(final HCF plugin) {
        this.users = new HashMap<UUID, FactionUser>();
        this.plugin = plugin;
        this.reloadUserData();
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.users.putIfAbsent(uuid, new FactionUser(uuid));
    }
    
 
    
    public FactionUser getUserAsync(final UUID uuid) {
        synchronized (this.users) {
            final FactionUser revert;
            final FactionUser user = this.users.putIfAbsent(uuid, revert = new FactionUser(uuid));
            return (FactionUser)MoreObjects.firstNonNull((Object)user, (Object)revert);
        }
    }
    
    public FactionUser getUser(final UUID uuid) {
        final FactionUser revert;
        final FactionUser user = this.users.putIfAbsent(uuid, revert = new FactionUser(uuid));
        return (FactionUser)MoreObjects.firstNonNull((Object)user, (Object)revert);
    }
    
    public void reloadUserData() {
        this.userConfig = new Config((JavaPlugin)this.plugin, "faction-users");
        final Object object = this.userConfig.get("users");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            final Collection<String> keys = (Collection<String>)section.getKeys(false);
            for (final String id : keys) {
                this.users.put(UUID.fromString(id), (FactionUser)this.userConfig.get(section.getCurrentPath() + '.' + id));
            }
        }
    }
    
    public void saveUserData() {
        final Set<Map.Entry<UUID, FactionUser>> entrySet = this.users.entrySet();
        final Map<String, FactionUser> saveMap = new LinkedHashMap<String, FactionUser>(entrySet.size());
        for (final Map.Entry<UUID, FactionUser> entry : entrySet) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.userConfig.set("users", (Object)saveMap);
        this.userConfig.save();
    }
}
