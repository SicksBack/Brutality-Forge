package org.brutality.events.listeners;

import org.brutality.events.Event;

public class EventKey extends Event<Event> {
    private final int key;

    public EventKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
