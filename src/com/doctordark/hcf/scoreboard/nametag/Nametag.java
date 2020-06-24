package com.doctordark.hcf.scoreboard.nametag;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;


public class Nametag {

    private NametagAdapter adapter;
    private JavaPlugin plugin;
    private NametagThread thread;

    public Nametag(JavaPlugin plugin) {
        this.plugin = plugin;
        thread = new NametagThread(this, 10);
        thread.start();
    }

    public Nametag(JavaPlugin plugin, NametagAdapter adapter) {
        this(plugin);
        this.adapter = adapter;
    }

    @SuppressWarnings("deprecation")
	public void cleanup() {
        thread.stop();
    }

    @SuppressWarnings("deprecation")
	public void update(Player player) {
        if (adapter == null) {
            return;
        }
        List<BufferedNametag> nametags = adapter.getPlate(player);
        Scoreboard sb = player.getScoreboard();

        if (sb == Bukkit.getScoreboardManager().getMainScoreboard()) {
            return;
        }

        for (BufferedNametag bufferedNametag : nametags) {
            //Get Team
            Team team = sb.getTeam(bufferedNametag.getGroupName());

            if (team == null) {
                team = sb.registerNewTeam(bufferedNametag.getGroupName());
            }

            //Set Prefix
            if (bufferedNametag.getPrefix() != null) {
                team.setPrefix(bufferedNametag.getPrefix());
            } else {
                team.setPrefix(ChatColor.WHITE.toString());
            }
            //Set Suffix
            if (bufferedNametag.getSuffix() != null) {
                team.setSuffix(bufferedNametag.getSuffix());
            } else {
                team.setSuffix(ChatColor.WHITE.toString());
            }
            if (bufferedNametag.getPlayer() != null && bufferedNametag.getPlayer().isOnline()) {
                team.addEntry(bufferedNametag.getPlayer().getName());
            }
            
            //Friendly Invis
            team.setCanSeeFriendlyInvisibles(bufferedNametag.isFriendlyInvis());

            if (bufferedNametag.isShowHealth() && sb.getObjective(DisplaySlot.BELOW_NAME) == null) {
                Objective objective = sb.registerNewObjective("showhealth", "health");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objective.setDisplayName(ChatColor.RED + StringEscapeUtils.unescapeJava("\u2764"));
                double health = ((CraftPlayer) bufferedNametag.getPlayer()).getHealth();
                objective.getScore(bufferedNametag.getPlayer()).setScore((int) Math.floor(health));
            }

            if (!bufferedNametag.isShowHealth() && sb.getObjective(DisplaySlot.BELOW_NAME) != null) {
                Objective objective = sb.getObjective(DisplaySlot.BELOW_NAME);
                objective.unregister();
            }
        }
    }
    
    public NametagAdapter getAdapter() {
		return adapter;
	}
    
    public JavaPlugin getPlugin() {
		return plugin;
	}
    
    public NametagThread getThread() {
		return thread;
	}
}
