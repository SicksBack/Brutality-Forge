package org.brutality.utils;

import net.minecraft.client.Minecraft;

public class RotationsUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.thePlayer.posX;
        double deltaY = y - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double deltaZ = z - mc.thePlayer.posZ;
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(deltaY, distance) * 180 / Math.PI);
        return new float[]{yaw, pitch};
    }
}
