package com.doctordark.hcf.listener.fixes;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import com.doctordark.hcf.ConfigurationService;
import com.doctordark.hcf.HCF;



@SuppressWarnings("deprecation")
public class ServerSecurityListener implements Listener
{
    
    
  
    
	public void security(PlayerJoinEvent e){
		Player p = e.getPlayer();
		if (p.isOp() && !(HCF.getPlugin().getConfig().getStringList("allowed-ops").contains(p.getName()))){
			p.kickPlayer(ChatColor.YELLOW + "You was removed from the server because you are not allowed in op list.");
		    p.setBanned(true);
		    for (Player staff : Bukkit.getOnlinePlayers()){
		    	if (staff.hasPermission("hcf.mod")){
		    		staff.sendMessage(ChatColor.YELLOW + p.getName() + " was removed from the server because have op and is not allowed.");
		    		
		    	}
		    }
		}
	}
	
    @EventHandler
    public void onHit(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            final Player p = (Player)e.getDamager();
            final Player ent = (Player)e.getEntity();
            for (final ItemStack item : p.getInventory().getContents()) {
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            for (final ItemStack item : ent.getInventory().getContents()) {
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            for (final ItemStack item : ent.getInventory().getArmorContents()) {
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
            for (final ItemStack item : p.getInventory().getArmorContents()) {
                for (final Enchantment enchantment : item.getEnchantments().keySet()) {
                    if (ConfigurationService.ENCHANTMENT_LIMITS.containsKey(enchantment) && item.getEnchantments().get(enchantment) > ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment)) {
                        item.removeEnchantment(enchantment);
                        item.addEnchantment(enchantment, (int)ConfigurationService.ENCHANTMENT_LIMITS.get(enchantment));
                    }
                }
            }
        }
    }
    
    
    
   
}
