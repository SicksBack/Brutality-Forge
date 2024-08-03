package org.brutality.events;

public interface EventListener<T extends Event> {
    void onEvent(T event);
}
