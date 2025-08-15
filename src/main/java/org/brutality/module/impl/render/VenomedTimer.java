package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Mouse;

public class VenomedTimer extends Module {

    private int venomedTextX = 10;  // Initial X position
    private int venomedTextY = 10;  // Initial Y position
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public VenomedTimer() {
        super("VenomedTimer", "stop venom hopping", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player.isPotionActive(Potion.poison)) {
            PotionEffect effect = player.getActivePotionEffect(Potion.poison);
            int duration = effect.getDuration() / 20;
            String text = "\u00A75Venomed: \u00A7d" + duration + "s";

            if (mc.currentScreen instanceof GuiChat) {
                if (dragging) {
                    venomedTextX = Mouse.getX() / 2 - dragX;
                    venomedTextY = (mc.displayHeight - Mouse.getY()) / 2 - dragY;
                }

                if (Mouse.isButtonDown(0)) {
                    if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, text, mc)) {
                        dragging = true;
                        dragX = Mouse.getX() / 2 - venomedTextX;
                        dragY = (mc.displayHeight - Mouse.getY()) / 2 - venomedTextY;
                    }
                } else {
                    dragging = false;
                }
            }

            mc.fontRendererObj.drawStringWithShadow(text, venomedTextX, venomedTextY, -1);
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, String text, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= venomedTextX && mouseX <= venomedTextX + textWidth
                && mouseY >= venomedTextY && mouseY <= venomedTextY + textHeight;
    }
}
