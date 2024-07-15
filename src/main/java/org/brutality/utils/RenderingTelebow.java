package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class RenderingTelebow {

    private int posX = 10;
    private int posY = 10;
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public void renderTimer(String text, int x, int y, int color) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRendererObj;
        ScaledResolution sr = new ScaledResolution(mc);

        if (dragging) {
            posX = (Mouse.getX() / sr.getScaleFactor()) - dragX;
            posY = (Mouse.getY() / sr.getScaleFactor()) - dragY;
        }

        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOverText(Mouse.getX() / sr.getScaleFactor(), Mouse.getY() / sr.getScaleFactor(), text, mc)) {
                dragging = true;
                dragX = (Mouse.getX() / sr.getScaleFactor()) - posX;
                dragY = (Mouse.getY() / sr.getScaleFactor()) - posY;
            }
        } else {
            dragging = false;
        }

        fontRenderer.drawStringWithShadow(text, posX, posY, color);
    }

    private boolean isMouseOverText(int mouseX, int mouseY, String text, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= posX && mouseX <= posX + textWidth && mouseY >= posY && mouseY <= posY + textHeight;
    }
}
