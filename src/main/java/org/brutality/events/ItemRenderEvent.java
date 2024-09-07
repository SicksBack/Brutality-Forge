package org.brutality.events;

public class ItemRenderEvent extends Event<Event> {
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
