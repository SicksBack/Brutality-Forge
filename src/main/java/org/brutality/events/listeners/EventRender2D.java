package org.brutality.events.listeners;

import org.brutality.events.Event;

public class EventRender2D extends Event {
    private final float partialTicks;

    public EventRender2D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
