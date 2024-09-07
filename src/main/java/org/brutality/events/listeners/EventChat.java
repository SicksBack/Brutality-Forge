package org.brutality.events.listeners;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventChat extends Event {
    private boolean cancelled = false;
private String message;

    public EventChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
