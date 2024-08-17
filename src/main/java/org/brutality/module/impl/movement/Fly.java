package org.brutality.module.impl.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.RandomUtils;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.utils.MoveUtil;
import org.brutality.utils.interfaces.MC;

public class Fly extends Module implements MC {
    public static NumberSetting horizontalSpeed;
    private NumberSetting verticalSpeed;
    private SimpleModeSetting mode; // Use SimpleModeSetting for mode selection

    public Fly() {
        super("Fly", "Allows the player to fly.", Category.MOVEMENT);

        mode = new SimpleModeSetting("Mode", this, "Creative", new String[]{"Creative"}); // Only Creative mode for now
        horizontalSpeed = new NumberSetting("Horizontal Speed", this, 2.0, 1.0, 10.0, 1);
        verticalSpeed = new NumberSetting("Vertical Speed", this, 2.0, 1.0, 10.0, 1);


        // Add settings to the module
        addSettings(horizontalSpeed, verticalSpeed, mode);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.thePlayer.capabilities.allowFlying = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.thePlayer.capabilities.allowFlying = false;
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.capabilities.setFlySpeed(0.05f);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (!this.isToggled() || event.phase != TickEvent.Phase.END || mc.thePlayer == null) {
            return;
        }

        switch (mode.getValue()) {
            case "Creative":
                mc.thePlayer.motionY = 0.0;
                mc.thePlayer.capabilities.setFlySpeed((float) (0.05 * horizontalSpeed.getValue()));
                mc.thePlayer.capabilities.isFlying = true;
                break;
        }
    }

    public static void setSpeed(double speed) {
        if (speed == 0.0) {
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.motionX = 0.0;
            return;
        }
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer.motionZ = 0.0;
            mc.thePlayer.motionX = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double radians = Math.toRadians(yaw + 90.0f);
            double sin = Math.sin(radians);
            double cos = Math.cos(radians);
            mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin;
            mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos;
        }
    }
}
