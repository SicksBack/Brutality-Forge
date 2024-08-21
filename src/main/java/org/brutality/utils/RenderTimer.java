package org.brutality.utils;

public class RenderTimer {
    private long lastUpdate;
    private long time;

    public RenderTimer() {
        this.lastUpdate = System.currentTimeMillis();
        this.time = 0L;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        this.time = currentTime - this.lastUpdate;
        this.lastUpdate = currentTime;
    }

    public float getRenderPartialTicks() {
        return this.time / 50.0F;
    }
}
