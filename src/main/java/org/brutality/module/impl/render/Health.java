package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Mouse;

public class Health extends Module {
    private Minecraft mc = Minecraft.getMinecraft();
    private boolean dragging;
    private int dragX, dragY;
    private int posX = 10, posY = 10;

    public Health() {
        super("Health", "displays ur hearts on screen (this is draggable)", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);
        GlStateManager.pushMatrix();
        handleDragging(sr);

        float health = mc.thePlayer.getHealth();
        String healthText = String.format("%.1f", health / 2.0f);
        int healthColor = getHealthColor(health);

        mc.fontRendererObj.drawStringWithShadow(healthText, posX, posY, healthColor);
        mc.fontRendererObj.drawStringWithShadow("❤", posX + mc.fontRendererObj.getStringWidth(healthText) + 2, posY, 0xFF0000);

        GlStateManager.popMatrix();
    }

    private int getHealthColor(float health) {
        if (health >= 16) { // 8 hearts or more
            return 0x00FF00; // Green
        } else if (health >= 8) { // 4 to 7 hearts
            return 0xFFFF00; // Yellow
        } else { // 3 hearts or less
            return 0xFF0000; // Red
        }
    }

    private void handleDragging(ScaledResolution sr) {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver(posX, posY, posX + mc.fontRendererObj.getStringWidth("20.0❤") + 2, posY + mc.fontRendererObj.FONT_HEIGHT)) {
                dragging = true;
                dragX = Mouse.getX() / sr.getScaleFactor() - posX;
                dragY = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor() - posY;
            }
            if (dragging) {
                posX = Mouse.getX() / sr.getScaleFactor() - dragX;
                posY = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor() - dragY;
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver(int x, int y, int width, int height) {
        int mouseX = Mouse.getX() / new ScaledResolution(mc).getScaleFactor();
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / new ScaledResolution(mc).getScaleFactor();
        return mouseX >= x && mouseY >= y && mouseX <= width && mouseY <= height;
    }
}
