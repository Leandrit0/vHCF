package com.doctordark.hcf.visualise;

import org.bukkit.Location;

import lombok.Getter;

public class VisualBlock
{
   @Getter private final VisualType visualType;
   @Getter  private final VisualBlockData blockData;
   @Getter private final Location location;
    
    public VisualBlock(final VisualType visualType, final VisualBlockData blockData, final Location location) {
        this.visualType = visualType;
        this.blockData = blockData;
        this.location = location;
    }
    

}
