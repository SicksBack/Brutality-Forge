package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.events.listeners.EventRenderWorld;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class NameTag extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting scaleSetting;
    private final NumberSetting opacitySetting;

    public NameTag() {
        super("NameTag", "Displays all users' names with additional info", Category.RENDER);
        this.scaleSetting = new NumberSetting("Scale", this, 0.5, 0.1, 1.0, 0);
        this.opacitySetting = new NumberSetting("Opacity", this, 30, 0, 100, 0);
        addSettings(scaleSetting, opacitySetting);
    }

    @SubscribeEvent
    public void onRenderWorld(EventRenderWorld event) {
        for (Object o : mc.theWorld.playerEntities) {
            if (o instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) o;
                if (player == mc.thePlayer || player.isDead) continue;
                double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks() - mc.getRenderManager().viewerPosX;
                double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks() - mc.getRenderManager().viewerPosY;
                double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks() - mc.getRenderManager().viewerPosZ;
                renderNameTag(player, x, y, z, event.getPartialTicks());
            }
        }
    }

    private void renderNameTag(EntityPlayer player, double x, double y, double z, float partialTicks) {
        double scale = scaleSetting.getValue();
        int opacity = (int) opacitySetting.getValue();

        String name = player.getDisplayName().getFormattedText();
        float health = player.getHealth();
        float absorption = player.getAbsorptionAmount();
        float totalHealth = health + absorption;
        String heartsStr = String.format("%.1f", totalHealth / 2.0);

        int healthColor;
        if (totalHealth > 12) {
            healthColor = 0x00FF00;
        } else if (totalHealth >= 6) {
            healthColor = 0xFFFF00;
        } else {
            healthColor = 0xFF0000;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y + player.height + 0.5F, (float)z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        int width = mc.fontRendererObj.getStringWidth(name + " " + heartsStr) / 2;
        int backgroundAlpha = (int) (opacity / 100.0 * 255);
        int backgroundColor = (backgroundAlpha << 24) | 0x000000;

        Gui.drawRect(-width - 2, -mc.fontRendererObj.FONT_HEIGHT - 1, width + 2, 1, backgroundColor);

        mc.fontRendererObj.drawStringWithShadow(name, -width, -mc.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow(heartsStr, width - mc.fontRendererObj.getStringWidth(heartsStr), -mc.fontRendererObj.FONT_HEIGHT, healthColor);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
