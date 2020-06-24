package com.doctordark.hcf.scoreboard.adapter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.scoreboard.nametag.BufferedNametag;
import com.doctordark.hcf.scoreboard.nametag.NametagAdapter;

public class TagAdapter implements NametagAdapter {

	@SuppressWarnings("unused")
	@Override
	public List<BufferedNametag> getPlate(Player player) {
		List<BufferedNametag> tag = new ArrayList<BufferedNametag>();
		for (Player players : Bukkit.getOnlinePlayers()) {
			BufferedNametag nametag = new BufferedNametag(players.getName(), ChatColor.RED.toString(), "", false, false, players);
			PlayerFaction team = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
			if (players == null) {
				return null;
			}
			if (player.equals(players)) {
				nametag = new BufferedNametag(players.getName(), ChatColor.GREEN.toString(), "", false, false, player);
			}
			if (team != null) {
				if (team.getMembers().keySet().contains(players.getUniqueId())) {
					nametag = new BufferedNametag(players.getName(), ChatColor.GREEN.toString(), "", false, false, players);
				}
				if (!team.getAlliedFactions().isEmpty()) {
					PlayerFaction targetTeam = HCF.getPlugin().getFactionManager().getPlayerFaction(players.getUniqueId());
						if (team.getAlliedFactions().contains(targetTeam)) {
							nametag = new BufferedNametag(players.getName(), ChatColor.YELLOW.toString(),"",false,false,players);
						}
					
					
				}
			}
			tag.add(nametag);
			
			
		}
		return tag;
	}



}
