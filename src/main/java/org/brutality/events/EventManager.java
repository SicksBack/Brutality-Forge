package org.brutality.events;

import java.util.ArrayList;
import java.util.List;

public class EventManager {

    private static final List<Object> registeredListeners = new ArrayList<>();

    public static void register(Object listener) {
        if (!registeredListeners.contains(listener)) {
            registeredListeners.add(listener);
        }
    }

    public static void unregister(Object listener) {
        registeredListeners.remove(listener);
    }

    public static void post(Event<Event> event) {
        for (Object listener : registeredListeners) {
            // Add logic to post the event to the listener
        }
    }
}
