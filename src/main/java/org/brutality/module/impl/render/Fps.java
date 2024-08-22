package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class Fps extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 5.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public Fps() {
        super("Fps", "Displays the current FPS.", Category.RENDER);
        addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.type != RenderGameOverlayEvent.ElementType.CHAT || !isToggled()) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging();
        }

        FontRenderer fr = mc.fontRendererObj;
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();

        int fps = Minecraft.getDebugFPS();

        String fpsText = "FPS: ";
        String fpsValue = String.valueOf(fps);

        // Draw "FPS: " in white
        fr.drawStringWithShadow(fpsText, posX, posY, 0xFFFFFF);

        // Draw the FPS value in green
        fr.drawStringWithShadow(fpsValue, posX + fr.getStringWidth(fpsText), posY, 0x00FF00);
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - (int) xPos.getValue();
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - (int) yPos.getValue();
            }
            if (dragging) {
                xPos.setValue(Mouse.getX() / 2 - dragX);
                yPos.setValue(new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY);
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 100 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 10;
    }
}
