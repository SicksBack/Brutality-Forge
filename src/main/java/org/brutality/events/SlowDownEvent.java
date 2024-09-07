package org.brutality.events;

public class SlowdownEvent extends Event<Event> {
    private float forward;
    private float strafe;
    private boolean allowedSprinting;

    public SlowdownEvent(float forward, float strafe, boolean allowedSprinting) {
        this.forward = forward;
        this.strafe = strafe;
        this.allowedSprinting = allowedSprinting;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public boolean isAllowedSprinting() {
        return allowedSprinting;
    }

    public void setAllowedSprinting(boolean allowedSprinting) {
        this.allowedSprinting = allowedSprinting;
    }
}
