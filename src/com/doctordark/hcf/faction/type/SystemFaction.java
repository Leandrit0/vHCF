package com.doctordark.hcf.faction.type;

import java.util.Map;

public class SystemFaction extends ClaimableFaction {

	public SystemFaction(String name, boolean deathban) {
		super(name);
		this.safezone = deathban;
        
		
	}
    public SystemFaction(final Map<String, Object> map) {
        super(map);
    }

}
