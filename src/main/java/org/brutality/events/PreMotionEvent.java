package org.brutality.events;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

// Define the PreMotionEvent class
public class PreMotionEvent extends Event {
    private final double posX, posY, posZ;
    private final float yaw, pitch;

    public PreMotionEvent(double posX, double posY, double posZ, float yaw, float pitch) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getPosZ() {
        return posZ;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
