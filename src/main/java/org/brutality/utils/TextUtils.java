package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class TextUtils {

    public static void drawScaledString(String text, int x, int y, int color, float scale) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        fontRenderer.drawStringWithShadow(text, x / scale, y / scale, color);
    }
}
