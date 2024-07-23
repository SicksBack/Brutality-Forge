package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class NameTagRenderUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawNametag(EntityPlayer player, double x, double y, double z) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        String displayText = String.format("%s %.1f", player.getName(), player.getHealth() / 2.0F);

        float scale = 0.016666668F * 1.6F;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + player.height + 0.5F, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int width = fontRenderer.getStringWidth(displayText) / 2;
        GlStateManager.disableTexture2D();
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.25F);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(-width - 1, -1, 0.0F);
        GL11.glVertex3f(-width - 1, 8, 0.0F);
        GL11.glVertex3f(width + 1, 8, 0.0F);
        GL11.glVertex3f(width + 1, -1, 0.0F);
        GL11.glEnd();
        GlStateManager.enableTexture2D();

        fontRenderer.drawString(displayText, -width, 0, 553648127);
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        fontRenderer.drawString(displayText, -width, 0, -1);

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
