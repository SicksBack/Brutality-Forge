package org.brutality.events.listeners;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventRenderWorld extends Event {
    private final float partialTicks;

    public EventRenderWorld(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
