package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Interface extends Module {
    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Theme", this, "Brutality", new String[]{"Brutality", "PrimeCheats", "Moon", "Rise"});
    public final ColorSetting riseColorSetting = new ColorSetting("Rise Color", this, Color.MAGENTA);

    private CustomFontRenderer fontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation moonImage = new ResourceLocation("brutality/font/textures/moon.png");

    public Interface() {
        super("Interface", "Shows information about Brutality", Category.RENDER);
        addSettings(modeSetting, riseColorSetting);
        fontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (modeSetting.getValue().equals("Brutality")) {
            renderBrutality(sr);
        } else if (modeSetting.getValue().equals("PrimeCheats")) {
            renderPrimeCheats(sr);
        } else if (modeSetting.getValue().equals("Moon")) {
            renderMoon(sr);
        } else if (modeSetting.getValue().equals("Rise")) {
            renderRise(sr);
        }
    }

    private void renderBrutality(ScaledResolution sr) {
        String text = "Brutality";
        int color = Color.RED.getRGB();
        int x = 10;
        int y = 10;
        fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    private void renderPrimeCheats(ScaledResolution sr) {
        String text = "§3primecheats.gg";
        int color = 0xFF008080; // Original cyan color
        int x = 10;
        int y = 10;
        long time = System.currentTimeMillis();
        int duration = 3000; // 3 seconds

        int textLength = text.length();
        int charsToColor = (int) ((time % duration) / (float) duration * textLength);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < textLength; i++) {
            if (i < charsToColor) {
                sb.append("§b").append(text.charAt(i)); // Cyan color for changing
            } else {
                sb.append("§3").append(text.charAt(i)); // Original color
            }
        }

        fontRenderer.drawStringWithShadow(sb.toString(), x, y, color);
    }

    private void renderMoon(ScaledResolution sr) {
        int x = 10;
        int y = 10;
        mc.getTextureManager().bindTexture(moonImage);
        GL11.glColor4f(1, 1, 1, 1);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 100, 50, 100, 50);
    }

    private void renderRise(ScaledResolution sr) {
        String text = "Rise";
        int color = riseColorSetting.getColor().getRGB();
        int x = 10;
        int y = 10;
        fontRenderer.drawStringWithShadow(text, x, y, color);
    }
}
