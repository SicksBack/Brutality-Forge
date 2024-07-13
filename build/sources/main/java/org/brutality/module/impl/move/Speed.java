package org.brutality.module.impl.move;

import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import net.minecraft.client.Minecraft;

public class Speed extends Module {

    private final NumberSetting speed;
    private final Minecraft mc = Minecraft.getMinecraft();

    public Speed() {
        super("Speed", "Increases your movement speed", Category.MOVEMENT);
        speed = new NumberSetting("Speed", this, 2, 1, 2, 1);

        addSettings(speed);
    }

    public void onUpdate() {
        double csp = getHorizontalSpeed();
        if (!(csp == 0.0 || !mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying || mc.thePlayer.hurtTime == mc.thePlayer.maxHurtResistantTime && mc.thePlayer.maxHurtResistantTime > 0 || isJumpDown())) {
            double val = speed.getValue() - (speed.getValue() - 1.0) * 0.5;
            setSpeed(csp * val, true);
        }
    }

    private double getHorizontalSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    private boolean isJumpDown() {
        return mc.gameSettings.keyBindJump.isKeyDown();
    }

    private void setSpeed(double speed, boolean setY) {
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double sin = Math.sin(yaw);
            double cos = Math.cos(yaw);
            mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
            mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
        }
        if (setY) {
            mc.thePlayer.motionY = 0.0;
        }
    }
}
