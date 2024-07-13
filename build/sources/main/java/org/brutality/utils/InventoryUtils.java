package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
    public static void SendInventoryClick(int slot, boolean rightClick, boolean windowClick) {
        Minecraft mc = Minecraft.getMinecraft();
        int clickType = rightClick ? 1 : 0;
        if (windowClick) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, clickType, 0, mc.thePlayer);
        } else {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, clickType, 2, mc.thePlayer);
        }
    }

    public static int findItem(ItemStack[] inventory, ItemStack item) {
        for (int i = 0; i < inventory.length; i++) {
            if (inventory[i] != null && inventory[i].isItemEqual(item)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean hasItem(ItemStack[] inventory, ItemStack item) {
        return findItem(inventory, item) != -1;
    }
}
