package com.doctordark.hcf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class Color {
public static String color(String s) {
		
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	public static List < String > color(List < String > text) {
        List < String > messages = new ArrayList < String > ();
        for (String string: text) {
            messages.add(color(string));
        }
        return messages;
    }
}
