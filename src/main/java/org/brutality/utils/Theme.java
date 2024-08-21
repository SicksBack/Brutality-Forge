package org.brutality.utils;

import java.awt.Color;

public class Theme {

    // Default theme colors
    private static final Color DEFAULT_GRADIENT_START = new Color(0, 0, 0, 200);
    private static final Color DEFAULT_GRADIENT_END = new Color(0, 0, 0, 150);
    private static final Color HEALTHY_COLOR = new Color(0, 255, 0);  // Green
    private static final Color LOW_HEALTH_COLOR = new Color(255, 0, 0);  // Red

    // Method to get the start color of the gradient based on the theme value
    public static int getGradientStartColor(int themeValue) {
        // You can customize this to return different colors based on the theme value
        return DEFAULT_GRADIENT_START.getRGB();
    }

    // Method to get the end color of the gradient based on the theme value
    public static int getGradientEndColor(int themeValue) {
        // You can customize this to return different colors based on the theme value
        return DEFAULT_GRADIENT_END.getRGB();
    }

    // Method to get the color representing the health status
    public static int getColorForHealth(float health) {
        // Adjust color based on health level
        if (health > 0.5f) {
            return HEALTHY_COLOR.getRGB();
        } else if (health > 0.2f) {
            // Color transitioning from green to red
            return interpolateColor(HEALTHY_COLOR, LOW_HEALTH_COLOR, (0.5f - health) / 0.3f).getRGB();
        } else {
            return LOW_HEALTH_COLOR.getRGB();
        }
    }

    // Utility method to interpolate between two colors
    private static Color interpolateColor(Color color1, Color color2, float ratio) {
        int r = (int) (color1.getRed() * (1 - ratio) + color2.getRed() * ratio);
        int g = (int) (color1.getGreen() * (1 - ratio) + color2.getGreen() * ratio);
        int b = (int) (color1.getBlue() * (1 - ratio) + color2.getBlue() * ratio);
        return new Color(r, g, b);
    }
}
