package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class BoxColorUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawBoundingBox(double x, double y, double z, double x1, double y1, double z1, Color color) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // Draw the box edges
        drawBoxEdges(worldRenderer, x, y, z, x1, y1, z1, color);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private static void drawBoxEdges(WorldRenderer worldRenderer, double x, double y, double z, double x1, double y1, double z1, Color color) {
        // Set color
        float red = color.getRed() / 255.0F;
        float green = color.getGreen() / 255.0F;
        float blue = color.getBlue() / 255.0F;
        float alpha = color.getAlpha() / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);

        // Begin drawing lines
        worldRenderer.begin(GL11.GL_LINES, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION);

        // Front
        worldRenderer.pos(x, y, z).endVertex();
        worldRenderer.pos(x1, y, z).endVertex();

        worldRenderer.pos(x1, y, z).endVertex();
        worldRenderer.pos(x1, y1, z).endVertex();

        worldRenderer.pos(x1, y1, z).endVertex();
        worldRenderer.pos(x, y1, z).endVertex();

        worldRenderer.pos(x, y1, z).endVertex();
        worldRenderer.pos(x, y, z).endVertex();

        // Back
        worldRenderer.pos(x, y, z1).endVertex();
        worldRenderer.pos(x1, y, z1).endVertex();

        worldRenderer.pos(x1, y, z1).endVertex();
        worldRenderer.pos(x1, y1, z1).endVertex();

        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x, y1, z1).endVertex();

        worldRenderer.pos(x, y1, z1).endVertex();
        worldRenderer.pos(x, y, z1).endVertex();

        // Connect front and back
        worldRenderer.pos(x, y, z).endVertex();
        worldRenderer.pos(x, y, z1).endVertex();

        worldRenderer.pos(x1, y, z).endVertex();
        worldRenderer.pos(x1, y, z1).endVertex();

        worldRenderer.pos(x1, y1, z).endVertex();
        worldRenderer.pos(x1, y1, z1).endVertex();

        worldRenderer.pos(x, y1, z).endVertex();
        worldRenderer.pos(x, y1, z1).endVertex();

        Tessellator.getInstance().draw();
    }
}
