package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

public class KillAuraBoxUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawTargetBox(Entity entity, Color color, float partialTicks) {
        if (entity == null || mc.getRenderManager() == null) return;

        RenderManager renderManager = mc.getRenderManager();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderManager.viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderManager.viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderManager.viewerPosZ;
        double width = entity.width / 2.0;
        double height = entity.height;

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        GL11.glLineWidth(2.0F);  // Use GL11 to set the line width

        GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.5F);

        GL11.glBegin(GL11.GL_LINES);
        // Draw the box around the entity
        drawBoundingBox(x - width, y, z - width, x + width, y + height, z + width);
        GL11.glEnd();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private static void drawBoundingBox(double x, double y, double z, double x1, double y1, double z1) {
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x, y, z1);

        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x, y1, z1);
    }
}
