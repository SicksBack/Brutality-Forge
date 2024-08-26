package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class PacketUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Sends a packet to the server without triggering any additional events.
     *
     * @param packet The packet to be sent.
     */
    public static void sendPacketNoEvent(Packet<?> packet) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return; // Ensure we are in a valid state.
        }

        // Send the packet directly to the server.
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    /**
     * Example method to send a player position packet.
     */
    public static void sendPlayerPositionPacket(double x, double y, double z, boolean onGround) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return; // Ensure we are in a valid state.
        }

        Packet<?> packet = new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, onGround);
        sendPacketNoEvent(packet);
    }
}
