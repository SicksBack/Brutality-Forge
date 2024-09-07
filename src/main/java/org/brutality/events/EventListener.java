package org.brutality.events;

public interface EventListener<T extends Event<Event>> {
    void onEvent(T event);
}
