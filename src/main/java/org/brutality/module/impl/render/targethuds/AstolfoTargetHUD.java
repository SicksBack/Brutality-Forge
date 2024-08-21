package org.brutality.module.impl.render.targethuds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AstolfoTargetHUD {

    private Minecraft mc = Minecraft.getMinecraft();
    private int hudX = mc.displayWidth - 180; // Adjusted for bottom-right position
    private int hudY = mc.displayHeight - 60;
    private EntityLivingBase lastHitTarget = null; // Store the last hit target

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (event.target instanceof EntityLivingBase) {
            lastHitTarget = (EntityLivingBase) event.target; // Update last hit target
        }
    }

    public void render(int x, int y, EntityLivingBase target) {
        if (target == null || target.getHealth() <= 0) {
            return; // Don't render if there's no valid target
        }

        int rectWidth = 160;
        int rectHeight = 40;
        int backgroundColor = 0x80000000; // Semi-transparent black background

        // Draw the background rectangle
        Gui.drawRect(x, y, x + rectWidth, y + rectHeight, backgroundColor);

        // Check if the target is a player and draw the target's skin or head
        if (target instanceof EntityPlayer) {
            AbstractClientPlayer player = (AbstractClientPlayer) target;
            ResourceLocation skin = player.getLocationSkin();
            mc.getTextureManager().bindTexture(skin);
            Gui.drawModalRectWithCustomSizedTexture(x + 5, y + 5, 8, 8, 32, 32, 32, 32);
        }

        // Draw the target's name
        mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 45, y + 10, 0xFFFFFFFF);

        // Draw the health bar
        int healthBarWidth = 100;
        int healthBarHeight = 10;
        int healthBarX = x + 45;
        int healthBarY = y + 25;

        float healthPercent = target.getHealth() / target.getMaxHealth();
        int healthColor = healthPercent > 0.5 ? 0xFF00FF00 : healthPercent > 0.25 ? 0xFFFFFF00 : 0xFFFF0000;

        Gui.drawRect(healthBarX, healthBarY, healthBarX + healthBarWidth, healthBarY + healthBarHeight, 0xFF555555);
        Gui.drawRect(healthBarX, healthBarY, healthBarX + (int) (healthBarWidth * healthPercent), healthBarY + healthBarHeight, healthColor);

        // Draw the target's health with a heart icon
        String healthText = String.format("%.1f", target.getHealth());
        mc.fontRendererObj.drawStringWithShadow(healthText + " ❤", healthBarX + healthBarWidth + 5, healthBarY - 2, healthColor);
    }
}


