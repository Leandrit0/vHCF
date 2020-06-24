package com.doctordark.hcf.scoreboard.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.doctordark.hcf.DateTimeFormats;
import com.doctordark.hcf.DurationFormatter;
import com.doctordark.hcf.HCF;
import com.doctordark.hcf.eventgame.EventTimer;
import com.doctordark.hcf.eventgame.eotw.EotwHandler;
import com.doctordark.hcf.eventgame.faction.ConquestFaction;
import com.doctordark.hcf.eventgame.faction.EventFaction;
import com.doctordark.hcf.eventgame.faction.KothFaction;
import com.doctordark.hcf.eventgame.tracker.ConquestTracker;
import com.doctordark.hcf.faction.type.PlayerFaction;
import com.doctordark.hcf.pvpclass.PvpClass;
import com.doctordark.hcf.pvpclass.archer.ArcherClass;
import com.doctordark.hcf.pvpclass.bard.BardClass;
import com.doctordark.hcf.pvpclass.type.AssassinClass;
import com.doctordark.hcf.pvpclass.type.MinerClass;
import com.doctordark.hcf.pvpclass.type.RogueClass;
import com.doctordark.hcf.scoreboard.AssembleAdapter;
import com.doctordark.hcf.timer.GlobalTimer;
import com.doctordark.hcf.timer.PlayerTimer;
import com.doctordark.hcf.timer.Timer;
import com.doctordark.hcf.timer.type.ArcherTimer;
import com.doctordark.hcf.timer.type.EnderPearlTimer;
import com.doctordark.hcf.timer.type.KeyAllTimer;
import com.doctordark.hcf.timer.type.LogoutTimer;
import com.doctordark.hcf.timer.type.NotchAppleTimer;
import com.doctordark.hcf.timer.type.PvpClassWarmupTimer;
import com.doctordark.hcf.timer.type.PvpProtectionTimer;
import com.doctordark.hcf.timer.type.SOTWTimer;
import com.doctordark.hcf.timer.type.SpawnTagTimer;
import com.doctordark.hcf.timer.type.StuckTimer;
import com.doctordark.hcf.timer.type.TeleportTimer;


public class Adapter implements AssembleAdapter{
    public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;
	public String color(String input) { return ChatColor.translateAlternateColorCodes('&', input); }
	 private static String handleBardFormat(long millis, boolean trailingZero) { return ((trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get()).format(millis * 0.001D); }
     static {
    	 CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
             @Override
             protected DecimalFormat initialValue() {
                 return new DecimalFormat("00.0");
             }
         };
     }
	@Override
	public String getTitle(Player player) {
 
		return HCF.getPlugin().getConfig().getString("scoreboard.title");
	}
	public String replaceAll(Player player, String input) {	
		PvpClass pvpClass = HCF.getPlugin().getPvpClassManager().getEquippedClass(player);
        final EotwHandler.EotwRunnable eotwRunnable = HCF.getPlugin().getEotwHandler().getRunnable();
		final BardClass bardClass = (BardClass)pvpClass;
        final EventTimer eventTimer = HCF.getPlugin().getTimerManager().eventTimer;
        final EventFaction eventFaction = eventTimer.getEventFaction();
	      if (input.contains("%onlineplayers%")){
			input = input.replace("%onlineplayers%", String.valueOf(Bukkit.getOnlinePlayers().length));
		}
	      if (input.contains("%kothname%")) {
	    	  KothFaction koth = (KothFaction) eventFaction;
	    	  if (eventFaction instanceof KothFaction) {
	    		  input = input.replace("%kothname%", eventFaction.getScoreboardName());
	    	  } else if (!(eventFaction instanceof KothFaction)){
	    		  return null;
	    	  }
	      }
	      
	      if (input.contains("%kothtime%")) {	    	 
	    	  if (eventFaction instanceof KothFaction) {
	    		  input = input.replace("%kothtime%", String.valueOf(DurationFormatter.getRemaining(eventTimer.getRemaining(), true)));
	    	  } else if (!(eventFaction instanceof KothFaction)){
	    		  return null;
	    	  }
	      }
		if (input.contains("%eotwremaining%")) {
			 if (eotwRunnable != null) {
		            long remaining4 = eotwRunnable.getTimeUntilStarting();
		            if (remaining4 > 0L) {
		                input = input.replace("%eotwremaining%", HCF.getRemaining(remaining4, true));
		            }
		            else if ((remaining4 = eotwRunnable.getTimeUntilCappable()) > 0L) {
                        input = input.replace("%eotwremaining%", HCF.getRemaining(remaining4, true));
		            }
			 } else if (eotwRunnable == null) {
				 return null;
			 }
		}
	
		if (input.contains("%faction%")) {
			PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
		    if (playerFaction != null) {
		    	input = input.replace("%faction%", playerFaction.getName());
		    } else {
		    	return null;
		    }
		}
		if (input.contains("%factiondtr%")){
			PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player);
			if (playerFaction != null) {
				input = input.replace("%factiondtr%", String.valueOf(playerFaction.getDeathsUntilRaidable()));
			} else if (playerFaction == null) {
				return null;
			}
		}
		if (input.contains("%bardeffect%")) {
			if (pvpClass instanceof BardClass) {
			final long remaining2 = bardClass.getRemainingBuffDelay(player);
			if (remaining2 > 0L) {
				input = input.replace("%bardeffect%", HCF.getRemaining(remaining2, false));
			} else if (remaining2 <= 0L) {
				return null;
			}
		  } else {
			  return null;
		  }
		}
		if (input.contains("%energy%")) {
			if (pvpClass instanceof BardClass) {
                
				input = input.replace("%energy%", handleBardFormat(bardClass.getEnergyMillis(player), true));
			} else {
				return null;
			}
		}
		
			if (input.contains("%class%")){
				if (pvpClass != null){
					if (pvpClass instanceof BardClass || pvpClass instanceof AssassinClass || (pvpClass instanceof ArcherClass && ArcherClass.tagged.containsValue(player.getUniqueId()))) {
					  input = input.replace("%class%", pvpClass.getName());
					}
				} else if (pvpClass == null){
					return null;
				}
		 
			
		}
		final Collection<Timer> timers = HCF.getPlugin().getTimerManager().getTimers();
        for (final Timer timer : timers) {
        	if (timer instanceof GlobalTimer){
        		 GlobalTimer playerTimer2 = (GlobalTimer)timer;
        	     long remaining3 = playerTimer2.getRemaining();
        	     if (input.contains("%sotwremaining%")){
        	    	 if (timer instanceof SOTWTimer){
        	    	 if (remaining3 > 0L) {
        	    		 input = input.replace("%sotwremaining%", DurationFormatter.getRemaining(remaining3, true));
        	    	 } else if (remaining3 == 0L) {
        	    		 return null;
        	    	 }
        	    	}
        	     }
        	     if (input.contains("%keyallremaining%")) {
        	    	 if (timer instanceof KeyAllTimer) {
        	    		 if (remaining3 > 0L) {
        	    		 input = input.replace("%keyallremaining%", DurationFormatter.getRemaining(remaining3, true));
        	    		 } else if (remaining3 == 0L) {
        	    			 return null;
        	    		 }
        	    	}
        	     }
        	}
            if (timer instanceof PlayerTimer) {
                final PlayerTimer playerTimer = (PlayerTimer)timer;
                final long remaining3 = playerTimer.getRemaining(player);
            	  if (input.contains("%enderpearl%")){
            		  if (timer instanceof EnderPearlTimer){
              		  if (remaining3 > 0L){
              		  input = input.replace("%enderpearl%", DurationFormatter.getRemaining(remaining3, true));
              		  } else if (remaining3 <= 0L) {
              			  return null;
              		  }
            		 }
                }
            	  if (input.contains("%warmuptimer%")){
            		  if (timer instanceof PvpClassWarmupTimer){
              		  if (remaining3 > 0L){
              		  input = input.replace("%warmuptimer%", DurationFormatter.getRemaining(remaining3, true));
              		  } else if (remaining3 <= 0L) {
              			  return null;
              		  }
            		 }
                }
            	  if (input.contains("%spawntag%")){
            		  if (timer instanceof SpawnTagTimer){
            		  if (remaining3 > 0L){
            		  if (HCF.getPlugin().getConfig().getBoolean("spawntag_decimals") == true){
            			input = input.replace("%spawntag%", DurationFormatter.getRemaining(remaining3, true));  
            		  } else {
            			 
            			  long seconds = TimeUnit.MILLISECONDS.toSeconds(remaining3);
            			  input = input.replace("%spawntag%", String.valueOf(seconds));  
            		  }
            		  } else if (remaining3 == 0L){
            			  return null;
            		  }
            		 }
            	  }
                   if (input.contains("%fstuck%")){
            		  if (timer instanceof StuckTimer){
              		  if (remaining3 > 0L){
              		  input = input.replace("%fstuck%", DurationFormatter.getRemaining(remaining3, true));
              		  } else if (remaining3 <= 0L) {
              			  return null;
              		  }
            		 }
                }
			    if (input.contains("%home%")){
            		  if (timer instanceof TeleportTimer){
              		  if (remaining3 > 0L){
              		  input = input.replace("%home%", DurationFormatter.getRemaining(remaining3, true));
              		  } else if (remaining3 <= 0L) {
              			  return null;
              		  }
            		 }
                }
          	  if (input.contains("%pvptimer%")){
        		  if (timer instanceof PvpProtectionTimer){
          		  if (remaining3 > 0L){
          		  input = input.replace("%pvptimer%", DurationFormatter.getRemaining(remaining3, true));
          		  } else if (remaining3 <= 0L) {
          			  return null;
          		  }
        		 }
            }
          	  if (input.contains("%archermark%")) {
          		  if (timer instanceof ArcherTimer) {
          			  if (remaining3 > 0L) {
          				  input = input.replace("%archermark%", DurationFormatter.getRemaining(remaining3, true));
          			 } else if (remaining3 <= 0L) {
             			  return null;
             		  }
          		  }
          	  }
        	  if (input.contains("%notchapple%")){
        		  if (timer instanceof NotchAppleTimer){
          		  if (remaining3 > 0L){
          		  input = input.replace("%notchapple%", DurationFormatter.getRemaining(remaining3, true));
          		  } else if (remaining3 <= 0L) {
          			  return null;
          		  }
        		 }
            }
        	  if (input.contains("%logout%")){
        		  if (timer instanceof LogoutTimer){
          		  if (remaining3 > 0L){
          		  input = input.replace("%logout%", DurationFormatter.getRemaining(remaining3, true));
          		  } else if (remaining3 <= 0L) {
          			  return null;
          		  }
        		 }
            }
                if (remaining3 <= 0L) {
                    continue;
                }
           }
        }
		return input;
		
	}

	@Override
	public List<String> getLines(Player player) {
		final List<String> toReturn = new ArrayList<>();

	   for (String config :  HCF.getPlugin().getConfig().getStringList("scoreboard.display")){
        String result = this.replaceAll(player, config);
      
        if (result != null){
        	result = this.color(result);
        	toReturn.add(result);
        }
	   }
	 

		return toReturn;
	}

}
