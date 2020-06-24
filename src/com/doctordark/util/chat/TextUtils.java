package com.doctordark.util.chat;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import java.util.Iterator;
import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import java.util.Collection;

public class TextUtils
{
    public static Text join(final Collection<Text> textCollection, final String delimiter) {
        final Text result = new Text();
        Text prefix = new Text();
        for (final Text text : textCollection) {
            result.append((IChatBaseComponent)prefix).append((IChatBaseComponent)text);
            prefix = new Text(", ");
        }
        return result;
    }
    
    public static Text joinItemList(final Collection<ItemStack> collection, final String delimiter, final boolean showQuantity) {
        final Text text = new Text();
        for (final ItemStack stack : collection) {
            if (stack == null) {
                continue;
            }
            text.append((IChatBaseComponent)new Text(delimiter));
            if (showQuantity) {
                text.append((IChatBaseComponent)new Text("[").setColor(ChatColor.YELLOW));
            }
            text.appendItem(stack);
            if (!showQuantity) {
                continue;
            }
            text.append((IChatBaseComponent)new Text(" x" + stack.getAmount()).setColor(ChatColor.YELLOW)).append((IChatBaseComponent)new Text("]").setColor(ChatColor.YELLOW));
        }
        return text;
    }
}
