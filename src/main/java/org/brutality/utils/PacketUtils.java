package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;

public class PacketUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void sendBlockPlacement() {
        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
    }

    public static void sendPacketNoEvent(Packet<?> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    public static void sendPacket(Packet<?> packet) {
        mc.thePlayer.sendQueue.addToSendQueue(packet);
    }

    public static void openInventory() {
        sendPacket(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
    }

    public static void closeWindow() {
        sendPacket(new C0DPacketCloseWindow());
    }
}
