package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.util.ChatComponentText;

public class Wrapper {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static EntityPlayer getPlayer() {
        return mc.thePlayer;
    }

    public static World getWorld() {
        return mc.theWorld;
    }

    public static void addChatMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
