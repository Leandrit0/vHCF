package com.doctordark.hcf.faction.type;

import com.google.common.collect.Maps;

import org.bukkit.event.Event;
import org.bukkit.Bukkit;
import java.util.Collections;
import org.bukkit.Location;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.World;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.event.FactionClaimChangeEvent;
import com.doctordark.hcf.faction.event.FactionClaimChangedEvent;
import com.doctordark.hcf.faction.event.cause.ClaimChangeCause;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.GenericUtils;
import com.doctordark.util.cuboid.Cuboid;
import com.google.common.collect.ImmutableMap;

public class ClaimableFaction extends Faction
{
    protected static final ImmutableMap<World.Environment, String> ENVIRONMENT_MAPPINGS;
    protected final Set<Claim> claims;
    
    public ClaimableFaction(final String name) {
        super(name);
        this.claims = new HashSet<Claim>();
    }
    
    public ClaimableFaction(final Map<String, Object> map) {
        super(map);
        (this.claims = new HashSet<Claim>()).addAll(GenericUtils.createList(map.get("claims"), (Class)Claim.class));
    }
    
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = super.serialize();
        map.put("claims", new ArrayList(this.claims));
        return map;
    }
    
    @Override
    public void printDetails(final CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(' ' + this.getDisplayName(sender));
        for (final Claim claim : this.claims) {
            final Location location = claim.getCenter();
            sender.sendMessage(ChatColor.YELLOW + "  Location: " + ChatColor.GRAY.toString() + (String)ClaimableFaction.ENVIRONMENT_MAPPINGS.get((Object)location.getWorld().getEnvironment()) + ", " + location.getBlockX() + " | " + location.getBlockZ());
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public Set<Claim> getClaims() {
        return this.claims;
    }
    
    public boolean addClaim(final Claim claim, final CommandSender sender) {
        return this.addClaims(Collections.<Claim>singleton(claim), sender);
    }
    
    public boolean addClaims(final Collection<Claim> adding, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.CLAIM, adding, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.addAll(adding)) {
            return false;
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.CLAIM, adding));
        return true;
    }
    
    public boolean removeClaim(final Claim claim, final CommandSender sender) {
        return this.removeClaims(Collections.<Claim>singleton(claim), sender);
    }
    
    public boolean removeClaims(final Collection<Claim> removing, CommandSender sender) {
        if (sender == null) {
            sender = (CommandSender)Bukkit.getConsoleSender();
        }
        int previousClaims = this.claims.size();
        final FactionClaimChangeEvent event = new FactionClaimChangeEvent(sender, ClaimChangeCause.UNCLAIM, removing, this);
        Bukkit.getPluginManager().callEvent((Event)event);
        if (event.isCancelled() || !this.claims.removeAll(removing)) {
            return false;
        }
        if (this instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction)this;
            final Location home = playerFaction.getHome();
            final HCF plugin = HCF.getPlugin();
            int refund = 0;
            for (final Claim claim : removing) {
                refund += plugin.getClaimHandler().calculatePrice((Cuboid)claim, previousClaims, true);
                if (previousClaims > 0) {
                    --previousClaims;
                }
                if (home != null && claim.contains(home)) {
                    playerFaction.setHome(null);
                    playerFaction.broadcast(ChatColor.RED.toString() + ChatColor.BOLD + "Your factions' home was unset as its residing claim was removed.");
                    break;
                }
            }
            plugin.getEconomyManager().addBalance(playerFaction.getLeader().getUniqueID(), refund);
            playerFaction.broadcast(ChatColor.YELLOW + "Faction leader was refunded " + ChatColor.GREEN + '$' + refund + ChatColor.YELLOW + " due to a land unclaim.");
        }
        Bukkit.getPluginManager().callEvent((Event)new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, removing));
        return true;
    }
    
    static {
        ENVIRONMENT_MAPPINGS = Maps.immutableEnumMap((Map)ImmutableMap.of((Object)World.Environment.NETHER, (Object)"Nether", (Object)World.Environment.NORMAL, (Object)"Overworld", (Object)World.Environment.THE_END, (Object)"The End"));
    }
}
