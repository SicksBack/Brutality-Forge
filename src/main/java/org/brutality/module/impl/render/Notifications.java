package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.render.notifications.PrimeCheatsNotification;
import org.brutality.settings.impl.SimpleModeSetting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Notifications extends Module {

    private final SimpleModeSetting displayModeSetting = new SimpleModeSetting("Display Mode", this, "Brutality", new String[]{"Brutality", "PrimeCheats"});
    private final PrimeCheatsNotification primeCheatsNotification = new PrimeCheatsNotification();

    private static final List<Notification> notifications = new ArrayList<>();

    public Notifications() {
        super("Notifications", "Displays notifications for module toggles.", Category.RENDER);
        addSettings(displayModeSetting);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.TEXT) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;

        String mode = displayModeSetting.getValue();
        if ("PrimeCheats".equals(mode)) {
            primeCheatsNotification.renderNotifications(mc, fontRenderer);
        }
    }

    // Method to send a notification
    public static void sendNotification(String message) {
        notifications.add(new Notification(message, System.currentTimeMillis()));
    }

    // Method to get the list of active notifications (Notification objects)
    public static List<Notification> getActiveNotifications() {
        long currentTime = System.currentTimeMillis();
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification notification = iterator.next();
            if (currentTime - notification.getTimestamp() > 9000) { // 9 seconds duration
                iterator.remove();
            }
        }
        return new ArrayList<>(notifications);
    }

    // Inner class to store notifications with their timestamps
    public static class Notification {
        private final String message;
        private final long timestamp;

        public Notification(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }

        public String getMessage() {
            return message;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
