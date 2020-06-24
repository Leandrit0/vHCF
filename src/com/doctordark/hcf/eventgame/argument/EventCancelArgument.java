package com.doctordark.hcf.eventgame.argument;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.EventTimer;
import com.doctordark.hcf.faction.type.Faction;
import com.doctordark.util.command.CommandArgument;

public class EventCancelArgument extends CommandArgument
{
    private final HCF plugin;
    
    public EventCancelArgument(final HCF plugin) {
        super("cancel", "Cancels a running event", new String[] { "stop", "end" });
        this.plugin = plugin;
        this.permission = "hcf.command.event.argument." + this.getName();
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName();
    }
    
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        final Faction eventFaction = eventTimer.getEventFaction();
        if (!eventTimer.clearCooldown()) {
            sender.sendMessage(ChatColor.RED + "There is not a running event.");
            return true;
        }
        Bukkit.broadcastMessage(sender.getName() + ChatColor.YELLOW + " has cancelled " + ((eventFaction == null) ? "the active event" : (eventFaction.getName() + ChatColor.YELLOW)) + ".");
        return true;
    }
}
