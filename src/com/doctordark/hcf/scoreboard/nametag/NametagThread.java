package com.doctordark.hcf.scoreboard.nametag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NametagThread extends Thread {

    private Nametag handler;
    private int interval;

    public NametagThread(Nametag handler, int interval) {
        setName("Nametag-Library");
        
        this.handler = handler;
        this.interval = interval;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void run() {
        while (true) {
            try {
            	
            	for(Player localPlayer : Bukkit.getOnlinePlayers()) {
            		if (localPlayer != null && localPlayer.isOnline()) {
                        handler.update(localPlayer);
                    }
            	}
            	
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
            try {
                //TODO make this configurable
                //TODO do a hook mode
                //TODO fix nullpointer
                sleep(50 * interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
