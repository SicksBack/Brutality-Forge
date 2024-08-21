package org.brutality.utils;

import java.awt.Color;

public class Colors {

    public static int GREEN = new Color(0, 255, 0).getRGB(); // Example color
    public static int YELLOW = new Color(255, 255, 0).getRGB(); // Example color
    public static int RED = new Color(255, 0, 0).getRGB(); // Example color

    public static int blendColors(float duration, float max, float factor, int color1, int color2) {
        float ratio = factor / max;
        int r = (int) ((getRed(color1) * ratio) + (getRed(color2) * (1 - ratio)));
        int g = (int) ((getGreen(color1) * ratio) + (getGreen(color2) * (1 - ratio)));
        int b = (int) ((getBlue(color1) * ratio) + (getBlue(color2) * (1 - ratio)));
        return new Color(r, g, b).getRGB();
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
        // Placeholder for duration calculation
        return 1;
    }
}
