package com.doctordark.hcf.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import com.doctordark.hcf.deathban.Deathban;
import com.doctordark.util.GenericUtils;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

public class FactionUser implements ConfigurationSerializable
{
  @Getter @Setter   private  Set<UUID> factionChatSpying;
  @Getter @Setter  private  Set<String> shownScoreboardScores;
  @Getter @Setter  private  UUID userUUID;
  @Getter @Setter  private boolean capzoneEntryAlerts;
  @Getter @Setter  private boolean showClaimMap;
  @Getter @Setter  private boolean showLightning;
  @Getter @Setter  private Deathban deathban;
  @Getter @Setter   private long lastFactionLeaveMillis;
  @Getter @Setter   private int kills;
  @Getter @Setter   private int diamondsMined;
  @Getter @Setter   private int healthBrewed;
  @Getter @Setter  private int deaths;
    
    public FactionUser(final UUID userUUID) {
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        this.userUUID = userUUID;
    }
    
    public FactionUser(final Map<String, Object> map) {
        this.factionChatSpying = new HashSet<UUID>();
        this.shownScoreboardScores = new HashSet<String>();
        this.showLightning = true;
        this.shownScoreboardScores.addAll(GenericUtils.createList(map.get("shownScoreboardScores"), String.class));
        this.factionChatSpying.addAll(GenericUtils.createList(map.get("faction-chat-spying"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.capzoneEntryAlerts = (boolean) map.get("capzoneEntryAlerts");
        this.showLightning = (boolean) map.get("showLightning");
        this.deathban = (Deathban) map.get("deathban");
        this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
        this.diamondsMined = (int) map.get("diamonds");
        this.healthBrewed = (int) map.get("brewed");
        this.kills = (int) map.get("kills");
        this.deaths = (int) map.get("deaths");
    }
    
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("shownScoreboardScores", new ArrayList(this.shownScoreboardScores));
        map.put("faction-chat-spying", this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("userUUID", this.userUUID.toString());
        map.put("diamonds", this.diamondsMined);
        map.put("brewed", this.healthBrewed);
        map.put("capzoneEntryAlerts", this.capzoneEntryAlerts);
        map.put("showClaimMap", this.showClaimMap);
        map.put("showLightning", this.showLightning);
        map.put("deathban", this.deathban);
        map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        return map;
    }


    public void removeDeathban() {
        this.deathban = null;
    }
 

}
