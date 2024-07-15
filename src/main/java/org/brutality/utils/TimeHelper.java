package org.brutality.utils;

public class TimeHelper {

    private long lastMS = getCurrentMS();

    public long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public long getLastMS() {
        return lastMS;
    }

    public boolean hasReached(long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

    public void setLastMS(long lastMS) {
        this.lastMS = lastMS;
    }

    public long getTimePassed() {
        return getCurrentMS() - lastMS;
    }

    public boolean hasTimePassed(long milliseconds) {
        return getTimePassed() >= milliseconds;
    }
}