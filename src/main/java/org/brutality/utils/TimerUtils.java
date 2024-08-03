package org.brutality.utils;

public class TimerUtils {
    private long lastMS = System.currentTimeMillis();

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public boolean hasReached(long milliseconds) {
        return getElapsedTime() >= milliseconds;
    }
}
