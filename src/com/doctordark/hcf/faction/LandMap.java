package com.doctordark.hcf.faction;

import java.util.Objects;
import java.util.List;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.claim.Claim;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.visualise.VisualBlockData;
import com.doctordark.hcf.visualise.VisualType;
import com.doctordark.util.BukkitUtils;

import org.bukkit.Location;
import org.bukkit.Material;
import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.ChatColor;

import java.util.LinkedHashSet;

import org.bukkit.entity.Player;

public class LandMap
{
    private static final int FACTION_MAP_RADIUS_BLOCKS = 22;
    
	public static boolean updateMap(final Player player, final HCF plugin, final VisualType visualType, final boolean inform) {
        final Location location = player.getLocation();
        final World world = player.getWorld();
        final int locationX = location.getBlockX();
        final int locationZ = location.getBlockZ();
        final int minimumX = locationX - LandMap.FACTION_MAP_RADIUS_BLOCKS;
        final int minimumZ = locationZ - LandMap.FACTION_MAP_RADIUS_BLOCKS;
        final int maximumX = locationX + LandMap.FACTION_MAP_RADIUS_BLOCKS;
        final int maximumZ = locationZ + LandMap.FACTION_MAP_RADIUS_BLOCKS;
        final Set<Claim> board = new LinkedHashSet<Claim>();
        if (visualType != VisualType.CLAIM_MAP) {
            player.sendMessage(ChatColor.RED + "Not supported: " + visualType.name().toLowerCase() + '.');
            return false;
        }
        for (int x = minimumX; x <= maximumX; ++x) {
            for (int z = minimumZ; z <= maximumZ; ++z) {
                final Claim claim = plugin.getFactionManager().getClaimAt(world, x, z);
                if (claim != null) {
                    board.add(claim);
                }
            }
        }
        if (board.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No claims are in a " + LandMap.FACTION_MAP_RADIUS_BLOCKS + " block radius.");
            return false;
        }
        for (final Claim claim2 : board) {
            final int maxHeight = Math.min(world.getMaxHeight(), 256);
            final Location[] corners = claim2.getCornerLocations();
            final List<Location> shown = new ArrayList<Location>(maxHeight * corners.length);
            Location[] array;
            for (int length = (array = corners).length, i = 0; i < length; ++i) {
                final Location corner = array[i];
                for (int y = 0; y < maxHeight; ++y) {
                    shown.add(world.getBlockAt(corner.getBlockX(), y, corner.getBlockZ()).getLocation());
                }
            }
            final Map<Location, VisualBlockData> dataMap = plugin.getVisualiseHandler().generate(player, shown, visualType, true);
            if (dataMap.isEmpty()) {
                continue;
            }
            String materialName = ChatColor.RED + "Illegal Exception please try again.";
            for (final VisualBlockData visualBlockData : dataMap.values()) {
                if (visualBlockData.getItemType() == Material.STAINED_GLASS) {
                    continue;
                }
                materialName = HCF.getPlugin().getItemDb().getName(new ItemStack(visualBlockData.getItemType()));
                break;
            }
            if (!inform) {
                continue;
            }
            player.sendMessage(String.valueOf(claim2.getFaction().getDisplayName((CommandSender)player)) + ChatColor.YELLOW + " owns land " + ChatColor.WHITE + ChatColor.GRAY + " (displayed with " + materialName + ")" + ChatColor.YELLOW + ".");
        }
        return true;
    }
    
    public static Location getNearestSafePosition(final Player player, final Location origin, final int searchRadius) {
        final FactionManager factionManager = HCF.getPlugin().getFactionManager();
        final Faction playerFaction = factionManager.getPlayerFaction(player.getUniqueId());
        final int minX = origin.getBlockX() - searchRadius;
        final int maxX = origin.getBlockX() + searchRadius;
        final int minZ = origin.getBlockZ() - searchRadius;
        final int maxZ = origin.getBlockZ() + searchRadius;
        for (int x = minX; x < maxX; ++x) {
            for (int z = minZ; z < maxZ; ++z) {
                final Location atPos = origin.clone().add((double)x, 0.0, (double)z);
                final Faction factionAtPos = factionManager.getFactionAt(atPos);
                if (Objects.equals(factionAtPos, playerFaction) || !(factionAtPos instanceof PlayerFaction)) {
                    return BukkitUtils.getHighestLocation(atPos, atPos);
                }
                final Location atNeg = origin.clone().add((double)x, 0.0, (double)z);
                final Faction factionAtNeg = factionManager.getFactionAt(atNeg);
                if (Objects.equals(factionAtNeg, playerFaction) || !(factionAtNeg instanceof PlayerFaction)) {
                    return BukkitUtils.getHighestLocation(atNeg, atNeg);
                }
            }
        }
        return null;
    }
}
