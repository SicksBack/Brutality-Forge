package org.brutality.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SendPacketEvent extends Event {
    private final Packet<?> packet;
    private boolean canceled;

    public SendPacketEvent(Packet<?> packet) {
        this.packet = packet;
        this.canceled = false;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
