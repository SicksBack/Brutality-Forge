package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Module;
import org.brutality.module.Category;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {

    private final BooleanSetting showHealth;
    private final BooleanSetting showDistance;
    private final BooleanSetting showHitsToKill;
    private final BooleanSetting showInvis;
    private final BooleanSetting showArmor;
    private final NumberSetting yOffset;
    private final NumberSetting scale;

    private Minecraft mc = Minecraft.getMinecraft();

    public NameTags() {
        super("NameTags", "Displays custom names above players with various additional information", Category.RENDER);

        // Initialize settings
        showHealth = new BooleanSetting("Show Health", this, true);
        showDistance = new BooleanSetting("Show Distance", this, true);
        showHitsToKill = new BooleanSetting("Show Hits To Kill", this, true);
        showInvis = new BooleanSetting("Show Invisible", this, true);
        showArmor = new BooleanSetting("Show Armor", this, true);
        yOffset = new NumberSetting("YOffset", this, 0, -10, 10, 1);
        scale = new NumberSetting("Scale", this, 1, 1, 10, 1); // Scale range from 1 to 10

        // Add settings to the module
        this.addSettings(showHealth, showDistance, showHitsToKill, showInvis, showArmor, yOffset, scale);
    }

    @SubscribeEvent
    public void onRenderLiving(RenderLivingEvent.Specials.Pre e) {
        if (e.entity instanceof EntityPlayer && e.entity != mc.thePlayer && e.entity.getHealth() > 0) {
            EntityPlayer entityPlayer = (EntityPlayer) e.entity;

            if (!showInvis.isEnabled() && entityPlayer.isInvisible()) {
                return;
            }

            // Cancel default name tag rendering
            e.setCanceled(true);

            // Get custom name from scoreboard objective
            String name = getCustomName(entityPlayer);

            // Add health in hearts without heart symbol
            if (showHealth.isEnabled()) {
                float health = entityPlayer.getHealth();
                float healthInHearts = health / 2.0F; // Convert health to hearts
                String healthText = String.format("%.2f", healthInHearts); // Format with two decimal places

                String healthColor;
                if (healthInHearts > 7) {
                    healthColor = "§a"; // Green
                } else if (healthInHearts >= 4) {
                    healthColor = "§e"; // Yellow
                } else {
                    healthColor = "§c"; // Red
                }

                name = name + " " + healthColor + healthText; // Remove heart symbol
            }

            if (showHitsToKill.isEnabled()) {
                int hitsToKill = MathHelper.floor_float(entityPlayer.getMaxHealth() - entityPlayer.getHealth());
                name = name + " " + hitsToKill + " Hits"; // Add "Hits" label
            }

            if (showDistance.isEnabled()) {
                int distance = MathHelper.floor_float(mc.thePlayer.getDistanceToEntity(entityPlayer));
                String color = "§";
                color = distance <= 8 ? color + "c" : (distance <= 15 ? color + "6" : (distance <= 25 ? color + "e" : ""));
                name = color + distance + "m§r " + name;
            }

            // Render name tag
            GlStateManager.pushMatrix();
            GlStateManager.translate(e.x, e.y + entityPlayer.height + yOffset.getValue() + 0.5, e.z); // Position name tag on top of head
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1, 0, 0);

            double scaleFactor = scale.getValue(); // Use double for scaling factor
            GlStateManager.scale(-0.02666667 * scaleFactor, -0.02666667 * scaleFactor, 0.02666667 * scaleFactor);

            // Draw name tag background
            drawBackground(name);

            // Draw name tag text with proper color
            mc.fontRendererObj.drawString(name, -mc.fontRendererObj.getStringWidth(name) / 2, 0, 0xFFFFFF); // Use default white color for the text

            if (showArmor.isEnabled()) {
                renderArmor(entityPlayer);
            }

            GlStateManager.popMatrix();

            // Re-enable depth testing and lighting
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
        }
    }

    private String getCustomName(EntityPlayer player) {
        // Example of how to get the custom name from the scoreboard objective
        // This method needs to be implemented based on how you manage the scoreboard data
        // For example, using a command or a custom method to retrieve the custom name
        return player.getDisplayName().getFormattedText(); // Placeholder, replace with actual logic
    }

    private void drawBackground(String name) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(GL11.GL_QUADS, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_COLOR);

        int textWidth = mc.fontRendererObj.getStringWidth(name);
        int backgroundWidth = textWidth + 4; // Add padding
        int backgroundHeight = 10; // Adjust height as needed

        worldRenderer.pos(-backgroundWidth / 2.0, -1, 0).color(0, 0, 0, 0.5F).endVertex();
        worldRenderer.pos(backgroundWidth / 2.0, -1, 0).color(0, 0, 0, 0.5F).endVertex();
        worldRenderer.pos(backgroundWidth / 2.0, backgroundHeight - 1, 0).color(0, 0, 0, 0.5F).endVertex();
        worldRenderer.pos(-backgroundWidth / 2.0, backgroundHeight - 1, 0).color(0, 0, 0, 0.5F).endVertex();
        tessellator.draw();
    }

    private void renderArmor(EntityPlayer entityPlayer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0, -0.3, 0.0); // Adjust position for rendering armor

        int pos = 0;
        for (int i = 3; i >= 0; --i) {
            ItemStack stack = entityPlayer.inventory.armorInventory[i];
            if (stack != null) {
                renderItemStack(stack, pos, (int) (-20.0 - yOffset.getValue()));
                pos += 16;
            }
        }

        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int xPos, int yPos) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        mc.getRenderItem().zLevel = -150.0F;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, xPos, yPos);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
}
