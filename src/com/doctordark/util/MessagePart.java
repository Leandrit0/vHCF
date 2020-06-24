package com.doctordark.util;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.com.google.gson.stream.JsonWriter;



final class MessagePart
{
  ChatColor color;
  ChatColor[] styles;
  String clickActionName;
  String clickActionData;
  String hoverActionName;
  String hoverActionData;
  final String text;
  
  MessagePart(String text) { this.text = text; }



  
  JsonWriter writeJson(JsonWriter json) {
    try {
      json.beginObject().name("text").value(this.text);
      if (this.color != null) {
        json.name("color").value(this.color.name().toLowerCase());
      }
      if (this.styles != null) {
        ChatColor[] arrayOfChatColor = null;
        
        int arrayOfChatColor1 = this.styles.length;
		int j = arrayOfChatColor1 ;
        for (int i = 0; i < j; i++) {
          
          ChatColor style = arrayOfChatColor[i];
          json.name(style.name().toLowerCase()).value(true);
        } 
      } 
      if (this.clickActionName != null && this.clickActionData != null) {
        json.name("clickEvent").beginObject().name("action").value(this.clickActionName).name("value").value(this.clickActionData).endObject();
      }
      if (this.hoverActionName != null && this.hoverActionData != null) {
        json.name("hoverEvent").beginObject().name("action").value(this.hoverActionName).name("value").value(this.hoverActionData).endObject();
      }
      return json.endObject();
    }
    catch (Exception e) {
      
      e.printStackTrace();
      
      return json;
    } 
  }
}
