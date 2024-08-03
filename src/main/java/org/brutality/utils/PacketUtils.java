package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public class PacketUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendBlockPlacement() {
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }
}
