package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class PartialRenderTickEvent extends Event {
    private final float partialTicks;

    public PartialRenderTickEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
