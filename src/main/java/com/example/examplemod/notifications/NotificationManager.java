package com.example.examplemod.notifications;

import com.example.examplemod.ExampleMod;
import net.minecraft.util.ChatComponentText;

public class NotificationManager {
    public static void sendNotification(String notification) {
        ExampleMod.mc.thePlayer.addChatMessage(new ChatComponentText("\u00A74[BRUTALITY]\u00A7r " + notification));
    }
}
