package com.doctordark.util.bossbar;

import net.minecraft.server.v1_7_R4.EntityLiving;
import com.google.common.base.Preconditions;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.Location;
import net.minecraft.server.v1_7_R4.EntityEnderDragon;
import net.minecraft.server.v1_7_R4.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R4.PacketPlayOutEntityDestroy;

public class BossBar
{
    public static final int MAX_TITLE_LENGTH = 64;
    public static final int MIN_HEALTH = 0;
    public static final int MAX_HEALTH = 200;
    public PacketPlayOutEntityDestroy destroyPacket;
    public PacketPlayOutSpawnEntityLiving spawnPacket;
    private EntityEnderDragon dragon;
    private Location location;
    private String title;
    private float health;
    
    public BossBar(final Location location, final String title) {
        this(location, title, 100);
    }
    
    public BossBar(final Location location, final String title, final int percent) {
        this.dragon = new EntityEnderDragon((World)((CraftWorld)location.getWorld()).getHandle());
        this.setLocation(location);
        this.setTitle(title);
        this.setHealth(percent / 100.0f * 200.0f);
        this.reloadPackets();
    }
    
    public EntityEnderDragon getDragon() {
        return this.dragon;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public void setLocation(final Location location) {
        Preconditions.checkNotNull((Object)location, (Object)"Location cannot be null");
        Preconditions.checkNotNull((Object)location.getWorld(), (Object)"Location world cannot be null");
        this.location = location;
        this.dragon.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        Preconditions.checkNotNull((Object)title, (Object)"Text cannot be null.");
        Preconditions.checkArgument(title.length() < 64, (Object)"Text cannot be longer than 64 characters.");
        this.title = title;
        this.dragon.setCustomName(title);
        this.dragon.setCustomNameVisible(true);
    }
    
    public float getHealth() {
        return this.health;
    }
    
    public void setHealth(final float health) {
        Preconditions.checkArgument(health >= 0.0f, (Object)("Health of " + health + " is less than minimum health: " + 0));
        Preconditions.checkArgument(health <= 200.0f, (Object)("Health of " + health + " is more than maximum health: " + 200));
        this.health = health;
        this.dragon.setHealth(health);
    }
    
    private void reloadPackets() {
        this.spawnPacket = new PacketPlayOutSpawnEntityLiving((EntityLiving)this.dragon);
        this.destroyPacket = new PacketPlayOutEntityDestroy(new int[] { this.dragon.getId() });
    }
}
