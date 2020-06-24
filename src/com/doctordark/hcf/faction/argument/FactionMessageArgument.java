package com.doctordark.hcf.faction.argument;

import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.struct.ChatChannel;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.util.command.CommandArgument;

public class FactionMessageArgument extends CommandArgument
{
    private final HCF plugin;
    
    public FactionMessageArgument(final HCF plugin) {
        super("message", "Sends a message to your faction.");
        this.plugin = plugin;
        this.aliases = new String[] { "msg" };
    }
    
    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <message>";
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Only players can use faction chat.");
            return true;
        }
        if (args.length < 2)
        {
            sender.sendMessage(ChatColor.RED + "Usage: " + getUsage(label));
            return true;
        }
        Player player = (Player)sender;
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player);
        if (playerFaction == null)
        {
            sender.sendMessage(ChatColor.RED + "You are not in a faction.");
            return true;
        }
        String format = String.format(ChatChannel.FACTION.getRawFormat(player), new Object[] { "", StringUtils.join((Object[])args, ' ', 1, args.length) });
        Iterator<Player> iterator = playerFaction.getOnlinePlayers().iterator();
        while (iterator.hasNext())
        {
            Player target = (Player)iterator.next();
            target.sendMessage(format);
        }
        return true;
    }
}
