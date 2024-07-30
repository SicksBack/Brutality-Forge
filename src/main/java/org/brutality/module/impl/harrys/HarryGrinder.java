package org.brutality.module.impl.harrys;

import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.events.EventTarget;

public class HarryGrinder extends Module {

    public HarryGrinder() {
        super("HarryGrinder", "Moves forward continuously to a specific point", Category.HARRYS);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {

        double targetX = 0;
        double targetY = 80;
        double targetZ = 0;

        double deltaX = targetX - mc.thePlayer.posX;
        double deltaZ = targetZ - mc.thePlayer.posZ;
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (distance > 1) {
            double speed = 0.2; // Adjust the speed as needed
            mc.thePlayer.motionX = (deltaX / distance) * speed;
            mc.thePlayer.motionZ = (deltaZ / distance) * speed;
        } else {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        }
    }
}
