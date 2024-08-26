package org.brutality.events;

import org.brutality.events.Event;

public class ItemRenderEvent extends Event {
    private boolean renderBlocking;

    public ItemRenderEvent(boolean renderBlocking) {
        this.renderBlocking = renderBlocking;
    }

    public boolean isRenderBlocking() {
        return renderBlocking;
    }

    public void setRenderBlocking(boolean renderBlocking) {
        this.renderBlocking = renderBlocking;
    }
}
