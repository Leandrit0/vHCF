package com.doctordark.hcf;

import org.apache.commons.lang3.time.DurationFormatUtils;
import java.util.concurrent.TimeUnit;
import java.util.EnumMap;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.potion.PotionType;
import org.bukkit.enchantments.Enchantment;
import java.util.Map;
import java.util.List;
import java.util.TimeZone;

@Deprecated
public final class ConfigurationService
{
    public static final TimeZone SERVER_TIME_ZONE;
    public static final int WARZONE_RADIUS = 1000;
    public static final String NAME = "Exodon";
    public static final String TEAMSPEAK_URL = HCF.getPlugin().getConfig().getString("teamspeak_url");
    public static final String DONATE_URL = HCF.getPlugin().getConfig().getString("donate_url");
    public static final String SUBREDDIT_URL = HCF.getPlugin().getConfig().getString("reddit_url");
    public static final int SPAWN_BUFFER = 150;
    public static final double MAP_NUMBER = 2.0;
    public static String KIT_MAP_NAME;
    public static final boolean KIT_MAP = false;
    public static final List<String> DISALLOWED_FACTION_NAMES;
    public static final Map<Enchantment, Integer> ENCHANTMENT_LIMITS;
    public static final Map<PotionType, Integer> POTION_LIMITS;
    public static final Map<World.Environment, Integer> BORDER_SIZES;
    public static final Map<World.Environment, Double> SPAWN_RADIUS_MAP;
    public static final int FACTION_PLAYER_LIMIT = 30;
    public static final ChatColor TEAMMATE_COLOUR;
    public static final ChatColor ALLY_COLOUR;
    public static final ChatColor ENEMY_COLOUR;
    public static final ChatColor SAFEZONE_COLOUR;
    public static final ChatColor ROAD_COLOUR;
    public static final ChatColor WARZONE_COLOUR;
    public static final ChatColor WILDERNESS_COLOUR;
    public static final String SCOREBOARD_TITLE;
    public static final int MAX_ALLIES_PER_FACTION = 0;
    public static final long DTR_MILLIS_BETWEEN_UPDATES;
    public static final String DTR_WORDS_BETWEEN_UPDATES;
    public static final int CONQUEST_REQUIRED_WIN_POINTS = 300;
    public static long DEFAULT_DEATHBAN_DURATION;
    public static boolean CRATE_BROADCASTS;
    
    static {
        ConfigurationService.KIT_MAP_NAME = null;
        SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
        DISALLOWED_FACTION_NAMES = ImmutableList.of("kohieotw", "kohisotw", "hcteams", "hcteamseotw", "hcteamssotw", "exploitesquad", "staff", "mod", "owner", "dev", "admin", "ipvp", new String[] { "para" });
        ENCHANTMENT_LIMITS = new HashMap<Enchantment, Integer>();
        POTION_LIMITS = new EnumMap<PotionType, Integer>(PotionType.class);
        BORDER_SIZES = new EnumMap<World.Environment, Integer>(World.Environment.class);
        SPAWN_RADIUS_MAP = new EnumMap<World.Environment, Double>(World.Environment.class);
        ConfigurationService.POTION_LIMITS.put(PotionType.INSTANT_DAMAGE, 0);
        ConfigurationService.POTION_LIMITS.put(PotionType.REGEN, 0);
        ConfigurationService.POTION_LIMITS.put(PotionType.STRENGTH, 0);
        ConfigurationService.POTION_LIMITS.put(PotionType.WEAKNESS, 0);
        ConfigurationService.POTION_LIMITS.put(PotionType.SLOWNESS, 1);
        ConfigurationService.POTION_LIMITS.put(PotionType.INVISIBILITY, 0);
        ConfigurationService.POTION_LIMITS.put(PotionType.POISON, 1);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.DAMAGE_ALL, 2);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_KNOCKBACK, 0);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.KNOCKBACK, 0);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.FIRE_ASPECT, 0);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.THORNS, 0);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_FIRE, 1);
        ConfigurationService.ENCHANTMENT_LIMITS.put(Enchantment.ARROW_DAMAGE, 3);
        ConfigurationService.BORDER_SIZES.put(World.Environment.NORMAL, 3000);
        ConfigurationService.BORDER_SIZES.put(World.Environment.NETHER, 1000);
        ConfigurationService.BORDER_SIZES.put(World.Environment.THE_END, 1500);
        ConfigurationService.SPAWN_RADIUS_MAP.put(World.Environment.NORMAL, 52.5);
        ConfigurationService.SPAWN_RADIUS_MAP.put(World.Environment.NETHER, 24.5);
        ConfigurationService.SPAWN_RADIUS_MAP.put(World.Environment.THE_END, 16.5);
        ConfigurationService.DEFAULT_DEATHBAN_DURATION = TimeUnit.HOURS.toMillis(3L);
        TEAMMATE_COLOUR = ChatColor.GREEN;
        ALLY_COLOUR = ChatColor.AQUA;
        ENEMY_COLOUR = ChatColor.RED;
        SAFEZONE_COLOUR = ChatColor.AQUA;
        ROAD_COLOUR = ChatColor.GOLD;
        WARZONE_COLOUR = ChatColor.DARK_RED;
        WILDERNESS_COLOUR = ChatColor.DARK_GREEN;
        SCOREBOARD_TITLE = String.valueOf(ChatColor.LIGHT_PURPLE.toString()) + ChatColor.BOLD + "   VeltPvP " + ChatColor.RED + "[Mini HCF]     ";
        DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);
        DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils.formatDurationWords(ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES, true, true);
        ConfigurationService.CRATE_BROADCASTS = false;
    }
}
