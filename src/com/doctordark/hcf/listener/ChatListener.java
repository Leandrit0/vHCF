package com.doctordark.hcf.listener;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.command.ConsoleCommandSender;
import java.util.Iterator;
import java.util.Set;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import org.bukkit.event.Event;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.concurrent.TimeUnit;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.event.FactionChatEvent;
import com.doctordark.hcf.faction.struct.ChatChannel;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import java.util.UUID;
import com.google.common.collect.ImmutableSet;

import org.bukkit.event.Listener;

public class ChatListener implements Listener
{
    private static final String EOTW_CAPPER_PREFIX;
    private static final ImmutableSet<UUID> EOTW_CAPPERS;
    private static final String DOUBLE_POST_BYPASS_PERMISSION = "hcf.doublepost.bypass";
    private static final Pattern PATTERN;
    private final ConcurrentMap<Object, Object> messageHistory;
    private final HCF plugin;
    protected boolean mute = false;    
    public boolean isMute() {
		return mute;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}
    
  

	public static String getEotwCapperPrefix() {
		return EOTW_CAPPER_PREFIX;
	}

	public static ImmutableSet<UUID> getEotwCappers() {
		return EOTW_CAPPERS;
	}

	public static String getDoublePostBypassPermission() {
		return DOUBLE_POST_BYPASS_PERMISSION;
	}

	public static Pattern getPattern() {
		return PATTERN;
	}

	public ConcurrentMap<Object, Object> getMessageHistory() {
		return messageHistory;
	}

	public HCF getPlugin() {
		return plugin;
	}



	public ChatListener(final HCF plugin) {
        this.plugin = plugin;    
        this.messageHistory = (ConcurrentMap<Object, Object>)CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        final Player player = event.getPlayer();
        final String lastMessage = (String) this.messageHistory.get(player.getUniqueId());
        final String cleanedMessage = ChatListener.PATTERN.matcher(message).replaceAll("");
        if (lastMessage != null && (message.equals(lastMessage) || StringUtils.getLevenshteinDistance(cleanedMessage, lastMessage) <= 1) && !player.hasPermission("hcf.doublepost.bypass")) {
            player.sendMessage(ChatColor.RED + "Double posting is prohibited.");
            event.setCancelled(true);
            return;
        }
        if (this.isMute() == true && !player.hasPermission(plugin.getConfig().getString("other.chatbypass"))) {
        	player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.mute")));
        }  else {
        	return;
        }
        this.messageHistory.put(player.getUniqueId(), cleanedMessage);
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        final ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        final Set<Player> recipients = (Set<Player>)event.getRecipients();
        if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if (!this.isGlobalChannel(message)) {
                final Collection<Player> online = (Collection<Player>)playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (final PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent((Event)new FactionChatEvent(true, playerFaction, player, chatChannel, (Collection<? extends CommandSender>)recipients, event.getMessage()));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        final boolean usingRecipientVersion = false;
        event.setCancelled(true);
        Boolean isTag = true;
        if (player.hasPermission("faction.removetag")) {
            isTag = true;
        }
        final String rank = ChatColor.translateAlternateColorCodes('&', "&e" + PermissionsEx.getUser(player).getPrefix()).replace("_", " ");
        String displayName = player.getDisplayName();
        displayName = rank + displayName;
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (message.toLowerCase().contains("nigger") || message.toLowerCase().contains("steal plugins") || message.toLowerCase().contains("take plugins") || message.toLowerCase().contains("kill yourself") || message.toLowerCase().contains("shit staff")) {
            for (final Player on : Bukkit.getOnlinePlayers()) {
                if (on.hasPermission("hcf.mod")) {
                    on.sendMessage(player.getDisplayName() + ChatColor.LIGHT_PURPLE + " " + message);
                }
            }
            event.setCancelled(true);
            return;
        }
        String tag = (playerFaction == null) ? (ChatColor.RED + "*") : playerFaction.getDisplayName((CommandSender)console);
        console.sendMessage(ChatColor.GOLD + "[" + tag + ChatColor.GOLD + "] " + displayName + " " + ChatColor.GRAY + message);
        for (final Player recipient : event.getRecipients()) {
            tag = ((playerFaction == null) ? (ChatColor.RED + "*") : playerFaction.getDisplayName((CommandSender)recipient));
            if (isTag) {
            	recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("chat.withfac").replace("%faction%", tag).replace("%playername%", displayName).replace("%message%", message)));
               
            }
            else {
            	recipient.sendMessage(ChatColor.translateAlternateColorCodes('&', HCF.getPlugin().getConfig().getString("chat.withoutfac").replace("%playername%", displayName).replace("%message%", message)));
              
            }
        }
    }
    
    private boolean isGlobalChannel(final String input) {
        final int length = input.length();
        if (length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while (i < length) {
            final char character = input.charAt(i);
            if (character == ' ') {
                ++i;
            }
            else {
                if (character == '/') {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    static {
        EOTW_CAPPER_PREFIX = ChatColor.YELLOW + "\u2605 ";
        final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
        EOTW_CAPPERS = builder.build();
        PATTERN = Pattern.compile("\\W");
    }
}
