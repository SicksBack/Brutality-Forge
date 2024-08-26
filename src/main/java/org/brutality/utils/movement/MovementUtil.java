package org.brutality.utils.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class MovementUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving() {
        EntityPlayer player = mc.thePlayer;
        return player.moveForward != 0 || player.moveStrafing != 0;
    }

    public static void strafe(double x, double z) {
        if (!isMoving()) return;

        EntityPlayer player = mc.thePlayer;
        float yaw = player.rotationYaw;

        if (player.moveForward < 0) yaw += 180;
        if (player.moveStrafing > 0) yaw -= 90 * (player.moveForward > 0 ? 0.5F : player.moveForward < 0 ? -0.5F : 1);
        if (player.moveStrafing < 0) yaw += 90 * (player.moveForward > 0 ? 0.5F : player.moveForward < 0 ? -0.5F : 1);

        double rad = Math.toRadians(yaw);
        player.motionX = -Math.sin(rad) * x;
        player.motionZ = Math.cos(rad) * z;
    }

    public static double getPlayerDirection() {
        EntityPlayer player = mc.thePlayer;
        float rotationYaw = player.rotationYaw;

        if (player.moveForward < 0) rotationYaw += 180;
        float forward = player.moveForward < 0 ? -0.5F : player.moveForward > 0 ? 0.5F : 1;
        if (player.moveStrafing > 0) rotationYaw -= 90 * forward;
        if (player.moveStrafing < 0) rotationYaw += 90 * forward;

        return Math.toRadians(rotationYaw);
    }
}
