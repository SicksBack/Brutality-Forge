package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isValidTarget(Entity entity) {
        if (entity instanceof EntityPlayer && entity == mc.thePlayer) return false;
        if (!(entity instanceof EntityLivingBase)) return false;
        return entity.isEntityAlive();
    }
}
