package org.brutality.events.listeners;

import org.brutality.events.Event;
import net.minecraft.network.Packet;

public class EventPacketSend extends Event {
    private final Packet<?> packet;

    public EventPacketSend(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
