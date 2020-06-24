package com.doctordark.hcf.timer.type;

import java.util.concurrent.TimeUnit;

import com.doctordark.hcf.timer.GlobalTimer;

import net.md_5.bungee.api.ChatColor;

public class KeyAllTimer extends GlobalTimer {

	public KeyAllTimer() {
		super("KeyAll", TimeUnit.HOURS.toSeconds(2L));

	}

	@Override
	public String getScoreboardPrefix() {

		return ChatColor.GREEN.toString() + ChatColor.BOLD;
	}

}
