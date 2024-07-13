package org.brutality.events.listeners;

import org.brutality.events.Event;
import net.minecraft.network.Packet;

public class EventPacketReceive extends Event {
    private Packet<?> packet;

    public EventPacketReceive(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
