package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class RenderTickEvent extends Event {
    public RenderTickEvent() {}

    @Override
    public EventPriority getPhase() {
        return EventPriority.NORMAL; // or whichever priority is appropriate
    }
}
