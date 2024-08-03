package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Health extends Module {
    private final SimpleModeSetting modeSetting = new SimpleModeSetting("Display Mode", this, "Hearts", new String[]{"Hearts", "Percentage"});
    private final BooleanSetting showHeartSymbol = new BooleanSetting("Show Heart Symbol", this, true);
    private final ColorSetting colorSetting = new ColorSetting("Color", this, Color.GREEN);

    private int posX = 10, posY = 10;
    private int dragX, dragY;
    private boolean dragging = false;

    public Health() {
        super("Health", "Displays your health in different formats.", Category.RENDER);
        addSettings(modeSetting, showHeartSymbol, colorSetting);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        if (player == null) return;

        if (mc.currentScreen != null) {
            handleDragging();
        }

        String displayText;
        String heartSymbol = "\u2764"; // Unicode heart symbol
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float healthPercentage = (health / maxHealth) * 100.0f;

        switch (modeSetting.getValue()) {
            case "Hearts":
                displayText = String.format("%.1f", health / 2);
                break;
            case "Percentage":
                displayText = String.format("%.1f%%", healthPercentage);
                break;
            default:
                displayText = "";
        }

        int color = colorSetting.getColor().getRGB();
        ScaledResolution sr = new ScaledResolution(mc);
        GL11.glPushMatrix();
        mc.fontRendererObj.drawStringWithShadow(displayText, posX, posY, color);

        if (showHeartSymbol.isEnabled()) {
            int textWidth = mc.fontRendererObj.getStringWidth(displayText);
            if (modeSetting.getValue().equals("Percentage")) {
                mc.fontRendererObj.drawStringWithShadow(heartSymbol, posX + textWidth + 4, posY, 0xFF0000); // Red heart symbol
            } else {
                mc.fontRendererObj.drawStringWithShadow(heartSymbol, posX + textWidth + 4, posY, 0xFF0000); // Red heart symbol
            }
        }

        GL11.glPopMatrix();
    }

    private void handleDragging() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - posX;
                dragY = sr.getScaledHeight() - Mouse.getY() / 2 - posY;
            }
            if (dragging) {
                posX = Mouse.getX() / 2 - dragX;
                posY = sr.getScaledHeight() - Mouse.getY() / 2 - dragY;
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        int mouseX = Mouse.getX() / 2;
        int mouseY = sr.getScaledHeight() - Mouse.getY() / 2;
        String heartSymbol = "\u2764";
        String displayText = String.format("%.1f", mc.thePlayer.getHealth() / 2) + " " + heartSymbol;
        int textWidth = mc.fontRendererObj.getStringWidth(displayText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= posX && mouseX <= posX + textWidth && mouseY >= posY && mouseY <= posY + textHeight;
    }
}
