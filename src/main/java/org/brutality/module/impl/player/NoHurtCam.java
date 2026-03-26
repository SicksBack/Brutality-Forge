package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam", "Removes hurt camera effect", Category.PLAYER);
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.entityLiving == Minecraft.getMinecraft().thePlayer) {
            Minecraft.getMinecraft().thePlayer.hurtTime = 0;
            Minecraft.getMinecraft().thePlayer.maxHurtTime = 0;
            Minecraft.getMinecraft().thePlayer.hurtResistantTime = 0;
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (Minecraft.getMinecraft().thePlayer.hurtTime > 0) {
            event.pitch = 0.0F;
            event.yaw = 0.0F;
            event.roll = 0.0F;
        }
    }
}
