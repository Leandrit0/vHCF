package com.doctordark.hcf.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.doctordark.hcf.HCF;
import com.doctordark.util.chat.ClickAction;
import com.doctordark.util.chat.Text;

public class FreezeListener implements Listener {

	public static Set<UUID> frozen = new HashSet<UUID>();
	
	public void onLeave(PlayerQuitEvent e){
	Player p = e.getPlayer();
	if (frozen.contains(p.getUniqueId())){
		for (Player online : Bukkit.getOnlinePlayers()){
			if (online.hasPermission("hcf.mod")){
				for (String s : HCF.getPlugin().getConfig().getStringList("freeze_leave")){
					new Text(ChatColor.translateAlternateColorCodes('&', s.replace("%player%", p.getName()))).setClick(ClickAction.RUN_COMMAND, HCF.getPlugin().getConfig().getString("freeze.command").replace("%player%", p.getName())).send(online);;
				}
			}
		}
	 }
	}
	
}
