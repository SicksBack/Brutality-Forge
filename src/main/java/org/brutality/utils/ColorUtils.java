package org.brutality.utils;

import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ColorUtils {

    /**
     * Translates Minecraft color codes from the format used in strings to EnumChatFormatting.
     *
     * @param text The input text with color codes.
     * @return The translated text with Minecraft color formatting.
     */
    public static String translate(String text) {
        // Replace color codes with Minecraft EnumChatFormatting codes
        text = text.replaceAll("&0", EnumChatFormatting.BLACK.toString());
        text = text.replaceAll("&1", EnumChatFormatting.DARK_BLUE.toString());
        text = text.replaceAll("&2", EnumChatFormatting.DARK_GREEN.toString());
        text = text.replaceAll("&3", EnumChatFormatting.DARK_AQUA.toString());
        text = text.replaceAll("&4", EnumChatFormatting.DARK_RED.toString());
        text = text.replaceAll("&5", EnumChatFormatting.DARK_PURPLE.toString());
        text = text.replaceAll("&6", EnumChatFormatting.GOLD.toString());
        text = text.replaceAll("&7", EnumChatFormatting.GRAY.toString());
        text = text.replaceAll("&8", EnumChatFormatting.DARK_GRAY.toString());
        text = text.replaceAll("&9", EnumChatFormatting.BLUE.toString());
        text = text.replaceAll("&a", EnumChatFormatting.GREEN.toString());
        text = text.replaceAll("&b", EnumChatFormatting.AQUA.toString());
        text = text.replaceAll("&c", EnumChatFormatting.RED.toString());
        text = text.replaceAll("&d", EnumChatFormatting.LIGHT_PURPLE.toString());
        text = text.replaceAll("&e", EnumChatFormatting.YELLOW.toString());
        text = text.replaceAll("&f", EnumChatFormatting.WHITE.toString());
        text = text.replaceAll("&k", EnumChatFormatting.OBFUSCATED.toString());
        text = text.replaceAll("&l", EnumChatFormatting.BOLD.toString());
        text = text.replaceAll("&m", EnumChatFormatting.STRIKETHROUGH.toString());
        text = text.replaceAll("&n", EnumChatFormatting.UNDERLINE.toString());
        text = text.replaceAll("&o", EnumChatFormatting.ITALIC.toString());
        text = text.replaceAll("&r", EnumChatFormatting.RESET.toString());
        return text;
    }
}
