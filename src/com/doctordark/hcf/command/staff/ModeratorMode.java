package com.doctordark.hcf.command.staff;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import com.doctordark.hcf.Color;
import com.doctordark.hcf.HCF;

public class ModeratorMode
  implements Listener, CommandExecutor
{
  public static ArrayList<String> modMode = new ArrayList();
  private final Set<UUID> staffMode = new HashSet();
  ArrayList<Player> frozen = new ArrayList();
  private static HashMap<String, ItemStack[]> armorContents = new HashMap();
  private static HashMap<String, ItemStack[]> inventoryContents = new HashMap();
  private static HashMap<String, Integer> xplevel = new HashMap();
  
  public boolean isStaffModeActive(Player player) { return this.staffMode.contains(player.getUniqueId()); }

  
  public static void saveInventory(Player p) {
    armorContents.put(p.getName(), p.getInventory().getArmorContents());
    inventoryContents.put(p.getName(), p.getInventory().getContents());
    xplevel.put(p.getName(), Integer.valueOf(p.getLevel()));
  }

  
  public static void loadInventory(Player p) {
    p.getInventory().clear();
    p.getInventory().setContents((ItemStack[])inventoryContents.get(p.getName()));
    p.getInventory().setArmorContents((ItemStack[])armorContents.get(p.getName()));
    p.setLevel(((Integer)xplevel.get(p.getName())).intValue());
    
    inventoryContents.remove(p.getName());
    armorContents.remove(p.getName());
    xplevel.remove(p.getName());
  }

  
  public static void modItems(Player p) {
    PlayerInventory playerInventory = p.getInventory();
    playerInventory.clear();
    ItemStack modCompass = new ItemStack(Material.COMPASS);
    ItemStack modBook = new ItemStack(Material.BOOK);
    ItemStack modFreeze = new ItemStack(Material.PACKED_ICE);
    ItemStack modTp = new ItemStack(Material.EYE_OF_ENDER);
    ItemStack modVanish = new ItemStack(Material.FEATHER);
    
    ItemMeta compassMeta = modCompass.getItemMeta();
    ItemMeta bookMeta = modBook.getItemMeta();
    ItemMeta freezeMeta = modFreeze.getItemMeta();
    ItemMeta tpMeta = modTp.getItemMeta();
    ItemMeta vanishMeta = modVanish.getItemMeta();
    
    compassMeta.setDisplayName(Color.color("&cWhoosh"));
    bookMeta.setDisplayName(Color.color("&cInspect Player"));
    freezeMeta.setDisplayName(Color.color("&cFreeze Player"));
    vanishMeta.setDisplayName(Color.color("&cToggle Vanish"));
    tpMeta.setDisplayName(Color.color("&cRandom Teleporter"));
    
    ArrayList<String> modCompassLore = new ArrayList<String>();
    ArrayList<String> modBookLore = new ArrayList<String>();
    ArrayList<String> modFreezeLore = new ArrayList<String>();
    ArrayList<String> modVanishLore = new ArrayList<String>();
    ArrayList<String> modTpLore = new ArrayList<String>();
    
    modCompassLore.add(Color.color("&7Used to teleport to eye location."));
    modBookLore.add(Color.color("&7Used to inspect a players inventory."));
    modFreezeLore.add(Color.color("&7Used to freeze a player."));
    modVanishLore.add(Color.color("&7Used to toggle vanish."));
    modTpLore.add(Color.color("&7Used to teleport to a random player."));
    
    compassMeta.setLore(modCompassLore);
    bookMeta.setLore(modBookLore);
    freezeMeta.setLore(modFreezeLore);
    vanishMeta.setLore(modVanishLore);
    tpMeta.setLore(modTpLore);
    
    modCompass.setItemMeta(compassMeta);
    modBook.setItemMeta(bookMeta);
    modFreeze.setItemMeta(freezeMeta);
    modTp.setItemMeta(tpMeta);
    modVanish.setItemMeta(vanishMeta);
    
    playerInventory.setItem(0, modCompass);
    playerInventory.setItem(1, modVanish);
    playerInventory.setItem(2, modBook);
    playerInventory.setItem(7, modFreeze);
    playerInventory.setItem(8, modTp);
  }

  
  public static void enterMod(Player p) {
    modMode.add(p.getName());
    saveInventory(p);
    p.getInventory().clear();
    p.getInventory().setHelmet(null);
    p.getInventory().setChestplate(null);
    p.getInventory().setLeggings(null);
    p.getInventory().setBoots(null);
    p.setExp(0.0F);
    modItems(p);
    p.setGameMode(GameMode.CREATIVE);
    p.sendMessage(Color.color("&6 &rYou have &aenabled &rModerator Mode!"));
  }

  
  public static void leaveMod(Player p) {
    modMode.remove(p.getName());
    p.setGameMode(GameMode.SURVIVAL);
    p.getInventory().clear();
    loadInventory(p);
    p.sendMessage(Color.color("&6 &rYou have &cdisabled &rModerator Mode!"));
  }


  
  public static void onDisableMod(Player p) {
    if (p.hasMetadata("ModMode")) {
    	p.removeMetadata("ModMode", HCF.getPlugin());
    	leaveMod(p);
    }
  }


  
  public static void onEnableMod(Player p) {
   
      if (p.hasMetadata("ModMode")) {
        
        p.removeMetadata("ModMode", HCF.getPlugin());
        enterMod(p);
      } 
    
  }

  
  @EventHandler
  public void rightClick(PlayerInteractEntityEvent e) {
    if (!(e.getRightClicked() instanceof Player)) {
      return;
    }
    Player player = e.getPlayer();
    Player p = (Player)e.getRightClicked();
    if (modMode.contains(player.getName()) && p instanceof Player && player instanceof Player && player.getItemInHand().getType() == Material.BOOK) {
      player.openInventory(p.getInventory());
      player.sendMessage(Color.color(" &6& &rNow opening the inventory of &e" + p.getName() + ChatColor.GRAY + "&r."));
    
    }
    else if (modMode.contains(player.getName()) && p instanceof Player && player instanceof Player && player.getItemInHand().getType() == Material.PACKED_ICE) {
      player.sendMessage(Color.color(" &6& &rAttempting to freeze &e" + p.getName() + "&r."));
      player.chat("/ss " + p.getName());
    } 
  }
  
  @EventHandler
  public void onJoinMod(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    if (player.hasPermission(HCF.getPlugin().getConfig().getString("command.mod"))) {
      enterMod(player);
    }
  }

  
  @EventHandler
  public void onRightClick2(PlayerInteractEvent event) {
    if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      Player player = event.getPlayer();
      if (modMode.contains(player.getName()) && player.getItemInHand().getType() == Material.FEATHER) {
        player.chat("/v");
      }
    } 
    
  }
  @EventHandler
  public void onPutBlock(BlockPlaceEvent e) {
	  Player p = e.getPlayer();
	  if (modMode.contains(p.getName())) {
		  e.setCancelled(true);
	  }
  }

  
  @EventHandler
  public void onRecord(PlayerInteractEvent e) {
    Player p = e.getPlayer();
    if (modMode.contains(p.getName()) && p.getItemInHand().getType() == Material.EYE_OF_ENDER && e.getAction().toString().contains("RIGHT")) {
      
      Player random = Bukkit.getOnlinePlayers()[(new Random()).nextInt(Bukkit.getOnlinePlayers().length)];
      if (Bukkit.getOnlinePlayers().length == 1)
        p.sendMessage(ChatColor.RED + "Oops, seems like there are not enough players to use this."); 
      e.setCancelled(true);
      if (Bukkit.getOnlinePlayers().length > 1) {
        if (p != random) {
          p.teleport(random);
          p.sendMessage(Color.color(" &6& &rYou were teleported randomly to &c" + random.getName() + "&r."));
          e.setCancelled(true);
        } 
        if (p == random) {
          p.sendMessage(Color.color("&6 &rOops, seems like we accidently found you. Please try again!"));
          e.setCancelled(true);
        } 
      } 
    } 
  }

  
  @EventHandler
  public void onTag(EntityDamageByEntityEvent e) {
    if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) {
      return;
    }
    Player staff = (Player)e.getDamager();
    if (modMode.contains(staff.getName())) {
      e.setCancelled(true);
    }
  }

  
  @EventHandler
  public void onPickup(PlayerPickupItemEvent e) {
    Player staff = e.getPlayer();
    if (modMode.contains(staff.getName())) {
      e.setCancelled(true);
    }
  }

  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Player p = (Player)e.getWhoClicked();
    if (modMode.contains(p.getName()) && p.getGameMode().equals(GameMode.CREATIVE)) {
      e.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (modMode.contains(player) && player.getGameMode().equals(GameMode.CREATIVE)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void onDropModItems(PlayerDropItemEvent event) {
    Player player = event.getPlayer();
    if (modMode.contains(player)) {
      event.setCancelled(true);
    }
  }

  
  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    if (p.hasPermission(HCF.getPlugin().getConfig().getString("command.mod")) && 
      modMode.contains(p)) {
      leaveMod(p);
    }
  }

  
  @EventHandler
  public void onPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    Material block = event.getBlock().getType();
    if (modMode.contains(player.getName()) && 
      block == Material.CARPET) {
      event.setCancelled(true);
      player.updateInventory();
    } 
  }

  
  @EventHandler
  public void onPlayerPlacesBlock(BlockCanBuildEvent event) { event.setBuildable(true); }


  
  @EventHandler
  public void onBreak(BlockBreakEvent e) {
    Player p = e.getPlayer();
    if (modMode.contains(p.getName())) {
      e.setCancelled(true);
    }
  }

  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (cmd.getName().equalsIgnoreCase("mod")) {
      
      if (!sender.hasPermission(HCF.getPlugin().getConfig().getString("command.mod"))) {
        
        sender.sendMessage("No Permission.");
        return true;
      } 
      if (args.length < 1) {
        
        if (!(sender instanceof Player)) {
          
          sender.sendMessage("Not a player.");
          return true;
        } 
        if (modMode.contains(sender.getName())) {
          leaveMod((Player)sender);
          return true;
        } 
        enterMod((Player)sender);
        return true;
      } 
      Player t = Bukkit.getServer().getPlayer(args[0]);
      if (t == null) {
        
        sender.sendMessage(Color.color("&eCould not find player &6" + args[0].toString() + "&e."));
        return true;
      } 
      if (modMode.contains(t.getName())) {
        
        leaveMod(t);
        sender.sendMessage(Color.color(" &6& &rModerator Mode has been &cdisabled &rfor &e" + t.getName()) );
        return true;
      } 
      enterMod(t);
      sender.sendMessage(Color.color("&e &6& &rModerator Mode has been &aenabled &rfor &e" + t.getName()));
      return true;
    } 
    return false;
  }
}
