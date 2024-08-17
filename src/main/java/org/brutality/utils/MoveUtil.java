package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;

public class MoveUtil {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isMoving() {
        if (mc.thePlayer == null) {
            return false;
        }
        MovementInput movementInput = mc.thePlayer.movementInput;
        return movementInput.moveForward != 0 || movementInput.moveStrafe != 0;
    }

    public static void strafe(double speed) {
        if (mc.thePlayer == null) {
            return;
        }
        MovementInput movementInput = mc.thePlayer.movementInput;
        float yaw = mc.thePlayer.rotationYaw;
        double forward = movementInput.moveForward;
        double strafe = movementInput.moveStrafe;

        if (forward == 0 && strafe == 0) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0) {
                if (strafe > 0) {
                    yaw += (forward > 0 ? -45 : 45);
                } else if (strafe < 0) {
                    yaw += (forward > 0 ? 45 : -45);
                }
                strafe = 0;
                forward = forward > 0 ? 1 : (forward < 0 ? -1 : 0);
            }
            double radians = Math.toRadians(yaw + 90.0);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
            mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
        }
    }

    public static void stop() {
        if (mc.thePlayer == null) {
            return;
        }
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }
}
