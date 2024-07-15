package org.brutality.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class EventBase extends Event {
    private boolean canceled;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
