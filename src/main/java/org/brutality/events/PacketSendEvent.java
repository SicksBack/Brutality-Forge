package org.brutality.events;

import net.minecraft.network.Packet;

public class PacketSendEvent extends Event<Event> {
    private final Packet<?> packet;

    public PacketSendEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
