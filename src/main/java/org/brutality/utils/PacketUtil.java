package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

public class PacketUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null && mc.thePlayer.sendQueue != null) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
    }
}
