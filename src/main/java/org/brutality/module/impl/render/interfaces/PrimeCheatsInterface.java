package org.brutality.module.impl.render.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

public class PrimeCheatsInterface {

    private static final EnumChatFormatting COLOR = EnumChatFormatting.DARK_AQUA; // Dark cyan color

    private final Minecraft mc;

    public PrimeCheatsInterface(Minecraft mc) {
        this.mc = mc;
    }

    public void render(ScaledResolution sr) {
        // Draw the text "primecheats.gg" in dark aqua color at position (x = 1, y = 1)
        mc.fontRendererObj.drawStringWithShadow(COLOR + "primecheats.gg", 1, 1, 0xFFFFFF);
    }
}
