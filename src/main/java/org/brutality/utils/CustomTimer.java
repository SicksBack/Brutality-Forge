package org.brutality.utils;

public class CustomTimer {
    private long lastTime;
    private double deltaTime;
    private double partialTicks;

    public CustomTimer() {
        lastTime = System.currentTimeMillis();
        deltaTime = 0;
        partialTicks = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastTime) / 1000.0;
        lastTime = currentTime;
    }

    public double getPartialTicks() {
        return partialTicks;
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public void setPartialTicks(double partialTicks) {
        this.partialTicks = partialTicks;
    }
}
