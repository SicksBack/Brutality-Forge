package org.brutality.notifications;

import org.brutality.BrutalityClient;
import net.minecraft.util.ChatComponentText;
import org.brutality.utils.interfaces.MC;

public class NotificationManager implements MC {
    public static void sendNotification(String notification) {
        mc.thePlayer.addChatMessage(new ChatComponentText("§4[BRUTALITY]§r " + notification));
    }
}
