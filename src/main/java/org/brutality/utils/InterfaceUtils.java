package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class InterfaceUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation MOON_ICON = new ResourceLocation("textures/gui/moon_icon.png");
    private static final ResourceLocation RISE_ICON = new ResourceLocation("textures/gui/rise_icon.png");

    public static void drawMoonIcon(int x, int y, int color) {
        mc.getTextureManager().bindTexture(MOON_ICON);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
    }

    public static void drawRiseIcon(int x, int y, int color) {
        mc.getTextureManager().bindTexture(RISE_ICON);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
    }
}
