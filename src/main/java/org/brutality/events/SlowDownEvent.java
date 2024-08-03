package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class SlowDownEvent extends Event {
    private float strafeSpeed;
    private float forwardSpeed;

    public SlowDownEvent(float strafeSpeed, float forwardSpeed) {
        this.strafeSpeed = strafeSpeed;
        this.forwardSpeed = forwardSpeed;
    }

    public float getStrafeSpeed() {
        return strafeSpeed;
    }

    public void setStrafeSpeed(float strafeSpeed) {
        this.strafeSpeed = strafeSpeed;
    }

    public float getForwardSpeed() {
        return forwardSpeed;
    }

    public void setForwardSpeed(float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }
}
