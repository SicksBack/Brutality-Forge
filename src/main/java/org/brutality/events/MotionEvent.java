package org.brutality.events;

public class MotionEvent {

    private boolean onGround;
    private double y;

    public MotionEvent(boolean onGround, double y) {
        this.onGround = onGround;
        this.y = y;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
