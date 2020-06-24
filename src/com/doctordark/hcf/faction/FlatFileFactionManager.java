package com.doctordark.hcf.faction;

import java.util.Set;
import org.bukkit.ChatColor;

import java.util.HashSet;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.java.JavaPlugin;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.event.FactionClaimChangedEvent;
import com.doctordark.hcf.faction.event.FactionCreateEvent;
import com.doctordark.hcf.faction.event.FactionRelationRemoveEvent;
import com.doctordark.hcf.faction.event.FactionRemoveEvent;
import com.doctordark.hcf.faction.event.FactionRenameEvent;
import com.doctordark.hcf.faction.event.PlayerJoinedFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeftFactionEvent;
import com.doctordark.hcf.faction.event.cause.ClaimChangeCause;
import com.doctordark.hcf.faction.struct.ChatChannel;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.faction.type.ClaimableFaction;
import com.doctordark.hcf.faction.type.EndPortalFaction;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.faction.type.RoadFaction;
import com.doctordark.hcf.faction.type.SpawnFaction;
import com.doctordark.hcf.faction.type.WarzoneFaction;
import com.doctordark.hcf.faction.type.WildernessFaction;
import com.doctordark.util.Config;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.cuboid.CoordinatePair;
import com.google.common.base.Preconditions;

import java.util.Collection;

import org.bukkit.event.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.OfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

import org.bukkit.event.Listener;

public class FlatFileFactionManager implements Listener, FactionManager
{
    private final WarzoneFaction warzone;
    private final WildernessFaction wilderness;
    private final Map<CoordinatePair, Claim> claimPositionMap;
    private final Map<UUID, UUID> factionPlayerUuidMap;
    private final Map<UUID, Faction> factionUUIDMap;
    private final Map<String, UUID> factionNameMap;
    private final HCF plugin;
    private Config config;
    
    public FlatFileFactionManager(final HCF plugin) {
        this.claimPositionMap = new HashMap<CoordinatePair, Claim>();
        this.factionPlayerUuidMap = new ConcurrentHashMap<UUID, UUID>();
        this.factionUUIDMap = new HashMap<UUID, Faction>();
        this.factionNameMap = new CaseInsensitiveMap<String, UUID>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)plugin);
        this.warzone = new WarzoneFaction();
        this.wilderness = new WildernessFaction();
        this.reloadFactionData();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        this.factionPlayerUuidMap.put(event.getUniqueID(), event.getFaction().getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        this.factionPlayerUuidMap.remove(event.getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(final FactionRenameEvent event) {
        this.factionNameMap.remove(event.getOriginalName());
        this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(final FactionClaimChangedEvent event) {
        for (final Claim claim : event.getAffectedClaims()) {
            this.cacheClaim(claim, event.getCause());
        }
    }
    
    @Deprecated
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }
    
    public List<Faction> getFactions() {
        final List<Faction> asd = new ArrayList<Faction>();
        for (final Faction fac : this.factionUUIDMap.values()) {
            asd.add(fac);
        }
        return asd;
    }
    
    public Claim getClaimAt(final World world, final int x, final int z) {
        return this.claimPositionMap.get(new CoordinatePair(world, x, z));
    }
    
    public Claim getClaimAt(final Location location) {
        return this.getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }
    
    public Faction getFactionAt(final World world, final int x, final int z) {
        final World.Environment environment = world.getEnvironment();
        final Claim claim = this.getClaimAt(world, x, z);
        if (claim != null) {
            final Faction faction = claim.getFaction();
            if (faction != null) {
                return faction;
            }
        }
        if (environment == World.Environment.THE_END) {
            return this.warzone;
        }
        int warzoneRadius = 1000;
        if (environment == World.Environment.NETHER) {
            warzoneRadius /= 8;
        }
        return (Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius) ? this.wilderness : this.warzone;
    }
    
    public Faction getFactionAt(final Location location) {
        return this.getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }
    
    public Faction getFactionAt(final Block block) {
        return this.getFactionAt(block.getLocation());
    }
    
    public Faction getFaction(final String factionName) {
        final UUID uuid = this.factionNameMap.get(factionName);
        return (uuid == null) ? null : this.factionUUIDMap.get(uuid);
    }
    
    public Faction getFaction(final UUID factionUUID) {
        return this.factionUUIDMap.get(factionUUID);
    }
    
    public PlayerFaction getPlayerFaction(final UUID playerUUID) {
        final UUID uuid = this.factionPlayerUuidMap.get(playerUUID);
        final Faction faction = (uuid == null) ? null : this.factionUUIDMap.get(uuid);
        return (faction instanceof PlayerFaction) ? ((PlayerFaction)faction) : null;
    }
    
    public PlayerFaction getPlayerFaction(final Player player) {
        return this.getPlayerFaction(player.getUniqueId());
    }
    
    public PlayerFaction getContainingPlayerFaction(final String search) {
        final OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search)) : Bukkit.getOfflinePlayer(search);
        return (target.hasPlayedBefore() || target.isOnline()) ? this.getPlayerFaction(target.getUniqueId()) : null;
    }
    
    public Faction getContainingFaction(final String search) {
        final Faction faction = this.getFaction(search);
        if (faction != null) {
            return faction;
        }
        final UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId();
        if (playerUUID != null) {
            return this.getPlayerFaction(playerUUID);
        }
        return null;
    }
    
    public boolean containsFaction(final Faction faction) {
        return this.factionNameMap.containsKey(faction.getName());
    }
    
    public boolean createFaction(final Faction faction) {
        return this.createFaction(faction, (CommandSender)Bukkit.getConsoleSender());
    }
    
    public boolean createFaction(final Faction faction, final CommandSender sender) {
        if (this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
            return false;
        }
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        if (faction instanceof PlayerFaction && sender instanceof Player) {
            final Player player = (Player)sender;
            final PlayerFaction playerFaction = (PlayerFaction)faction;
            if (!playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
                return false;
            }
        }
        final FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
        Bukkit.getPluginManager().callEvent((Event)createEvent);
        return !createEvent.isCancelled();
    }
    
    public boolean removeFaction(final Faction faction, final CommandSender sender) {
        if (this.factionUUIDMap.remove(faction.getUniqueID()) == null) {
            return false;
        }
        this.factionNameMap.remove(faction.getName());
        final FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent((Event)removeEvent);
        if (removeEvent.isCancelled()) {
            return false;
        }
        if (faction instanceof ClaimableFaction) {
            Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, ((ClaimableFaction)faction).getClaims()));
        }
        if (faction instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction)faction;
            for (final PlayerFaction ally : playerFaction.getAlliedFactions()) {
                Bukkit.getPluginManager().callEvent((Event)new FactionRelationRemoveEvent(playerFaction, ally, Relation.ENEMY));
                ally.getRelations().remove(faction.getUniqueID());
            }
            for (final UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.setMember(uuid, null, true);
            }
        }
        return true;
    }
    
    private void cacheClaim(final Claim claim, final ClaimChangeCause cause) {
        Preconditions.checkNotNull((Object)claim, (Object)"Claim cannot be null");
        Preconditions.checkNotNull((Object)cause, (Object)"Cause cannot be null");
        Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE, (Object)"Cannot cache claims of resize type");
        final World world = claim.getWorld();
        if (world == null) {
            return;
        }
        final int minX = Math.min(claim.getX1(), claim.getX2());
        final int maxX = Math.max(claim.getX1(), claim.getX2());
        final int minZ = Math.min(claim.getZ1(), claim.getZ2());
        final int maxZ = Math.max(claim.getZ1(), claim.getZ2());
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                final CoordinatePair coordinatePair = new CoordinatePair(world, x, z);
                if (cause == ClaimChangeCause.CLAIM) {
                    this.claimPositionMap.put(coordinatePair, claim);
                }
                else if (cause == ClaimChangeCause.UNCLAIM) {
                    this.claimPositionMap.remove(coordinatePair);
                }
            }
        }
    }
    
    private void cacheFaction(final Faction faction) {
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        this.factionUUIDMap.put(faction.getUniqueID(), faction);
        if (faction instanceof ClaimableFaction) {
            final ClaimableFaction claimableFaction = (ClaimableFaction)faction;
            for (final Claim claim : claimableFaction.getClaims()) {
                this.cacheClaim(claim, ClaimChangeCause.CLAIM);
            }
        }
        if (faction instanceof PlayerFaction) {
            for (final FactionMember factionMember : ((PlayerFaction)faction).getMembers().values()) {
                this.factionPlayerUuidMap.put(factionMember.getUniqueID(), faction.getUniqueID());
            }
        }
    }
    
    public void reloadFactionData() {
        this.factionNameMap.clear();
        this.config = new Config((JavaPlugin)this.plugin, "factions");
        final Object object = this.config.get("factions");
        if (object instanceof MemorySection) {
            final MemorySection section = (MemorySection)object;
            for (final String factionName : section.getKeys(false)) {
                final Object next = this.config.get(section.getCurrentPath() + '.' + factionName);
                if (next instanceof Faction) {
                    this.cacheFaction((Faction)next);
                }
            }
        }
        else if (object instanceof List) {
            final List<?> list = (List<?>)object;
            for (final Object next2 : list) {
                if (next2 instanceof Faction) {
                    this.cacheFaction((Faction)next2);
                }
            }
        }
        final Set<Faction> adding = new HashSet<Faction>();
        if (!this.factionNameMap.containsKey("Warzone")) {
            adding.add(new WarzoneFaction());
        }
        if (!this.factionNameMap.containsKey("Spawn")) {
            adding.add(new SpawnFaction());
        }
        if (!this.factionNameMap.containsKey("NorthRoad")) {
            adding.add(new RoadFaction.NorthRoadFaction());
            adding.add(new RoadFaction.EastRoadFaction());
            adding.add(new RoadFaction.SouthRoadFaction());
            adding.add(new RoadFaction.WestRoadFaction());
        }
        if (!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction());
        }
        for (final Faction added : adding) {
            this.cacheFaction(added);
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + added.getName() + " not found, created.");
        }
    }
    
    public void saveFactionData() {
        this.config.set("factions", (Object)new ArrayList(this.factionUUIDMap.values()));
        this.config.save();
    }
}
