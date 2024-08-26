package org.brutality.module.impl.render.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.brutality.module.impl.render.Notifications;

import java.util.List;

public class PrimeCheatsNotification {

    private static final int DISPLAY_DURATION = 9000; // 9 seconds

    public void renderNotifications(Minecraft mc, FontRenderer fontRenderer) {
        List<Notifications.Notification> notifications = Notifications.getActiveNotifications();
        long currentTime = System.currentTimeMillis();
        int posX = 3; // X position for notifications
        int startY = 11; // Starting Y position for notifications
        int lineOffset = fontRenderer.FONT_HEIGHT + 2; // Line height between messages

        int posY = startY;

        for (Notifications.Notification notification : notifications) {
            long elapsed = currentTime - notification.getTimestamp();
            if (elapsed <= DISPLAY_DURATION) {
                fontRenderer.drawStringWithShadow(notification.getMessage(), posX, posY, 0xFFFFFF); // White text
                posY += lineOffset;
            }
        }
    }
}
