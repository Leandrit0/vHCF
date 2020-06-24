package com.doctordark.hcf.api;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class Crowbar
{
    public static final int MAX_SPAWNER_USES = 1;
    public static final int MAX_END_FRAME_USES = 5;
    public static final Material CROWBAR_TYPE;
    private static final String CROWBAR_NAME;
    private static final String SPAWNER_USE_TAG = "Spawner Uses";
    private static final String END_FRAME_USE_TAG = "End Frame Uses";
    private static final String LORE_FORMAT;
    private int endFrameUses;
    private int spawnerUses;
    private final ItemStack stack;
    private boolean needsMetaUpdate;
    
    public Crowbar() {
        this(1, 5);
    }
    
    public Crowbar(final int spawnerUses, final int endFrameUses) {
        this.stack = new ItemStack(Crowbar.CROWBAR_TYPE, 1);
        Preconditions.checkArgument(spawnerUses > 0 || endFrameUses > 0, (Object)"Cannot create a crowbar with empty uses");
        this.setSpawnerUses(Math.min(1, spawnerUses));
        this.setEndFrameUses(Math.min(5, endFrameUses));
    }
    
    public static Optional<Crowbar> fromStack(final ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) {
            return Optional.absent();
        }
        final ItemMeta meta = stack.getItemMeta();
        if (!meta.hasDisplayName() || !meta.hasLore() || !meta.getDisplayName().equals(Crowbar.CROWBAR_NAME)) {
            return Optional.absent();
        }
        final Crowbar crowbar = new Crowbar();
        final List<String> loreList = (List<String>)meta.getLore();
        for (String lore : loreList) {
            lore = ChatColor.stripColor(lore);
            for (int length = lore.length(), i = 0; i < length; ++i) {
                final char character = lore.charAt(i);
                if (Character.isDigit(character)) {
                    final int amount = Integer.parseInt(String.valueOf(character));
                    if (lore.startsWith("End Frame Uses")) {
                        crowbar.setEndFrameUses(amount);
                        break;
                    }
                    if (lore.startsWith("Spawner Uses")) {
                        crowbar.setSpawnerUses(amount);
                        break;
                    }
                }
            }
        }
        return Optional.of(crowbar);
    }
    
    public int getEndFrameUses() {
        return this.endFrameUses;
    }
    
    public void setEndFrameUses(final int uses) {
        if (this.endFrameUses != uses) {
            this.endFrameUses = Math.min(5, uses);
            this.needsMetaUpdate = true;
        }
    }
    
    public int getSpawnerUses() {
        return this.spawnerUses;
    }
    
    public void setSpawnerUses(final int uses) {
        if (this.spawnerUses != uses) {
            this.spawnerUses = Math.min(1, uses);
            this.needsMetaUpdate = true;
        }
    }
    
    public ItemStack getItemIfPresent() {
        final Optional<ItemStack> optional = this.toItemStack();
        return (ItemStack)(optional.isPresent() ? optional.get() : new ItemStack(Material.AIR, 1));
    }
    
    public Optional<ItemStack> toItemStack() {
        if (this.needsMetaUpdate) {
            final double maxDurability;
            double curDurability = maxDurability = Crowbar.CROWBAR_TYPE.getMaxDurability();
            final double increment = curDurability / 6.0;
            curDurability -= increment * (this.spawnerUses + this.endFrameUses);
            if (Math.abs(curDurability - maxDurability) == 0.0) {
                return Optional.absent();
            }
            final ItemMeta meta = this.stack.getItemMeta();
            meta.setDisplayName(Crowbar.CROWBAR_NAME);
            meta.setLore((List)Arrays.<String>asList(String.format(Crowbar.LORE_FORMAT, "Spawner Uses", this.spawnerUses, 1), String.format(Crowbar.LORE_FORMAT, "End Frame Uses", this.endFrameUses, 5)));
            this.stack.setItemMeta(meta);
            this.stack.setDurability((short)curDurability);
            this.needsMetaUpdate = false;
        }
        return Optional.of(this.stack);
    }
    
    static {
        CROWBAR_TYPE = Material.DIAMOND_HOE;
        CROWBAR_NAME = ChatColor.RED.toString() + "Crowbar";
        LORE_FORMAT = ChatColor.GRAY + "%1$s: " + ChatColor.YELLOW + "%2$s/%3$s";
    }
}
