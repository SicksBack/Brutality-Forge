package org.brutality.utils.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.brutality.utils.interfaces.MC;
import org.lwjgl.opengl.GL11;

import javax.swing.text.html.parser.Entity;
import java.awt.*;

public class RenderUtil implements MC {
    public static void drawColoredCircle(double x, double y, double radius, float brightness) {
        GL11.glPushMatrix();
        GL11.glLineWidth(3.5F);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glBegin(3);

        for(int i = 0; i < 360; ++i) {
            color(Color.HSBtoRGB(1.0F, 0.0F, brightness));
            GL11.glVertex2d(x, y);
            color(Color.HSBtoRGB((float)i / 360.0F, 1.0F, brightness));
            GL11.glVertex2d(x + Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius);
        }

        GL11.glEnd();
        GL11.glShadeModel(7424);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }

    public static void drawCircle(double x, double y, double radius, int color) {
        GL11.glPushMatrix();
        color(color);
        GL11.glBegin(9);

        for(int i = 0; i < 360; ++i) {
            GL11.glVertex2d(x + Math.sin(Math.toRadians(i)) * radius, y + Math.cos(Math.toRadians(i)) * radius);
        }

        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void color(int argb) {
        float alpha = (float)(argb >> 24 & 0xFF) / 255.0F;
        float red = (float)(argb >> 16 & 0xFF) / 255.0F;
        float green = (float)(argb >> 8 & 0xFF) / 255.0F;
        float blue = (float)(argb & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void color(Color color) {
        color(color.getRGB());
    }

    public static void drawRoundedRect(float startX, float startY, float width, float height, float radius, int color) {
        float endX = startX + width;
        float endY = startY + height;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0F;
        float red = (float)(color >> 16 & 0xFF) / 255.0F;
        float green = (float)(color >> 8 & 0xFF) / 255.0F;
        float blue = (float)(color & 0xFF) / 255.0F;
        float z = 0.0F;
        if (startX > endX) {
            z = startX;
            startX = endX;
            endX = z;
        }

        if (startY > endY) {
            z = startY;
            startY = endY;
            endY = z;
        }

        double x1 = startX + radius;
        double y1 = startY + radius;
        double x2 = endX - radius;
        double y2 = endY - radius;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glLineWidth(1.0F);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glBegin(9);
        double degree = Math.PI / 180.0;

        for(double i = 0.0; i <= 90.0; ++i) {
            GL11.glVertex2d(x2 + Math.sin(i * degree) * (double)radius, y2 + Math.cos(i * degree) * (double)radius);
        }

        for(double i = 90.0; i <= 180.0; ++i) {
            GL11.glVertex2d(x2 + Math.sin(i * degree) * (double)radius, y1 + Math.cos(i * degree) * (double)radius);
        }

        for(double i = 180.0; i <= 270.0; ++i) {
            GL11.glVertex2d(x1 + Math.sin(i * degree) * (double)radius, y1 + Math.cos(i * degree) * (double)radius);
        }

        for(double i = 270.0; i <= 360.0; ++i) {
            GL11.glVertex2d(x1 + Math.sin(i * degree) * (double)radius, y2 + Math.cos(i * degree) * (double)radius);
        }

        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glDisable(2848);
        GlStateManager.popMatrix();
    }

    public static void drawRect(int x, int y, int width, int height, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
    }

    /**
     * This is just so i dont have to do some whack casting
     */
    public static void drawRect(float x, float y, float width, float height, int color) {
        Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), color);
    }

    /**
     * This is just so i dont have to do some whack casting
     */
    public static void drawAbsoluteRect(float x, float y, float x2, float y2, int color) {
        Gui.drawRect((int) x, (int) y, (int) x2, (int) y2, color);
    }

    public static void drawBorder(float left, float top, float right, float bottom, float borderWidth, int borderColor, boolean borderIncludedInBounds) {
        float adjustedLeft = left;
        float adjustedTop = top;
        float adjustedRight = right;
        float adjustedBottom = bottom;
        if (!borderIncludedInBounds) {
            adjustedLeft = left - borderWidth;
            adjustedTop = top - borderWidth;
            adjustedRight = right + borderWidth;
            adjustedBottom = bottom + borderWidth;
        }

        drawAbsoluteRect(adjustedLeft, adjustedTop, adjustedRight, adjustedTop + borderWidth, borderColor);
        drawAbsoluteRect(adjustedLeft, adjustedBottom - borderWidth, adjustedRight, adjustedBottom, borderColor);
        drawAbsoluteRect(
                adjustedLeft, adjustedTop + borderWidth, adjustedLeft + borderWidth, adjustedBottom - borderWidth, borderColor
        );
        drawAbsoluteRect(
                adjustedRight - borderWidth, adjustedTop + borderWidth, adjustedRight, adjustedBottom - borderWidth, borderColor
        );
    }

    public static void drawBorder(double left, double top, double right, double bottom, double borderWidth, int borderColor, boolean borderIncludedInBounds) {
        drawBorder((float)left, (float)top, (float)right, (float)bottom, (float)borderWidth, borderColor, borderIncludedInBounds);
    }

    public static void startScissorBox() {
        GL11.glPushMatrix();
        GL11.glEnable(3089);
    }

    public static void drawScissorBox(double x, double y, double width, double height) {
        width = Math.max(width, 0.1);
        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();
        y = (double)sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
    }

    public static void drawScissorBox(double x, double y, double width, double height, double scale) {
        width = Math.max(width, 0.1);
        ScaledResolution sr = new ScaledResolution(mc);
        y = (double)sr.getScaledHeight() - y;
        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;
        GL11.glScissor((int)x, (int)(y - height), (int)width, (int)height);
    }

    public static void endScissorBox() {
        GL11.glDisable(3089);
        GL11.glPopMatrix();
    }
}
