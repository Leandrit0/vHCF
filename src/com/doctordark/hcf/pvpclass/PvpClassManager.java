package com.doctordark.hcf.pvpclass;

import org.bukkit.event.Event;

import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import java.util.Collection;
import org.bukkit.Bukkit;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;

import com.doctordark.hcf.HCF;
import com.doctordark.hcf.pvpclass.archer.ArcherClass;
import com.doctordark.hcf.pvpclass.bard.BardClass;
import com.doctordark.hcf.pvpclass.event.PvpClassEquipEvent;
import com.doctordark.hcf.pvpclass.event.PvpClassUnequipEvent;
import com.doctordark.hcf.pvpclass.type.AssassinClass;
import com.doctordark.hcf.pvpclass.type.MinerClass;

import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class PvpClassManager
{
    private final Map<UUID, PvpClass> equippedClass;
    private final Map<String, PvpClass> pvpClasses;
    
    public PvpClassManager(final HCF plugin) {
        this.equippedClass = new HashMap<UUID, PvpClass>();
        (this.pvpClasses = new HashMap<String, PvpClass>()).put("Archer", new ArcherClass(plugin));
        this.pvpClasses.put("Bard", new BardClass(plugin));
        this.pvpClasses.put("Miner", new MinerClass(plugin));
        this.pvpClasses.put("Reaper", new AssassinClass(plugin));
        for (final PvpClass pvpClass : this.pvpClasses.values()) {
            if (pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener)pvpClass, (Plugin)plugin);
            }
        }
    }
    
    public void onDisable() {
        for (final Map.Entry<UUID, PvpClass> entry : new HashMap<UUID, PvpClass>(this.equippedClass).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer((UUID)entry.getKey()), null);
        }
        this.pvpClasses.clear();
        this.equippedClass.clear();
    }
    
    public Collection<PvpClass> getPvpClasses() {
        return this.pvpClasses.values();
    }
    
    public PvpClass getPvpClass(final String name) {
        return this.pvpClasses.get(name);
    }
    
    public PvpClass getEquippedClass(final Player player) {
        synchronized (this.equippedClass) {
            return this.equippedClass.get(player.getUniqueId());
        }
    }
    
    public boolean hasClassEquipped(final Player player, final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        return equipped != null && equipped.equals(pvpClass);
    }
    
    public void setEquippedClass(final Player player, @Nullable final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        if (equipped != null) {
            if (pvpClass == null) {
                this.equippedClass.remove(player.getUniqueId());
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent((Event)new PvpClassUnequipEvent(player, equipped));
                return;
            }
        }
        else if (pvpClass == null) {
            return;
        }
        if (pvpClass.onEquip(player)) {
            this.equippedClass.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent((Event)new PvpClassEquipEvent(player, pvpClass));
        }
    }
}
