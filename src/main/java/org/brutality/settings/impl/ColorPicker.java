package org.brutality.settings.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import org.brutality.utils.interfaces.MC;
import org.brutality.utils.render.RenderUtil;

import java.awt.*;
import java.util.function.Consumer;

@Getter
@Setter
public class ColorPicker implements MC {
    public int x;
    public int y;
    private String currentColorHexInputString = "";
    private final double radius;
    private final Consumer<Color> color;
    private double selectedX;
    private double selectedY;
    private float brightness;
    private float alpha;

    public ColorPicker(double radius, Color selectedColor, Consumer<Color> color) {
        this.radius = radius;
        this.alpha = (float)selectedColor.getAlpha() / 255.0F;
        this.brightness = Color.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null)[2];
        this.setColor(selectedColor);
        this.color = color;
    }

    public void click(double mouseX, double mouseY, int button) {
        if (button == 0 && this.isPointInCircle(this.x, this.y, this.radius, mouseX, mouseY)) {
            this.selectedX = mouseX - this.x;
            this.selectedY = mouseY - this.y;
            this.currentColorHexInputString = getStringRepresentation(this.getColor());
        }
    }

    public Color stringToColor(String colorString) {
        try {
            long colorValue = Long.parseLong(colorString, 16);
            if (colorString.length() == 8) {
                return new Color((int) colorValue, true);
            }
        } catch (NumberFormatException e) {
        }
        return this.getColor();
    }

    public void draw() {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderUtil.drawColoredCircle(this.x, this.y, this.radius, this.getBrightness());
        RenderUtil.drawCircle(this.x + this.selectedX, this.y + this.selectedY, 2.5, -15592942);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        this.color.accept(this.getColor());
    }

    public static String getStringRepresentation(Color color) {
        return String.format("%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }


    private float getNormalized() {
        return (float)((-Math.toDegrees(Math.atan2(this.selectedY, this.selectedX)) + 450.0) % 360.0) / 360.0F;
    }

    private Color getColor() {
        Color color1 = Color.getHSBColor(this.getNormalized(), (float)(Math.hypot(this.selectedX, this.selectedY) / this.radius), this.getBrightness());
        return new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), (int)(this.getAlpha() * 255.0F));
    }

    public void setColor(Color selectedColor) {
        float[] hsb = Color.RGBtoHSB(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), null);
        this.selectedX = (double)hsb[1] * this.radius * (Math.sin(Math.toRadians(hsb[0] * 360.0F)) / Math.sin(Math.toRadians(90.0)));
        this.selectedY = (double)hsb[1] * this.radius * (Math.sin(Math.toRadians(90.0F - hsb[0] * 360.0F)) / Math.sin(Math.toRadians(90.0)));
    }

    private boolean isPointInCircle(double x, double y, double radius, double pX, double pY) {
        return (pX - x) * (pX - x) + (pY - y) * (pY - y) <= radius * radius;
    }

    public boolean mouseOver(double mouseX, double mouseY, double posX, double posY, double width, double height) {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height;
    }
}
