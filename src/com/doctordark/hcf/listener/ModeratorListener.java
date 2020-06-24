package com.doctordark.hcf.listener;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ModeratorListener {
    public static HashMap<UUID, ItemStack[]> PlayerInventoryContents;
    public static HashMap<UUID, ItemStack[]> PlayerArmorContents;
    
    public void saveInventory(Player p) {
    	PlayerInventoryContents.put(p.getUniqueId(), p.getInventory().getContents());
        PlayerArmorContents.put(p.getUniqueId(), p.getInventory().getArmorContents());
    }
    
    public void loadInventory(Player p){
    	p.getInventory().setContents((ItemStack[])ModeratorListener.PlayerInventoryContents.get(p.getUniqueId()));
        p.getInventory().setArmorContents((ItemStack[])ModeratorListener.PlayerArmorContents.get(p.getUniqueId()));
        PlayerInventoryContents.remove(p.getUniqueId());
        PlayerArmorContents.remove(p.getUniqueId());
    }
    
    
}
