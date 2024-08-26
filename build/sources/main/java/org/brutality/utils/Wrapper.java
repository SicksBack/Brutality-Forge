package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

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

    public static boolean hasItem(EntityPlayer player, Item item) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack != null && stack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    public static int findItem(EntityPlayer player, Item item) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    public static class Colors {
        public static final String black = EnumChatFormatting.BLACK.toString();
        public static final String dark_red = EnumChatFormatting.DARK_RED.toString();
        public static final String red = EnumChatFormatting.RED.toString();
        public static final String green = EnumChatFormatting.GREEN.toString();
        public static final String gold = EnumChatFormatting.GOLD.toString();
        public static final String gray = EnumChatFormatting.GRAY.toString();
        public static final String dark_gray = EnumChatFormatting.DARK_GRAY.toString();
        public static final String blue = EnumChatFormatting.BLUE.toString();
        public static final String dark_blue = EnumChatFormatting.DARK_BLUE.toString();
        public static final String light_purple = EnumChatFormatting.LIGHT_PURPLE.toString();
        public static final String purple = EnumChatFormatting.DARK_PURPLE.toString();
        public static final String aqua = EnumChatFormatting.AQUA.toString();
        public static final String yellow = EnumChatFormatting.YELLOW.toString();
        public static final String white = EnumChatFormatting.WHITE.toString();
    }
}
