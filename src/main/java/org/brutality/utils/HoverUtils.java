package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class HoverUtils {

    public static boolean isWithinBoxRadius(int x, int y, int height, int width) {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
