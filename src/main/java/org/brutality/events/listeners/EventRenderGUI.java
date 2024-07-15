package org.brutality.events.listeners;

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
