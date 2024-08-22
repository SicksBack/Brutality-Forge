package org.brutality.utils;

import java.awt.Color;

public class Colors {

    public static final int GREEN = new Color(0, 255, 0).getRGB(); // Example color
    public static final int YELLOW = new Color(255, 255, 0).getRGB(); // Example color
    public static final int RED = new Color(255, 0, 0).getRGB(); // Example color
    public static final int AQUA = new Color(0, 255, 255).getRGB(); // Aqua color

    public static int blendColors(float duration, float max, float factor, int color1, int color2) {
        float ratio = factor / max;
        int r = clamp((int) ((getRed(color1) * ratio) + (getRed(color2) * (1 - ratio))));
        int g = clamp((int) ((getGreen(color1) * ratio) + (getGreen(color2) * (1 - ratio))));
        int b = clamp((int) ((getBlue(color1) * ratio) + (getBlue(color2) * (1 - ratio))));
        return new Color(r, g, b).getRGB();
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }

    public static int getBlue(int color) {
        return color & 0xFF;
    }

    public static int getDura() {
        return 1;
    }
}
