package org.brutality.module.impl.render.targethuds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;

public class DefaultTargetHUD {

    private Minecraft mc = Minecraft.getMinecraft();

    public void render(int x, int y, EntityLivingBase target) {
        int rectWidth = 160;
        int rectHeight = 50;
        int backgroundColor = 0x80000000; // Semi-transparent black background

        // Draw background rectangle
        Gui.drawRect(x, y, x + rectWidth, y + rectHeight, backgroundColor);

        // Draw health bar
        float healthPercent = target.getHealth() / target.getMaxHealth();
        int healthBarColor = healthPercent > 0.5 ? 0xFF00FF00 : 0xFFFF0000; // Green for more than 50% health, red otherwise
        Gui.drawRect(x + 4, y + rectHeight - 10, (int) (x + 4 + (rectWidth - 8) * healthPercent), y + rectHeight - 5, healthBarColor);

        // Draw entity name and health
        mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 6, y + 6, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow(String.format("%.1f", target.getHealth()) + " ❤", x + 6, y + 18, 0xFFFFFF);
    }
}
