package org.brutality.events.listeners;

import org.brutality.events.Event;

public class EventMotion extends Event {

    private float yaw;
    private float pitch;
    private boolean pre;

    public EventMotion(float yaw, float pitch, boolean pre) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.pre = pre;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isPre() {
        return pre;
    }

    public void setPre(boolean pre) {
        this.pre = pre;
    }

    public boolean isPost() {
        return !pre;
    }
}
