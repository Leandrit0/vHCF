package com.doctordark.hcf.faction.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitTask;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.deathban.Deathban;
import com.doctordark.hcf.faction.FactionMember;
import com.doctordark.hcf.faction.event.FactionDtrChangeEvent;
import com.doctordark.hcf.faction.event.PlayerJoinedFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeaveFactionEvent;
import com.doctordark.hcf.faction.event.PlayerLeftFactionEvent;
import com.doctordark.hcf.faction.event.cause.FactionLeaveCause;
import com.doctordark.hcf.faction.struct.Raidable;
import com.doctordark.hcf.faction.struct.RegenStatus;
import com.doctordark.hcf.faction.struct.Relation;
import com.doctordark.hcf.faction.struct.Role;
import com.doctordark.hcf.timer.type.TeleportTimer;
import com.doctordark.hcf.user.FactionUser;
import com.doctordark.util.BukkitUtils;
import com.doctordark.util.GenericUtils;
import com.doctordark.util.JavaUtils;
import com.doctordark.util.PersistableLocation;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;

public class PlayerFaction extends ClaimableFaction implements Raidable
{
    private static final UUID[] EMPTY_UUID_ARRAY;
    protected final Map requestedRelations;
    protected final Map relations;
    protected final Map members;
    protected final Set<String> invitedPlayerNames;
    protected PersistableLocation home;
    protected String announcement;
    protected boolean open;
    protected int balance;
    protected boolean friendlyfire = false; 
    private BukkitTask rallytask;
    
    public BukkitTask getRallytask() {
		return rallytask;
	}

	public void setRallytask(BukkitTask rallytask) {
		this.rallytask = rallytask;
	}

	private Location rally;
    /**
	 * @return the rally
	 */
	public Location getRally() {
		return rally;
	}

	/**
	 * @param rally the rally to set
	 */
	public void setRally(Location rally) {
		this.rally = rally;
	}

	public boolean isFriendlyfire() {
		return friendlyfire;
	}

	public void setFriendlyfire(boolean friendlyfire) {
		this.friendlyfire = friendlyfire;
	}

	protected int points = 0;
    public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public void addPoints(int amount){
		amount += this.points;
	}
	public void removePoints(int remove){
		remove -= this.points;
	}
	public long getLastDtrUpdateTimestamp() {
		return lastDtrUpdateTimestamp;
	}

	public void setLastDtrUpdateTimestamp(long lastDtrUpdateTimestamp) {
		this.lastDtrUpdateTimestamp = lastDtrUpdateTimestamp;
	}

	public static UUID[] getEmptyUuidArray() {
		return EMPTY_UUID_ARRAY;
	}

	

	public void setRegenCooldownTimestamp(long regenCooldownTimestamp) {
		this.regenCooldownTimestamp = regenCooldownTimestamp;
	}

	protected double deathsUntilRaidable;
    protected long regenCooldownTimestamp;
    private long lastDtrUpdateTimestamp;
    
    public PlayerFaction(final String name) {
        super(name);
        this.requestedRelations = new HashMap();
        this.relations = new HashMap();
        this.members = new HashMap();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
    }
    
    public PlayerFaction(final Map map) {
        super(map);
        this.requestedRelations = new HashMap();
        this.relations = new HashMap();
        this.members = new HashMap();
        this.invitedPlayerNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        this.deathsUntilRaidable = 1.0;
        for (final Map.Entry entry : GenericUtils.castMap(map.get("members"), String.class, FactionMember.class).entrySet()) {
            this.members.put(UUID.fromString((String) entry.getKey()), entry.getValue());
        }
        this.invitedPlayerNames.addAll(GenericUtils.createList((Object)map.get("invitedPlayerNames"), (Class)String.class));
        Object object2 = map.get("home");
        if (object2 != null) {
            this.home = (PersistableLocation)object2;
        }
        object2 = map.get("announcement");
        if (object2 != null) {
            this.announcement = (String)object2;
        }
        for (final Map.Entry entry3 : GenericUtils.castMap(map.get("relations"), String.class, String.class).entrySet()) {
            this.relations.put(UUID.fromString((String) entry3.getKey()), Relation.valueOf((String) entry3.getValue()));
        }
        for (final Map.Entry entry3 : GenericUtils.castMap(map.get("requestedRelations"), String.class, String.class).entrySet()) {
            this.requestedRelations.put(UUID.fromString((String) entry3.getKey()), Relation.valueOf((String) entry3.getValue()));
        }
        this.open = (boolean)map.get("open");
        this.balance = (int)map.get("balance");
        this.points = (int)map.get("points");
        this.deathsUntilRaidable = (double)map.get("deathsUntilRaidable");
        this.regenCooldownTimestamp = Long.parseLong((String) map.get("regenCooldownTimestamp"));
        this.lastDtrUpdateTimestamp = Long.parseLong((String) map.get("lastDtrUpdateTimestamp"));
    }
    

    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public Map serialize()
    {
        Map map = super.serialize();
        HashMap relationSaveMap = new HashMap(this.relations.size());
        Iterator requestedRelationsSaveMap = this.relations.entrySet().iterator();
        while (requestedRelationsSaveMap.hasNext())
        {
            Map.Entry entrySet = (Map.Entry)requestedRelationsSaveMap.next();
            relationSaveMap.put(((UUID)entrySet.getKey()).toString(), ((Relation)entrySet.getValue()).name());
        }
        map.put("relations", relationSaveMap);
        HashMap requestedRelationsSaveMap1 = new HashMap(this.requestedRelations.size());
        Iterator entrySet1 = this.requestedRelations.entrySet().iterator();
        while (entrySet1.hasNext())
        {
            Map.Entry saveMap = (Map.Entry)entrySet1.next();
            requestedRelationsSaveMap1.put(saveMap.getKey().toString(), ((Relation)saveMap.getValue()).name());
        }
        map.put("requestedRelations", requestedRelationsSaveMap1);
        Set entrySet2 = this.members.entrySet();
        LinkedHashMap saveMap1 = new LinkedHashMap(this.members.size());
        Iterator var6 = entrySet2.iterator();
        while (var6.hasNext())
        {
            Map.Entry entry = (Map.Entry)var6.next();
            saveMap1.put(entry.getKey().toString(), entry.getValue());
        }
        map.put("members", saveMap1);
        map.put("invitedPlayerNames", new ArrayList(this.invitedPlayerNames));
        if (this.home != null) {
            map.put("home", this.home);
        }
        if (this.announcement != null) {
            map.put("announcement", this.announcement);
        }
        map.put("open", Boolean.valueOf(this.open));
        map.put("balance", Integer.valueOf(this.balance));
        map.put("deathsUntilRaidable", Double.valueOf(this.deathsUntilRaidable));
        map.put("regenCooldownTimestamp", Long.toString(this.regenCooldownTimestamp));
        map.put("lastDtrUpdateTimestamp", Long.toString(this.lastDtrUpdateTimestamp));
        return map;
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember) {
        return this.setMember(null, playerUUID, factionMember, false);
    }
    
    public boolean setMember(final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        return this.setMember(null, playerUUID, factionMember, force);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember) {
        return this.setMember(player, player.getUniqueId(), factionMember, false);
    }
    
    public boolean setMember(final Player player, final FactionMember factionMember, final boolean force) {
        return this.setMember(player, player.getUniqueId(), factionMember, force);
    }
    
    private boolean setMember(final Player player, final UUID playerUUID, final FactionMember factionMember, final boolean force) {
        if (factionMember == null) {
            if (!force) {
                final PlayerLeaveFactionEvent event = (player == null) ? new PlayerLeaveFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeaveFactionEvent(player, this, FactionLeaveCause.LEAVE);
                Bukkit.getPluginManager().callEvent((Event)event);
                if (event.isCancelled()) {
                    return false;
                }
            }
            this.members.remove(playerUUID);
            this.setDeathsUntilRaidable(Math.min(this.deathsUntilRaidable, this.getMaximumDeathsUntilRaidable()));
            final PlayerLeftFactionEvent event2 = (player == null) ? new PlayerLeftFactionEvent(playerUUID, this, FactionLeaveCause.LEAVE) : new PlayerLeftFactionEvent(player, this, FactionLeaveCause.LEAVE);
            Bukkit.getPluginManager().callEvent((Event)event2);
            return true;
        }
        final PlayerJoinedFactionEvent eventPre = (player == null) ? new PlayerJoinedFactionEvent(playerUUID, this) : new PlayerJoinedFactionEvent(player, this);
        Bukkit.getPluginManager().callEvent((Event)eventPre);
        this.lastDtrUpdateTimestamp = System.currentTimeMillis();
        this.invitedPlayerNames.remove(factionMember.getName());
        this.members.put(playerUUID, factionMember);
        return true;
    }
    
    public Collection<UUID> getAllied() {
        return (Collection<UUID>)Maps.filterValues(this.relations, (Predicate)new Predicate<Relation>() {
            public boolean apply(final Relation relation) {
                return relation == Relation.ALLY;
            }
        }).keySet();
    }
    
    public List<PlayerFaction> getAlliedFactions() {
        final Collection<UUID> allied = this.getAllied();
        final Iterator<UUID> iterator = allied.iterator();
        final List<PlayerFaction> results = new ArrayList<PlayerFaction>(allied.size());
        while (iterator.hasNext()) {
            final Faction faction = HCF.getPlugin().getFactionManager().getFaction(iterator.next());
            if (faction instanceof PlayerFaction) {
                results.add((PlayerFaction)faction);
            }
            else {
                iterator.remove();
            }
        }
        return results;
    }
    
    public Map<UUID, Relation> getRequestedRelations() {
        return (Map<UUID, Relation>)this.requestedRelations;
    }
    
    public Map<UUID, Relation> getRelations() {
        return (Map<UUID, Relation>)this.relations;
    }
    
    public Map<UUID, FactionMember> getMembers() {
        return (Map<UUID, FactionMember>)ImmutableMap.copyOf(this.members);
    }
    
    public Set getOnlinePlayers() {
        return this.getOnlinePlayers(null);
    }
    
    public Set getOnlinePlayers(CommandSender sender)
    {
        Set entrySet = getOnlineMembers(sender).entrySet();
        HashSet results = new HashSet(entrySet.size());
        Iterator var4 = entrySet.iterator();
        while (var4.hasNext())
        {
            Map.Entry entry = (Map.Entry)var4.next();
            results.add(Bukkit.getPlayer((UUID)entry.getKey()));
        }
        return results;
    }
    
    public Map getOnlineMembers() {
        return this.getOnlineMembers(null);
    }
    
    public Map<UUID, FactionMember> getOnlineMembers(CommandSender sender)
    {
        Player senderPlayer = (sender instanceof Player) ? (Player)sender : null;
        HashMap<UUID, FactionMember> results = new HashMap();
        Iterator<Map.Entry<UUID, FactionMember>> iterator = this.members.entrySet().iterator();
        while (iterator.hasNext())
        {
            Map.Entry<UUID, FactionMember> entry = (Map.Entry)iterator.next();
            Player target = Bukkit.getPlayer((UUID)entry.getKey());
            if ((target != null) && (
                    (senderPlayer == null) || (senderPlayer.canSee(target)))) {
                results.put(entry.getKey(), entry.getValue());
            }
        }
        return results;
    }
    
    public FactionMember getLeader() {
        final Map<UUID, FactionMember> members = (Map<UUID, FactionMember>)this.members;
        final Iterator<Map.Entry<UUID, FactionMember>> iterator = members.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<UUID, FactionMember> entry;
            if ((entry = iterator.next()).getValue().getRole() == Role.LEADER) {
                return entry.getValue();
            }
        }
        return null;
    }
    
    @Deprecated
    public FactionMember getMember(final String memberName) {
        final UUID uuid = Bukkit.getOfflinePlayer(memberName).getUniqueId();
        if (uuid == null) {
            return null;
        }
        final FactionMember factionMember = (FactionMember) this.members.get(uuid);
        return factionMember;
    }
    
    public FactionMember getMember(final Player player) {
        return this.getMember(player.getUniqueId());
    }
    
    public FactionMember getMember(final UUID memberUUID) {
        return (FactionMember) this.members.get(memberUUID);
    }
    
    public Set<String> getInvitedPlayerNames() {
        return this.invitedPlayerNames;
    }
    
    public Location getHome() {
        return (this.home == null) ? null : this.home.getLocation();
    }
    
    public void setHome(Location home)
    {
        if ((home == null) && (this.home != null))
        {
            TeleportTimer timer = HCF.getPlugin().getTimerManager().teleportTimer;
            Iterator var3 = getOnlinePlayers().iterator();
            while (var3.hasNext())
            {
                Player player = (Player)var3.next();
                Location destination = (Location)timer.getDestination(player);
                if (Objects.equal(destination, this.home.getLocation()))
                {
                    timer.clearCooldown(player);
                    player.sendMessage(ChatColor.RED + "Your home was unset, so your " + timer.getDisplayName() + ChatColor.RED + " timer has been cancelled");
                }
            }
        }
        this.home = (home == null ? null : new PersistableLocation(home));
    }
    
    public String getAnnouncement() {
        return this.announcement;
    }
    
    public void setAnnouncement(@Nullable final String announcement) {
        this.announcement = announcement;
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
    }
    
    public int getBalance() {
        return this.balance;
    }
    
    public void setBalance(final int balance) {
        this.balance = balance;
    }
    
    @Override
    public boolean isRaidable() {
        return this.deathsUntilRaidable <= 0.0;
    }
    
    @Override
    public double getDeathsUntilRaidable() {
        return this.getDeathsUntilRaidable(true);
    }
    
    @Override
    public double getMaximumDeathsUntilRaidable() {
        if (this.members.size() == 1) {
            return 1.1;
        }
        return Math.min(5.0, this.members.size() * 0.9);
    }
    
    public double getDeathsUntilRaidable(final boolean updateLastCheck) {
        if (updateLastCheck) {
            this.updateDeathsUntilRaidable();
        }
        return this.deathsUntilRaidable;
    }
    
    public ChatColor getDtrColour() {
        this.updateDeathsUntilRaidable();
        if (this.deathsUntilRaidable < 0.0) {
            return ChatColor.RED;
        }
        if (this.deathsUntilRaidable < 1.0) {
            return ChatColor.YELLOW;
        }
        return ChatColor.GREEN;
    }
    
    private void updateDeathsUntilRaidable() {
        if (this.getRegenStatus() == RegenStatus.REGENERATING) {
            final long now = System.currentTimeMillis();
            final long millisPassed = now - this.lastDtrUpdateTimestamp;
            if (millisPassed >= ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES) {
                final long remainder = millisPassed % ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES;
                final int multiplier = (int)((millisPassed + remainder) / ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES);
                final double increase = multiplier * 0.1;
                this.lastDtrUpdateTimestamp = now - remainder;
                this.setDeathsUntilRaidable(this.deathsUntilRaidable + increase);
            }
        }
    }
    
    @Override
    public double setDeathsUntilRaidable(final double deathsUntilRaidable) {
        return this.setDeathsUntilRaidable(deathsUntilRaidable, true);
    }
    
    private double setDeathsUntilRaidable(double deathsUntilRaidable, final boolean limit) {
        deathsUntilRaidable = deathsUntilRaidable * 100.0 / 100.0;
        if (limit) {
            deathsUntilRaidable = Math.min(deathsUntilRaidable, this.getMaximumDeathsUntilRaidable());
        }
        if (deathsUntilRaidable - this.deathsUntilRaidable != 0.0) {
            final FactionDtrChangeEvent event = new FactionDtrChangeEvent(FactionDtrChangeEvent.DtrUpdateCause.REGENERATION, this, this.deathsUntilRaidable, deathsUntilRaidable);
            Bukkit.getPluginManager().callEvent((Event)event);
            if (!event.isCancelled()) {
                deathsUntilRaidable = event.getNewDtr();
                if (deathsUntilRaidable > 0.0 && deathsUntilRaidable <= 0.0) {
                    HCF.getPlugin().getLogger().info("Faction " + this.getName() + " is now raidable.");
                }
                this.lastDtrUpdateTimestamp = System.currentTimeMillis();
                return this.deathsUntilRaidable = deathsUntilRaidable;
            }
        }
        return this.deathsUntilRaidable;
    }
    
    protected long getRegenCooldownTimestamp() {
        return this.regenCooldownTimestamp;
    }
    
    @Override
    public long getRemainingRegenerationTime() {
        return (this.regenCooldownTimestamp == 0L) ? 0L : (this.regenCooldownTimestamp - System.currentTimeMillis());
    }
    
    @Override
    public void setRemainingRegenerationTime(final long millis) {
        final long systemMillis = System.currentTimeMillis();
        this.regenCooldownTimestamp = systemMillis + millis;
        this.lastDtrUpdateTimestamp = systemMillis + ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES * 2L;
    }
    
    @Override
    public RegenStatus getRegenStatus() {
        if (this.getRemainingRegenerationTime() > 0L) {
            return RegenStatus.PAUSED;
        }
        if (this.getMaximumDeathsUntilRaidable() > this.deathsUntilRaidable) {
            return RegenStatus.REGENERATING;
        }
        return RegenStatus.FULL;
    }
    
    public void printStats(final CommandSender sender) {
        Integer combinedKills = 0;
        Integer combinedDiamonds = 0;
        Long combinedPlaytime = null;
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        while (this.members.entrySet().iterator().hasNext()) {
            final Map.Entry entry = (Map.Entry)this.members.entrySet().iterator().next();
            final FactionUser user = HCF.getPlugin().getUserManager().getUser((UUID) entry.getKey());
            final int kills = user.getKills();
            combinedKills += kills;
            final int diamonds = user.getDiamondsMined();
            combinedDiamonds += diamonds;
          
        }
        sender.sendMessage(ChatColor.YELLOW + "  Kills: " + ChatColor.GRAY + combinedKills);
        sender.sendMessage(ChatColor.YELLOW + "  Diamonds: " + ChatColor.GRAY + combinedDiamonds);
        sender.sendMessage(ChatColor.YELLOW + "  PlayTime: " + ChatColor.GRAY + combinedPlaytime);
    }
    
    @Override
    public void printDetails(CommandSender sender)
    {
        String leaderName = null;
        HashSet allyNames = new HashSet(1);
        Iterator combinedKills = this.relations.entrySet().iterator();
        while (combinedKills.hasNext())
        {
            Map.Entry memberNames = (Map.Entry)combinedKills.next();
            Faction captainNames = HCF.getPlugin().getFactionManager().getFaction((UUID)memberNames.getKey());
            if ((captainNames instanceof PlayerFaction))
            {
                PlayerFaction playerFaction = (PlayerFaction)captainNames;
                allyNames.add(playerFaction.getDisplayName(sender) + ChatColor.GRAY + '[' + ChatColor.GRAY + playerFaction.getOnlinePlayers(sender).size() + ChatColor.GRAY + '/' + ChatColor.GRAY + playerFaction.members.size() + ChatColor.GRAY + ']');
            }
        }
        int combinedKills2 = 0;
        HashSet memberNames1 = new HashSet();
        HashSet<String> captainNames1 = new HashSet();
        Iterator playerFaction1 = this.members.entrySet().iterator();
        while (playerFaction1.hasNext())
        {
            Map.Entry entry = (Map.Entry)playerFaction1.next();
            FactionMember factionMember = (FactionMember)entry.getValue();
            Player target = factionMember.toOnlinePlayer();
            FactionUser user = HCF.getPlugin().getUserManager().getUser((UUID)entry.getKey());
            int kills = user.getKills();
            combinedKills2 += kills;
            Deathban deathban = user.getDeathban();
            ChatColor colour = (target == null) || (((sender instanceof Player)) && (!((Player)sender).canSee(target))) ? ChatColor.GRAY : (deathban != null) && (deathban.isActive()) ? ChatColor.RED : ChatColor.GREEN;
            if ((deathban != null) && (deathban.isActive())) {
                colour = ChatColor.RED;
            } else if ((target != null) && ((!(sender instanceof Player)) || (((Player)sender).canSee(target)))) {
                colour = ChatColor.GREEN;
            } else {
                colour = ChatColor.GRAY;
            }
            String memberName = colour + factionMember.getName() + ChatColor.GRAY + '[' + ChatColor.GRAY + kills + ChatColor.GRAY + ']';
            memberNames1.add(memberName);
            if (factionMember.getRole() == Role.CAPTAIN) {
                captainNames1.add(memberName);
            }
            for (String members : captainNames1) {
                memberNames1.remove(members);
            }
            factionMember.getRole();
            if (factionMember.getRole() == Role.LEADER) {
                leaderName = memberName;
            }
            if (memberNames1.contains(leaderName)) {
                memberNames1.remove(leaderName);
            }
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
        sender.sendMessage(this.getDisplayName(sender) + ChatColor.GRAY + "[" + this.getOnlineMembers().size() + "/" + this.getMembers().size() + "] " + ChatColor.YELLOW + "Home: " + ChatColor.WHITE + ((this.home == null) ? "None" : (ChatColor.RED.toString() + '(' + this.home.getLocation().getBlockX() + " | " + this.home.getLocation().getBlockZ() + ')')));
        if (!allyNames.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Allies: " + StringUtils.join((Iterable)allyNames, ChatColor.GRAY + ", "));
        }
        if (leaderName != null) {
            sender.sendMessage(ChatColor.YELLOW + "  Leader: " + ChatColor.RED + leaderName);
        }
        if (!captainNames1.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Captains: " + ChatColor.RED + StringUtils.join(captainNames1, ChatColor.GRAY + ", "));
        }
        if (!memberNames1.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "  Members: " + ChatColor.RED + StringUtils.join(memberNames1, ChatColor.GRAY + ", "));
        }
        sender.sendMessage(ChatColor.YELLOW + "  Points: " + ChatColor.RED + getPoints());
        sender.sendMessage(ChatColor.YELLOW + "  Balance: " + ChatColor.RED + '$' + this.balance);
        sender.sendMessage(ChatColor.YELLOW + "  Total Kills: " + ChatColor.RED + combinedKills2);
        sender.sendMessage(ChatColor.YELLOW + "  Deaths Until Raidable: " + ChatColor.YELLOW + " [" + this.getRegenStatus().getSymbol() + this.getDtrColour() + JavaUtils.format((Number)this.getDeathsUntilRaidable(false)) + ChatColor.YELLOW + '/' + JavaUtils.format((Number)this.getMaximumDeathsUntilRaidable()) + ']');
        final long dtrRegenRemaining = this.getRemainingRegenerationTime();
        if (dtrRegenRemaining > 0L) {
            sender.sendMessage(ChatColor.YELLOW + "  Time Until Regen: " + ChatColor.GRAY + DurationFormatUtils.formatDurationWords(dtrRegenRemaining, true, true));
        }
        sender.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
    
    public String replace(Player p, String input) {
    	if (input.contains("{DTR}")) {
    		input = input.replace("{DTR}", JavaUtils.format((Number)this.getDeathsUntilRaidable(false)));
    	}
    	if (input.contains("{LEADER}")) {
    		input = input.replace("{LEADER}", String.valueOf(getLeader()));   		
    	}
    	if (input.contains("{BALANCE}")) {
    		input = input.replace("{BALANCE}", String.valueOf(this.balance));
    	}
    	if (input.contains("{POINTS}")) {
    		input = input.replace("{POINTS}", String.valueOf(getPoints()));
    	}
    	if (input.contains("{NAME}")) {
    		input = input.replace("{NAME}", this.getDisplayName(p));
    	}
    	if (input.contains("{ANNOUNCEMENT}")) {
    		if (this.announcement != null) {
    		input = input.replace("{ANNOUNCEMENT}", this.announcement);
    		 } else if (this.announcement == null) {
    			 return null;
    		 }
    	}
    	return input;
    }
    public void broadcast(final String message) {
        this.broadcast(message, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String[] messages) {
        this.broadcast(messages, PlayerFaction.EMPTY_UUID_ARRAY);
    }
    
    public void broadcast(final String message, @Nullable final UUID... ignore) {
        this.broadcast(new String[] { message }, ignore);
    }
    
    public void broadcast(final String[] messages, final UUID... ignore) {
        Preconditions.checkNotNull((Object)messages, (Object)"Messages cannot be null");
        Preconditions.checkArgument(messages.length > 0, (Object)"Message array cannot be empty");
        final Collection<Player> players = (Collection<Player>)this.getOnlinePlayers();
        final Collection<UUID> ignores = ((ignore.length == 0) ? Collections.emptySet() : Sets.newHashSet(ignore));
        for (final Player player : players) {
            if (!ignores.contains(player.getUniqueId())) {
                player.sendMessage(messages);
            }
        }
    }
    
    static {
        EMPTY_UUID_ARRAY = new UUID[0];
    }
}
