package org.brutality.module.impl.render.targethuds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.utils.HoverUtils;
import org.brutality.utils.RectUtils;
import org.brutality.utils.TextUtils;
import org.lwjgl.input.Mouse;

public class ExhibitionTargetHUD {

    private final Minecraft mc = Minecraft.getMinecraft();

    public void render(int x, int y, EntityLivingBase target) {
        if (target == null) return;

        // Variables for positioning and color
        int width = 140;
        int height = 50;
        int currentColor = -1;

        // Determine color based on health
        if (target.getHealth() > 17.5) {
            currentColor = -15875797;
        } else if (target.getHealth() > 15.0f) {
            currentColor = -14815168;
        } else if (target.getHealth() > 12.5) {
            currentColor = -4789977;
        } else if (target.getHealth() > 9.0f) {
            currentColor = -1704957;
        } else if (target.getHealth() > 5.0f) {
            currentColor = -1071856;
        } else if (target.getHealth() > 0.0f) {
            currentColor = -1308657;
        }

        ScaledResolution scaledResolution = new ScaledResolution(mc);

        // Handle HUD movement with mouse
        if (mc.currentScreen != null && HoverUtils.isWithinBoxRadius(x, y, height, width) && mc.currentScreen instanceof Gui) {
            // Get mouse coordinates
            int mouseX = Mouse.getX() * scaledResolution.getScaledWidth() / mc.displayWidth;
            int mouseY = scaledResolution.getScaledHeight() - Mouse.getY() * scaledResolution.getScaledHeight() / mc.displayHeight - 1;
            x = mouseX - width / 2;
            y = mouseY - height / 2;
        }

        int rectBehindPlayerX = x + 2;
        int rectBehindPlayerY = y + 2;
        int widthX = 46;
        int widthY = 46;

        // Draw the HUD
        RectUtils.drawExhiRect(x, y, width, height);
        RectUtils.drawThinExhiRect(rectBehindPlayerX, rectBehindPlayerY, widthX, widthY);
        TextUtils.drawScaledString(target.getName(), rectBehindPlayerX + widthX + 4, rectBehindPlayerY, -1, 1.0f);

        if (mc.thePlayer != null) {
            String playerinfo = "HP: " + target.getHealth() + " | Dist: " + String.format("%.1f", mc.thePlayer.getDistanceToEntity(target));
            TextUtils.drawScaledString(playerinfo, rectBehindPlayerX + widthX + 4, rectBehindPlayerY + mc.fontRendererObj.FONT_HEIGHT * 2 + 4, -1, 0.5f);
        }

        RenderManager renderManager = mc.getRenderManager();

        // Render the entity
        renderManager.renderEntityWithPosYaw(target, rectBehindPlayerX + widthX / 2, rectBehindPlayerY + widthY - 3, 21, target.rotationYaw, target.rotationPitch);

        Gui.drawRect(rectBehindPlayerX + widthX + 4, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.3),
                rectBehindPlayerX + width - 10, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.8), currentColor);
        Gui.drawRect(rectBehindPlayerX + widthX + 4, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.3),
                rectBehindPlayerX + width - 10, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.8), -16777216);
        Gui.drawRect(rectBehindPlayerX + widthX + 4, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.3),
                rectBehindPlayerX + 1 + widthX + 4, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.8), -16777216);

        int i = 0;
        while (i < 17) {
            int val = 10 + 5 * i;
            Gui.drawRect(rectBehindPlayerX + width - val, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.3),
                    rectBehindPlayerX + 1 + width - val, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 1.8), -16777216);
            i++;
        }

        RenderHelper.enableGUIStandardItemLighting();
        int v = 0;
        int i2 = 0;
        while (i2 < 5) {
            int val = 71 - 17 * i2;
            mc.getRenderItem().renderItemAndEffectIntoGUI(target.getEquipmentInSlot(v),
                    rectBehindPlayerX + widthX + val, rectBehindPlayerY + (int) (mc.fontRendererObj.FONT_HEIGHT * 3.2));
            if (v < 4) {
                v++;
            }
            i2++;
        }
        RenderHelper.disableStandardItemLighting();
    }
}
