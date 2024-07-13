package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventRenderGUI extends Event {
    private final float partialTicks;

    public EventRenderGUI(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
