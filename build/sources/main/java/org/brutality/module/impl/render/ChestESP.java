package org.brutality.module.impl.render;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.events.listeners.EventRenderWorld;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.render.RenderUtil;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;

public class ChestESP extends Module {

    public ChestESP() {
        super("ChestESP", "Highlights chests in the world", Category.RENDER);
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        EventRenderWorld renderEvent = new EventRenderWorld(event.partialTicks);
        for (TileEntity tileEntity : Minecraft.getMinecraft().theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest) {
                double x = tileEntity.getPos().getX() - Minecraft.getMinecraft().getRenderManager().viewerPosX;
                double y = tileEntity.getPos().getY() - Minecraft.getMinecraft().getRenderManager().viewerPosY;
                double z = tileEntity.getPos().getZ() - Minecraft.getMinecraft().getRenderManager().viewerPosZ;

                drawChestESP(x, y, z);
                drawTracerToChest(x, y, z, renderEvent);
                drawOutlineAroundChest(x, y, z);
            }
        }
    }

    private void drawChestESP(double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        RenderUtil.drawBox(x, y, z, x + 1, y + 1, z + 1, 0.0f, 1.0f, 0.0f, 0.5f); // Drawing the box

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawTracerToChest(double x, double y, double z, EventRenderWorld event) {
        double playerX = Minecraft.getMinecraft().thePlayer.getPositionEyes(event.getPartialTicks()).xCoord - Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double playerY = Minecraft.getMinecraft().thePlayer.getPositionEyes(event.getPartialTicks()).yCoord - Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double playerZ = Minecraft.getMinecraft().thePlayer.getPositionEyes(event.getPartialTicks()).zCoord - Minecraft.getMinecraft().getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glLineWidth(1.5F);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(0.0f, 1.0f, 0.0f, 0.5f); // Set color to green with transparency
        GL11.glVertex3d(playerX, playerY, playerZ); // Start at the player's eye position
        GL11.glVertex3d(x, y, z); // End at the chest's position
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void drawOutlineAroundChest(double x, double y, double z) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);

        GL11.glLineWidth(2.0F);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f); // Set color to white with transparency

        GL11.glBegin(GL11.GL_LINES);
        // Draw outline edges
        RenderUtil.drawBoxOutline(x, y, z, x + 1, y + 1, z + 1);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
