package com.doctordark.util.player;

import org.bukkit.Bukkit;
import java.util.Iterator;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import org.bukkit.entity.Player;

public class PlayerList implements Iterable<Player>
{
    private final List<UUID> playerUniqueIds;
    private final List<Player> playerList;
    
    public PlayerList() {
        this.playerList = new ArrayList<Player>();
        this.playerUniqueIds = new ArrayList<UUID>();
    }
    
    public PlayerList(final Iterable<UUID> iterable) {
        this.playerList = new ArrayList<Player>();
        this.playerUniqueIds = (List<UUID>)Lists.newArrayList((Iterable)iterable);
    }
    
    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            private int index = 0;
            
            @Override
            public boolean hasNext() {
                return !PlayerList.this.playerUniqueIds.isEmpty() && this.index < PlayerList.this.playerUniqueIds.size();
            }
            
            @Override
            public Player next() {
                ++this.index;
                return PlayerList.this.getPlayers().get(this.index - 1);
            }
            
            @Override
            public void remove() {
            }
        };
    }
    
    public int size() {
        return this.playerUniqueIds.size();
    }
    
    public List<Player> getPlayers() {
        this.playerList.clear();
        for (final UUID uuid : this.playerUniqueIds) {
            this.playerList.add(Bukkit.getPlayer(uuid));
        }
        return this.playerList;
    }
    
    public boolean contains(final Player player) {
        return player != null && this.playerUniqueIds.contains(player.getUniqueId());
    }
    
    public boolean add(final Player player) {
        return !this.playerUniqueIds.contains(player.getUniqueId()) && this.playerUniqueIds.add(player.getUniqueId());
    }
    
    public boolean remove(final Player player) {
        return this.playerUniqueIds.remove(player.getUniqueId());
    }
    
    public void remove(final UUID playerUUID) {
        this.playerUniqueIds.remove(playerUUID);
    }
    
    public void clear() {
        this.playerUniqueIds.clear();
    }
}
