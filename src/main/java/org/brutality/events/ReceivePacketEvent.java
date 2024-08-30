package org.brutality.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class ReceivePacketEvent extends Event {

    private final Packet<?> packet;
    private boolean cancelled;

    // Constructor
    public ReceivePacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    // Getter for the packet
    public Packet<?> getPacket() {
        return packet;
    }

    @Override
    public boolean isCanceled() {
        return cancelled;
    }

    @Override
    public void setCanceled(boolean canceled) {

    }

    public void setCancelled(boolean b) {
    }
}
