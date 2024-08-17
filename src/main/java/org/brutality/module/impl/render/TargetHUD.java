package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class TargetHUD extends Module {

    private final NumberSetting xPos = new NumberSetting("X Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private EntityLivingBase target;

    public TargetHUD() {
        super("TargetHUD", "Displays information about your target", Category.RENDER);
        addSettings(xPos, yPos);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        target = null;

        double closestDistance = Double.MAX_VALUE;
        for (EntityLivingBase entity : mc.theWorld.playerEntities) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer && !entity.isDead && entity.getHealth() > 0) {
                double distance = mc.thePlayer.getDistanceToEntity(entity);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    target = entity;
                }
            }
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging();
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || target == null) {
            return;
        }

        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);

            int posX = (int) this.xPos.getValue();
            int posY = (int) this.yPos.getValue();

            String targetName = target.getName();
            float health = target.getHealth();
            float maxHealth = target.getMaxHealth();
            String healthInfo = String.format("%.1f/%.1f", health, maxHealth);

            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.GOLD + "Target: " + EnumChatFormatting.RESET + targetName, posX, posY, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.RED + "Health: " + EnumChatFormatting.RESET + healthInfo, posX, posY + mc.fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
        }
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - (int) xPos.getValue();
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - (int) yPos.getValue();
            }
            if (dragging) {
                xPos.setValue(Mouse.getX() / 2 - dragX);
                yPos.setValue(new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY);
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 200 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 20;
    }
}
