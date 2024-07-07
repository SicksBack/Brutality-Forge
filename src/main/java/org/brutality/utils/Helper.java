package org.brutality.utils;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.Minecraft;

public class Helper
{
    public static Minecraft mc;

    public static ScaledResolution getScaledResolution() {
        return new ScaledResolution(Minecraft.getMinecraft());
    }

    static {
        Helper.mc = Minecraft.getMinecraft();
    }
}