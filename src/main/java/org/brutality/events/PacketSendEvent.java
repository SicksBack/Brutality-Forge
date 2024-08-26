package org.brutality.events;

import net.minecraft.network.Packet;
import org.brutality.events.Event;

public class PacketSendEvent extends Event {
    private final Packet packet;
    private boolean cancelled;

    public PacketSendEvent(Packet packet) {
        this.packet = packet;
        this.cancelled = false;
    }

    public Packet getPacket() {
        return packet;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
