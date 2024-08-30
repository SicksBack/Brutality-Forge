package org.brutality.events;

import org.brutality.events.Event;

public class TickEvent extends Event {
    public enum Phase {
        START, END
    }

    private final Phase phase;

    public TickEvent(Phase phase) {
        this.phase = phase;
    }

    public Phase getPhase() {
        return phase;
    }
}
