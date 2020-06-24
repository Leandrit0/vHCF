package com.doctordark.hcf.listener.fixes;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
import org.bukkit.GameMode;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;

public class BlockJumpGlitchFixListener implements Listener
{
    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            final Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE || player.getAllowFlight()) {
                return;
            }
            final Block block = event.getBlockPlaced();
            if (block.getType().isSolid() && !(block.getState() instanceof Sign)) {
                final int playerY = player.getLocation().getBlockY();
                final int blockY = block.getLocation().getBlockY();
                if (playerY > blockY) {
                    final Vector vector = player.getVelocity();
                    vector.setX(-0.1);
                    vector.setZ(-0.1);
                    player.setVelocity(vector.setY(vector.getY() - 0.41999998688697815));
                }
            }
        }
    }
}
