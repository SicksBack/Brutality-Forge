package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PreMotionEvent extends Event {
    private boolean onGround;

    public PreMotionEvent(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
