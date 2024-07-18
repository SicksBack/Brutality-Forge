package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class Health extends Module {
    private int posX = 10, posY = 10;
    private int dragX, dragY;
    private boolean dragging = false;
    private CustomFontRenderer fontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();

    public Health() {
        super("Health", "Shows the player's health", Category.RENDER);
        setKey(Keyboard.KEY_H);
        fontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        ScaledResolution sr = new ScaledResolution(mc);
        int y = posY;

        // Example render logic
        fontRenderer.drawStringWithShadow("Health: " + mc.thePlayer.getHealth(), posX, y, Color.RED.getRGB());
        handleDragging();
    }

    private void handleDragging() {
        if (mc.currentScreen != null && Mouse.isButtonDown(0)) {
            if (isMouseOver() && !dragging) {
                dragging = true;
                dragX = Mouse.getX() / 2 - posX;
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - posY;
            }
            if (dragging) {
                posX = Mouse.getX() / 2 - dragX;
                posY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY;
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= posX && mouseX <= posX + 100 && mouseY >= posY && mouseY <= posY + 20;
    }
}
