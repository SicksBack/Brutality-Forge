package org.brutality.module.impl.render.targethuds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.GuiChat; // Import GuiChat
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AstolfoTargetHUD {

    private Minecraft mc = Minecraft.getMinecraft();
    private int hudX = 10;
    private int hudY = 10;
    private int rectWidth = 160;
    private int rectHeight = 50;
    private EntityLivingBase lastHitTarget = null;

    private int color1 = 0xFF00FF; // Example colors
    private int color2 = 0x00FFFF;
    private int darkcolor1 = 0xAA00FF;
    private int darkcolor2 = 0x0055FF;
    private int lessdarkcolor1 = 0xCC00FF;
    private int lessdarkcolor2 = 0x0033FF;

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.target instanceof EntityLivingBase) {
            lastHitTarget = (EntityLivingBase) event.target;
        }
    }

    public void render(int x, int y, EntityLivingBase target) {
        if (target == null || target.getHealth() <= 0) {
            return; // No valid target
        }

        int backgroundColor = -921167848;
        int x2 = x + rectWidth;
        int y2 = y + rectHeight;

        if (Mouse.isButtonDown(0) && isWithinBox(x, y, rectWidth, rectHeight) && mc.currentScreen instanceof GuiChat) {
            int mouseX = Mouse.getX() * mc.displayWidth / mc.displayWidth;
            int mouseY = mc.displayHeight - Mouse.getY() * mc.displayHeight / mc.displayHeight - 1;
            x = mouseX - rectWidth / 2;
            y = mouseY - rectHeight / 2;
            hudX = x;
            hudY = y;
        }

        int color123 = blendColors(1.0f, color1, color2);
        int color1234 = blendColors(1.0f, darkcolor1, darkcolor2);
        int color12345 = blendColors(1.0f, lessdarkcolor1, lessdarkcolor2);

        Gui.drawRect(x, y, x2, y2, backgroundColor);

        int x_padding = 4;
        int y_padding = 4;
        float healthBarSect = target.getHealth() / target.getMaxHealth();
        double healthBarSize = x + rectWidth * healthBarSect;
        double starting_x = x + x_padding + 40;

        Gui.drawRect((int)starting_x, y2 - y_padding, (int)(x2 - x_padding), y2 - y_padding - 7, color1234);
        Gui.drawRect((int)starting_x, y2 - y_padding, (int)(healthBarSize - x_padding), y2 - y_padding - 7, color12345);
        Gui.drawRect((int)starting_x, y2 - y_padding, (int)(healthBarSize - x_padding), y2 - y_padding - 7, color123);

        String healthText = String.format("%.1f", target.getHealth()) + " ❤";
        mc.fontRendererObj.drawStringWithShadow(healthText, (int)starting_x, y2 - 33, color123);

        mc.fontRendererObj.drawStringWithShadow(target.getName(), (int)starting_x, y2 - 45, -1);

        GuiInventory.drawEntityOnScreen(x + x_padding + 18, y2 + y_padding - 9, 22, target.rotationYaw, target.rotationPitch, target);
    }

    private boolean isWithinBox(int x, int y, int width, int height) {
        int mouseX = Mouse.getX() * mc.displayWidth / mc.displayWidth;
        int mouseY = mc.displayHeight - Mouse.getY() * mc.displayHeight / mc.displayHeight - 1;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private int blendColors(float factor, int color1, int color2) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));
        return (r << 16) | (g << 8) | b;
    }
}
