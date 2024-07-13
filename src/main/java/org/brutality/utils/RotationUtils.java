package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotations(EntityLivingBase target, float currentYaw, float currentPitch) {
        double diffX = target.posX - mc.thePlayer.posX;
        double diffZ = target.posZ - mc.thePlayer.posZ;
        double diffY = target.posY + target.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0 / Math.PI);
        return new float[]{currentYaw + MathHelper.wrapAngleTo180_float(yaw - currentYaw), currentPitch + MathHelper.wrapAngleTo180_float(pitch - currentPitch)};
    }

    public static double getRotationDifference(EntityLivingBase entity, boolean usePitch) {
        float[] rotations = getRotations(entity, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
        float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(rotations[0] - mc.thePlayer.rotationYaw));
        float pitchDiff = usePitch ? Math.abs(MathHelper.wrapAngleTo180_float(rotations[1] - mc.thePlayer.rotationPitch)) : 0.0f;
        return yawDiff + pitchDiff;
    }
}
