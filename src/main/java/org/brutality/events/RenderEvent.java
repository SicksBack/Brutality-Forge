package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEvent extends Event {
    private final float partialTicks;

    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
