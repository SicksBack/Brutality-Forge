package org.brutality.utils;

public class ColorUtils {

    public static int getColor(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
