package com.doctordark.hcf.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.Sound;

import net.minecraft.util.com.google.common.primitives.Ints;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.World;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.WarzoneFaction;
import com.doctordark.util.BukkitUtils;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.CommandExecutor;

public class CannonCommand implements CommandExecutor, TabCompleter
{
    private static final Material SPAWN_CANNON_BLOCK;
    private static final ImmutableList<String> COMPLETIONS;
    private final HCF plugin;
    
    public CannonCommand(final HCF plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players may use the spawn cannon.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <launch|aim [x z])>");
            return true;
        }
        final Player player = (Player)sender;
        final World world = player.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use the spawn cannon in the overworld.");
            return true;
        }
        final Location location = player.getLocation();
        if (location.getBlock().getRelative(BlockFace.DOWN).getType() != CannonCommand.SPAWN_CANNON_BLOCK) {
            sender.sendMessage(ChatColor.RED + "You are not on a spawn cannon (" + ChatColor.AQUA + CannonCommand.SPAWN_CANNON_BLOCK.name() + ChatColor.RED + ").");
            return true;
        }
        if (!this.plugin.getFactionManager().getFactionAt(location).isSafezone()) {
            sender.sendMessage(ChatColor.RED + "You can only use the spawn cannon in safe-zones.");
            return true;
        }
        if (args[0].equalsIgnoreCase("aim")) {
            if (!sender.hasPermission(command.getPermission() + ".aim")) {
                sender.sendMessage(ChatColor.WHITE + "Sub-Command not found.");
                return true;
            }
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + ' ' + args[0].toLowerCase() + " <x> <z>");
                return true;
            }
            final Integer x = Ints.tryParse(args[1]);
            final Integer z;
            if (x == null || (z = Ints.tryParse(args[2])) == null) {
                sender.sendMessage(ChatColor.RED + "Your x or z co-ordinate was invalid.");
                return true;
            }
            this.launchPlayer(player, new Location(world, (double)x, 0.0, (double)z));
            return true;
        }
        else {
            if (!args[0].equalsIgnoreCase("launch")) {
                sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <launch|aim [x z])>");
                return true;
            }
            if (!sender.hasPermission(command.getPermission() + ".launch")) {
                sender.sendMessage(ChatColor.RED + "You do not have access to launch the cannon.");
                return true;
            }
            final double min = ConfigurationService.SPAWN_RADIUS_MAP.get(world.getEnvironment());
            final int max = 1000;
            final int maxCannonDistance = this.getMaxCannonDistance(sender);
            final Random random = this.plugin.getRandom();
            double x2 = Math.max(random.nextInt(Math.min(1000, maxCannonDistance)), min);
            if (random.nextBoolean()) {
                x2 = -x2;
            }
            double z2 = Math.max(random.nextInt(Math.min(1000, maxCannonDistance)), min);
            if (random.nextBoolean()) {
                z2 = -z2;
            }
            this.launchPlayer(player, new Location(world, x2, 0.0, z2));
            return true;
        }
    }
    
    public void launchPlayer(final Player player, Location location) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        if (!(factionAt instanceof WarzoneFaction)) {
            player.sendMessage(ChatColor.RED + "You can only cannon to areas in the Warzone.");
            return;
        }
        final int x = location.getBlockX();
        final int z = location.getBlockZ();
        final int maxDistance = this.getMaxCannonDistance((CommandSender)player);
        if (Math.abs(x) > maxDistance || Math.abs(z) > maxDistance) {
            player.sendMessage(ChatColor.RED + "You cannot launch that far from the spawn cannon. Your limit is " + maxDistance + '.');
            return;
        }
        location = BukkitUtils.getHighestLocation(location).add(0.0, 3.0, 0.0);
        player.sendMessage(ChatColor.YELLOW + "Launched To " + ChatColor.BLUE + x + ChatColor.YELLOW + ", " + z + ChatColor.YELLOW + '.');
        player.playSound(location, Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.teleport(location, PlayerTeleportEvent.TeleportCause.COMMAND);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 1));
    }
    
    public int getMaxCannonDistance(final CommandSender sender) {
        int radius;
        for (int decrement = 50, i = radius = (850 + decrement - 1) / decrement * decrement; i > 0; i -= decrement) {
            if (sender.hasPermission("hcf.spawncannon." + i)) {
                return i;
            }
        }
        return 100;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 1) ? BukkitUtils.getCompletions(args, (List)CannonCommand.COMPLETIONS) : Collections.<String>emptyList();
    }
    
    static {
        SPAWN_CANNON_BLOCK = Material.BEACON;
        COMPLETIONS = ImmutableList.of("aim", "launch");
    }
}
