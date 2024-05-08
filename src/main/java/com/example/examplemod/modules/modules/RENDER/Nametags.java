package com.example.examplemod.modules.modules.RENDER;

import brutality.client.events.Event;
import brutality.client.events.listeners.EventRender2D;
import brutality.client.modules.Module;
import brutality.client.settings.BooleanSetting;
import brutality.client.settings.Setting;
import brutality.client.utils.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class NameTags extends Module {

    private BooleanSetting showNameTags = new BooleanSetting("Show NameTags", true);

    public NameTags() {
        super("NameTags", "Draws nametags above players", 0, Module.Category.RENDER, false);
        addSettings(showNameTags);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRender2D && showNameTags.isEnabled()) {
            for (Object entityObj : mc.theWorld.loadedEntityList) {
                if (entityObj instanceof EntityOtherPlayerMP) {
                    EntityOtherPlayerMP player = (EntityOtherPlayerMP) entityObj;
                    if (player != mc.thePlayer) {
                        drawNameTag(player);
                    }
                }
            }
        }
    }

    private void drawNameTag(EntityOtherPlayerMP player) {
        double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
        double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
        double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;

        float distance = mc.thePlayer.getDistanceToEntity(player);
        if (distance <= 8) { // Adjust this distance as needed
            RenderUtils.beginGl();
            GlStateManager.pushMatrix();
            GlStateManager.translate(posX, posY + player.height + 0.5, posZ);
            GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int width = mc.fontRendererObj.getStringWidth(player.getName());
            RenderUtils.drawRect(-width / 2 - 2, -1, width / 2 + 2, 9, new Color(0, 0, 0, 128).getRGB());
            mc.fontRendererObj.drawStringWithShadow(player.getName(), -width / 2, 0, -1);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            RenderUtils.endGl();
        }
    }
}
