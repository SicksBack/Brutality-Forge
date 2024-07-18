package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.stream.Collectors;

public class ArrayListModule extends Module {
    public final ColorSetting colorSetting = new ColorSetting("Color", this, Color.WHITE);
    public final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    public final NumberSetting spacingSetting = new NumberSetting("Spacing", this, 1, 1, 10, 1);

    private int posX = 10, posY = 10;
    private int dragX, dragY;
    private boolean dragging = false;
    private CustomFontRenderer fontRenderer;
    private CustomFontRenderer bigFontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();

    public ArrayListModule() {
        super("ArrayListModule", "Shows a list of toggled modules", Category.RENDER);
        setKey(Keyboard.KEY_P);
        fontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
        bigFontRenderer = new CustomFontRenderer(new Font("Arial", Font.BOLD, 24), true, true);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        int color = colorSetting.getColor().getRGB();
        int offsetY = posY + 30; // Adjusted to accommodate image height
        int spacing = (int) spacingSetting.getValue(); // Retrieve spacing value

        // Handle dragging
        if (mc.currentScreen != null) {
            handleDragging();
        }

        // Render the "VAPEV4" text with gradient effect
        drawGradientText("VAPEV4", posX, posY);

        int y = offsetY;
        for (Module m : mm.stream()
                .sorted((o1, o2) -> Integer.compare(fontRenderer.getStringWidth(o2.getName()), fontRenderer.getStringWidth(o1.getName())))
                .collect(Collectors.toList())) {
            if (m.isToggled()) {
                if (backgroundSetting.isEnabled()) {
                    Gui.drawRect((int)(posX - 2), (int)(y - 2), (int)(posX + fontRenderer.getStringWidth(m.getName()) + 2), (int)(y + fontRenderer.getFontHeight() + 2), new Color(0, 0, 0, 120).getRGB());
                }
                fontRenderer.drawStringWithShadow(m.getName(), posX, y, color);
                y += fontRenderer.getFontHeight() + spacing;
            }
        }
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - posX;
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - posY;
            }
            if (dragging) {
                posX = Mouse.getX() / 2 - dragX;
                posY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY;
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= posX && mouseX <= posX + 100 && mouseY >= posY && mouseY <= posY + 20;
    }

    private void drawGradientText(String text, int x, int y) {
        // Simulating the gradient effect using multiple draw calls with varying colors
        int startColor = colorSetting.getColor().brighter().getRGB();
        int endColor = colorSetting.getColor().darker().getRGB();
        int textWidth = bigFontRenderer.getStringWidth(text);
        int gradientWidth = textWidth / text.length();

        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / (text.length() - 1);
            int color = blendColors(startColor, endColor, ratio);
            bigFontRenderer.drawStringWithShadow(String.valueOf(text.charAt(i)), x + i * gradientWidth, y, color);
        }
    }

    private int blendColors(int color1, int color2, float ratio) {
        int r = (int) ((Color.decode(String.valueOf(color2)).getRed() - Color.decode(String.valueOf(color1)).getRed()) * ratio + Color.decode(String.valueOf(color1)).getRed());
        int g = (int) ((Color.decode(String.valueOf(color2)).getGreen() - Color.decode(String.valueOf(color1)).getGreen()) * ratio + Color.decode(String.valueOf(color1)).getGreen());
        int b = (int) ((Color.decode(String.valueOf(color2)).getBlue() - Color.decode(String.valueOf(color1)).getBlue()) * ratio + Color.decode(String.valueOf(color1)).getBlue());
        return new Color(r, g, b).getRGB();
    }
}
