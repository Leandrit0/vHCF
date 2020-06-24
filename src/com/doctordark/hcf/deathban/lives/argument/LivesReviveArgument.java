package com.doctordark.hcf.deathban.lives.argument;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.deathban.Deathban;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.util.command.CommandArgument;

public class LivesReviveArgument extends CommandArgument
{
    private static final String REVIVE_BYPASS_PERMISSION = "hcf.revive.bypass";
    private static final String PROXY_CHANNEL_NAME = "BungeeCord";
    private final HCF plugin;
    
    public LivesReviveArgument(final HCF plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        this.permission = "hcf.command.lives.argument." + this.getName();
        plugin.getServer().getMessenger().registerOutgoingPluginChannel((Plugin)plugin, "BungeeCord");
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.GOLD + "Player '" + ChatColor.WHITE + args[1] + ChatColor.GOLD + "' not found.");
            return true;
        }
        final UUID targetUUID = target.getUniqueId();
        final FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
        final Deathban deathban = factionTarget.getDeathban();
        if (deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        Relation relation = Relation.ENEMY;
        if (sender instanceof Player) {
            if (!sender.hasPermission("hcf.revive.bypass") && this.plugin.getEotwHandler().isEndOfTheWorld()) {
                sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
                return true;
            }
            if (!sender.hasPermission("hcf.revive.bypass")) {
                final Player player = (Player)sender;
                final UUID playerUUID = player.getUniqueId();
                final int selfLives = this.plugin.getDeathbanManager().getLives(playerUUID);
                if (selfLives <= 0) {
                    sender.sendMessage(ChatColor.RED + "You do not have any lives.");
                    return true;
                }
                this.plugin.getDeathbanManager().setLives(playerUUID, selfLives - 1);
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
                relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
                sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
            else {
                sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }
        }
        else {
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
        }
        factionTarget.removeDeathban();
        return true;
    }
    
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length != 2) {
            return Collections.<String>emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
        for (final FactionUser factionUser : factionUsers) {
            final Deathban deathban = factionUser.getDeathban();
            if (deathban != null) {
                if (!deathban.isActive()) {
                    continue;
                }
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                final String offlineName = offlinePlayer.getName();
                if (offlineName == null) {
                    continue;
                }
                results.add(offlinePlayer.getName());
            }
        }
        return results;
    }
}
