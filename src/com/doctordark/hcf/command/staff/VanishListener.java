package com.doctordark.hcf.command.staff;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.doctordark.hcf.HCF;

import net.md_5.bungee.api.ChatColor;

public class VanishListener
  implements Listener {
  private final HCF plugin;
  public static List<UUID> vanishList;
  
  public VanishListener(HCF plugin) {
    this.plugin = plugin;
    vanishList = new ArrayList();
  }
  public void setVanished(Player p) {
	  vanishList.add(p.getUniqueId());
	  for (Player p1 : Bukkit.getOnlinePlayers()) {
		  Bukkit.getPlayer(p.getName()).hidePlayer(p);
		  p1.hidePlayer(p);
	  }
  }
  public void setVisible(Player p) {
	  if (vanishList.contains(p.getUniqueId())){
		  vanishList.remove(p.getUniqueId());
		  for (Player p1 : Bukkit.getOnlinePlayers()) {
			  p1.hidePlayer(p);
			  Bukkit.getPlayer(p.getName()).showPlayer(p);
		  }
	
	  }
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    if (vanishList.contains(e.getPlayer().getUniqueId()) && e.getPlayer().hasPermission("hcf.command.vanish"))
      e.getPlayer().sendMessage(ChatColor.GOLD + "You are no longer in vanish."); 
  }
}
