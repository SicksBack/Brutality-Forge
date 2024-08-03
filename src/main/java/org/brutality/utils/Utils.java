package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import org.brutality.settings.impl.NumberSetting;

import java.util.Random;

public class Utils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean nullCheck() {
        return mc.thePlayer == null || mc.theWorld == null;
    }

    public static boolean holdingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static boolean holdingWeapon() {
        return holdingSword();
    }

    public static void attackEntity(EntityLivingBase target, boolean swingArm, boolean silent) {
        mc.thePlayer.swingItem();
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        if (silent) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        }
    }

    public static double getRandomValue(NumberSetting min, NumberSetting max, Random rand) {
        return min.getValue() + (max.getValue() - min.getValue()) * rand.nextDouble();
    }

    public static void correctValue(NumberSetting min, NumberSetting max) {
        if (min.getValue() > max.getValue()) {
            min.setValue(max.getValue());
        }
    }

    public static long getDifference(long start, long end) {
        return end - start;
    }

    public static String formatColor(String message) {
        return message.replaceAll("&", "\u00a7");
    }
}
