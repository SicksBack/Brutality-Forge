package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

import java.util.Collection;

public class PotionEffectsHUD extends Module {

    private final NumberSetting xPos = new NumberSetting("X Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public PotionEffectsHUD() {
        super("PotionEffectsHUD", "Displays active potion effects on the HUD", Category.RENDER);
        addSettings(xPos, yPos);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging();
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) {
            return;
        }

        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);

            int posX = (int) this.xPos.getValue();
            int posY = (int) this.yPos.getValue();

            GlStateManager.pushMatrix();
            GlStateManager.translate(posX, posY, 0);

            Collection<PotionEffect> effects = mc.thePlayer.getActivePotionEffects();
            int y = 0;
            for (PotionEffect effect : effects) {
                Potion potion = Potion.potionTypes[effect.getPotionID()];
                String name = potion.getName() + " " + Potion.getDurationString(effect);
                mc.fontRendererObj.drawStringWithShadow(name, 0, y, potion.getLiquidColor());
                y += mc.fontRendererObj.FONT_HEIGHT + 1;
            }

            GlStateManager.popMatrix();
        }
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
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 200 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 20;
    }
}
