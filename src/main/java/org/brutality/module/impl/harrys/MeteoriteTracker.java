package org.brutality.module.impl.harrys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class MeteoriteTracker extends Module {
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean tracking = false;
    private int targetX = 0;
    private int targetZ = 0;

    public MeteoriteTracker() {
        super("MeteoriteTracker", "Tracks meteorite coordinates and sets a waypoint", Category.HARRYS);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.contains("MINOR EVENT! METEORITE!")) {
            String[] parts = message.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("x:")) {
                    targetX = Integer.parseInt(parts[i + 1]);
                }
                if (parts[i].equals("Z:")) {
                    targetZ = Integer.parseInt(parts[i + 1].replace(")", "").replace(".", ""));
                }
            }
            tracking = true;
        }

        if (message.contains("METEORITE! Ended")) {
            tracking = false;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (tracking) {
            ScaledResolution sr = new ScaledResolution(mc);
            String text = "Tracking Meteorite at X: " + targetX + " Z: " + targetZ;
            int width = sr.getScaledWidth();
            int height = sr.getScaledHeight();
            mc.fontRendererObj.drawStringWithShadow(text, width / 2 - mc.fontRendererObj.getStringWidth(text) / 2, height / 2 + 10, 0xFF00FF00);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (tracking) {
            // You can add code here to move the player or display a waypoint marker.
            // For simplicity, this example just displays the coordinates on the screen.
        }
    }
}
