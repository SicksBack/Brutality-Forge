package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class SewerESP extends Module {
    private boolean showNotification = false;
    private int notificationTicks = 0;
    private static final int NOTIFICATION_DURATION = 60; // 3 seconds at 20 ticks per second

    public SewerESP() {
        super("SewerESP", "Displays a notification when a new treasure spawns in the sewers.", Category.PIT);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains("SEWERS! A new treasure spawned somewhere")) {
            showNotification = true;
            notificationTicks = NOTIFICATION_DURATION;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (showNotification) {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer fr = mc.fontRendererObj;
            ScaledResolution sr = new ScaledResolution(mc);

            String text = "New Treasure Spawned!";
            int x = (sr.getScaledWidth() - fr.getStringWidth(text)) / 2;
            int y = sr.getScaledHeight() / 2 - fr.FONT_HEIGHT / 2;

            fr.drawStringWithShadow(text, x, y, 0xFFFF00);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (showNotification) {
            notificationTicks--;
            if (notificationTicks <= 0) {
                showNotification = false;
            }
        }
    }
}
