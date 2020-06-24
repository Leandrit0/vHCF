package com.doctordark.hcf.scoreboard.nametag;

import java.util.List;

import org.bukkit.entity.Player;

public interface NametagAdapter {

    List<BufferedNametag> getPlate(Player player);
}
