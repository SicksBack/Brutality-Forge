package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotations(EntityLivingBase target) {
        double diffX = target.posX - mc.thePlayer.posX;
        double diffY = target.posY + (target.height / 2.0F) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = target.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
        return new float[]{
                mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
        };
    }

    public static void setRotations(float yaw, float pitch) {
        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;
    }

    public static double getAngleChange(EntityLivingBase entity) {
        float[] rotations = getRotations(entity);
        float yaw = rotations[0];
        float pitch = rotations[1];
        float playerYaw = mc.thePlayer.rotationYaw;
        float playerPitch = mc.thePlayer.rotationPitch;
        float yawChange = MathHelper.wrapAngleTo180_float(yaw - playerYaw);
        float pitchChange = MathHelper.wrapAngleTo180_float(pitch - playerPitch);
        return Math.sqrt(yawChange * yawChange + pitchChange * pitchChange);
    }
}
