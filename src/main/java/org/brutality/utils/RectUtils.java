package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class RectUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Draws a rectangle with custom styles for the "Exhibition" target HUD.
     */
    public static void drawExhiRect(int x, int y, int width, int height) {
        int color1 = 0xFF000000; // Black color with full opacity
        int color2 = 0xFF555555; // Dark gray color with full opacity
        Gui.drawRect(x, y, x + width, y + height, color1); // Main rectangle
        Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, color2); // Inner rectangle
    }

    /**
     * Draws a thin rectangle with custom styles for the "Exhibition" target HUD.
     */
    public static void drawThinExhiRect(int x, int y, int width, int height) {
        int borderColor = 0xFFFFFFFF; // White color with full opacity
        Gui.drawRect(x, y, x + width, y + 1, borderColor); // Top border
        Gui.drawRect(x, y, x + 1, y + height, borderColor); // Left border
        Gui.drawRect(x + width - 1, y, x + width, y + height, borderColor); // Right border
        Gui.drawRect(x, y + height - 1, x + width, y + height, borderColor); // Bottom border
    }
}
