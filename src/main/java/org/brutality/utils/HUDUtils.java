package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class HUDUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawImage(ResourceLocation resourceLocation, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(resourceLocation);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }
}
