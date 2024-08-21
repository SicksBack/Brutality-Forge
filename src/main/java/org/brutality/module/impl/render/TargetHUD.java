package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.module.impl.render.targethuds.DefaultTargetHUD;
import org.brutality.module.impl.render.targethuds.AstolfoTargetHUD;

public class TargetHUD extends Module {

    private final NumberSetting xPos = new NumberSetting("X Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final SimpleModeSetting mode = new SimpleModeSetting("Mode", this, "Default", new String[]{"Default", "Astolfo"});

    private EntityLivingBase target;
    private final DefaultTargetHUD defaultHUD = new DefaultTargetHUD();
    private final AstolfoTargetHUD astolfoHUD = new AstolfoTargetHUD();

    public TargetHUD() {
        super("TargetHUD", "Displays information about your target", Category.RENDER);
        addSettings(xPos, yPos, mode);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Find the closest target
        target = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase livingEntity = (EntityLivingBase) entity;
                if (livingEntity instanceof EntityPlayer && livingEntity != mc.thePlayer && !livingEntity.isDead && livingEntity.getHealth() > 0) {
                    double distance = mc.thePlayer.getDistanceToEntity(livingEntity);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        target = livingEntity;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || target == null) {
            return;
        }

        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            int posX = (int) this.xPos.getValue();
            int posY = (int) this.yPos.getValue();

            // Render the HUD based on the selected mode
            if (mode.getValue().equals("Default")) {
                defaultHUD.render(posX, posY, target);
            } else if (mode.getValue().equals("Astolfo")) {
                astolfoHUD.render(posX, posY, target);
            }
        }
    }
}
