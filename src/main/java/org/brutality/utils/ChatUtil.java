package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void displayDraggableMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
        // Here you might want to implement the draggable chat functionality.
        // This is just a placeholder for displaying the message.
    }
}
