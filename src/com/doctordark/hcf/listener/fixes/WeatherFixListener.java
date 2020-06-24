package com.doctordark.hcf.listener.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.World;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.Listener;

public class WeatherFixListener implements Listener
{
    @EventHandler
    public void onWeatherChange(final WeatherChangeEvent e) {
        if (e.getWorld().getEnvironment() == World.Environment.NORMAL && e.getWorld().getWeatherDuration() > 0) {
            e.setCancelled(true);
            e.getWorld().setWeatherDuration(0);
        }
    }
}
