package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.events.listeners.ClickListener;
import org.lwjgl.input.Mouse;

public class Cps extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 80.0, 0.0, 1200.0, 1);

    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private int cps = 0;
    private long lastUpdateTime = 0;

    public Cps() {
        super("Cps", "Displays the CPS (Clicks Per Second).", Category.RENDER);
        addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdateTime > 1000) {
                cps = ClickListener.getClicks();
                lastUpdateTime = currentTime;
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            ScaledResolution sr = new ScaledResolution(mc);
            FontRenderer fr = mc.fontRendererObj;

            if (mc.currentScreen instanceof GuiChat) {
                handleDragging();
            }

            float posX = (float) this.xPos.getValue();
            float posY = (float) this.yPos.getValue();
            int indexTing = (int) posY;

            // Draw CPS with specific colors
            fr.drawStringWithShadow("CPS: ", posX, indexTing, 0xFFFFFF); // White color for label
            fr.drawStringWithShadow(String.valueOf(cps), posX + fr.getStringWidth("CPS: "), indexTing, 0x00FFFF); // Aqua color for CPS
        }
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() * mc.displayWidth / mc.displayWidth - (int) xPos.getValue();
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() * mc.displayHeight / mc.displayHeight - (int) yPos.getValue();
            }
            if (dragging) {
                xPos.setValue(Mouse.getX() * mc.displayWidth / mc.displayWidth - dragX);
                yPos.setValue(new ScaledResolution(mc).getScaledHeight() - Mouse.getY() * mc.displayHeight / mc.displayHeight - dragY);
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() * mc.displayWidth / mc.displayWidth;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() * mc.displayHeight / mc.displayHeight;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 100 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 20;
    }
}
