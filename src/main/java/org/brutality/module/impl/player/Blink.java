package org.brutality.module.impl.player;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.events.SendPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.PacketUtils;
import org.brutality.utils.Utils;

public class Blink extends Module {
    private List<Packet<?>> blinkedPackets = new ArrayList<>();
    private Vec3 pos;

    public Blink() {
        super("Blink", "Chinks Can't Blink - Sick 26/08/24", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        this.blinkedPackets.clear();
        Minecraft mc = Minecraft.getMinecraft();
        this.pos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @Override
    public void onDisable() {
        for (Packet<?> packet : this.blinkedPackets) {
            PacketUtils.sendPacketNoEvent(packet);
        }
        this.blinkedPackets.clear();
        this.pos = null;
    }

    @SubscribeEvent
    public void onSendPacket(SendPacketEvent e) {
        if (Utils.nullCheck()) {
            this.onDisable(); // Call onDisable instead of disable
            return;
        }
        Packet<?> packet = e.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        if (packet instanceof C00Handshake || packet instanceof C00PacketLoginStart || packet instanceof C00PacketServerQuery || packet instanceof C01PacketPing || packet instanceof C01PacketEncryptionResponse || packet instanceof C00PacketKeepAlive || packet instanceof C0FPacketConfirmTransaction) {
            return;
        }
        this.blinkedPackets.add(packet);
        e.setCanceled(true);
    }
}
