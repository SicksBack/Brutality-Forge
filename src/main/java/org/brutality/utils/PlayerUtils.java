package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemStack;

public class PlayerUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isHoldingWeapon() {
        ItemStack itemStack = mc.thePlayer.getHeldItem();
        return itemStack != null && itemStack.getItem() instanceof ItemSword;
    }
}
