package org.brutality.utils;

public class TimerUtils {
    private long lastMS;

    public TimerUtils() {
        reset();
    }

    public boolean hasReached(long milliseconds) {
        return getTimePassed() >= milliseconds;
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - lastMS;
    }
}
