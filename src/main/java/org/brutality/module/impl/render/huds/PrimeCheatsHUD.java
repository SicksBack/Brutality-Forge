package org.brutality.module.impl.render.huds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;

public class PrimeCheatsHUD {
    private final Minecraft mc;
    private final CustomFontRenderer smallFontRenderer;
    private final CustomFontRenderer bigFontRenderer;
    private final SimpleModeSetting modeSetting;
    private final List<Color> colors;
    private int posX, posY;
    private int boxWidth, boxHeight;
    private boolean dragging;
    private int dragX, dragY;

    public PrimeCheatsHUD(Minecraft mc, CustomFontRenderer smallFontRenderer, CustomFontRenderer bigFontRenderer,
                          SimpleModeSetting modeSetting, List<Color> colors,
                          int posX, int posY, int boxWidth, int boxHeight) {
        this.mc = mc;
        this.smallFontRenderer = smallFontRenderer;
        this.bigFontRenderer = bigFontRenderer;
        this.modeSetting = modeSetting;
        this.colors = colors;
        this.posX = posX;
        this.posY = posY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void setBoxDimensions(int width, int height) {
        this.boxWidth = width;
        this.boxHeight = height;
    }

    public void render(RenderGameOverlayEvent.Text event, List<Module> modules) {
        ScaledResolution sr = new ScaledResolution(mc);

        // Darken the grey box background
        Gui.drawRect(posX, posY, posX + boxWidth, posY + boxHeight, new Color(30, 30, 30, 220).getRGB());

        // Position the "HUD" text at (x = 903, y = 2)
        String hudText = "HUD";
        mc.fontRendererObj.drawStringWithShadow(hudText, 903, 2, Color.WHITE.getRGB());

        // Calculate module list height with spacing
        int moduleSpacing = 5; // Space between modules
        int moduleHeight = (int) (smallFontRenderer.getFontHeight() * 0.40); // 40% size of original font
        int modulesHeight = (int) ((modules.size() * moduleHeight) + (modules.size() - 1) * moduleSpacing);

        // Extend the background height
        int extendedHeight = modulesHeight + 20; // Slightly more space below the last module for a seamless look

        // Draw transparent background for modules
        Gui.drawRect(posX, posY + boxHeight, posX + boxWidth, posY + boxHeight + extendedHeight, new Color(0, 0, 0, 100).getRGB());

        // Adjust module list X position by shifting it 5 pixels to the right
        int adjustedPosX = posX + 5;

        // Render each module with reduced font size and spacing
        int offsetY = posY + boxHeight + moduleSpacing; // Start below the box with space

        for (Module module : modules) {
            if (module.isToggled()) {
                String moduleName = module.getName();
                int colorIndex = modules.indexOf(module) % colors.size();
                Color color = colors.get(colorIndex);

                // Draw the module name with reduced font size
                String moduleText = moduleName;
                int moduleTextWidth = smallFontRenderer.getStringWidth(moduleText);
                int moduleTextHeight = (int) (smallFontRenderer.getFontHeight() * 0.40); // 40% size

                // Draw text with shadow
                smallFontRenderer.drawStringWithShadow(moduleText, adjustedPosX + 2, offsetY, color.getRGB());

                // Update offset for next module with spacing
                offsetY += moduleTextHeight + moduleSpacing; // Move down for next module
            }
        }
    }

    public void handleDragging() {
        ScaledResolution sr = new ScaledResolution(mc); // Initialize ScaledResolution here

        if (Mouse.isButtonDown(0)) { // Left mouse button
            if (dragging) {
                posX = Mouse.getX() * mc.displayWidth / sr.getScaledWidth() - dragX;
                posY = mc.displayHeight - Mouse.getY() * mc.displayHeight / sr.getScaledHeight() - dragY;
            }
        } else {
            dragging = false;
        }
    }
}
