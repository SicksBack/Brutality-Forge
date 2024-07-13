package org.brutality.utils;

public class TimeHelper {
    private long lastMS = System.currentTimeMillis();

    public boolean hasReached(long milliseconds) {
        return System.currentTimeMillis() - lastMS >= milliseconds;
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
    }
}
