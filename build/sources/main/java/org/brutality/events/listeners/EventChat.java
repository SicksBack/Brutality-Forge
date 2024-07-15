package org.brutality.events.listeners;

import org.brutality.events.Event;

public class EventChat extends Event<EventChat> {
    private String message;

    public EventChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
