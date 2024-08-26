package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Wrapper;

public class AutoTelebow extends Module {

    private final NumberSetting degrees = new NumberSetting("Degrees", this, 360, 1, 360, 0);
    private final NumberSetting health = new NumberSetting("Health", this, 10, 1, 12, 0);

    public AutoTelebow() {
        super("AutoTelebow", "Automatically uses the Telebow", Category.PIT);
        addSettings(degrees, health);
    }

    @Override
    public void onEnable() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Find and select the best Telebow in the hotbar
        ItemStack telebow = findBestTelebow();
        if (telebow == null) {
            // Notify the player if no Telebow is found
            Wrapper.addChatMessage(Wrapper.Colors.black + "[" +
                    Wrapper.Colors.black + "]" +
                    Wrapper.Colors.dark_red + "B" +
                    Wrapper.Colors.black + "] " +
                    Wrapper.Colors.red + "Couldn't Find A Telebow!"
            );
            return; // Exit if no Telebow is found
        }

        // Swap to the Telebow
        int slot = Wrapper.findItem(mc.thePlayer, telebow.getItem());
        if (slot != -1) {
            mc.thePlayer.inventory.currentItem = slot;
            mc.thePlayer.inventoryContainer.slotClick(slot, 0, 1, mc.thePlayer);
        }

        // Check health and apply the appropriate action
        double playerHealth = mc.thePlayer.getHealth();
        if (playerHealth <= health.getValue()) {
            // Set the desired angle
            float angle = (float) degrees.getValue();

            // Adjust power and shoot based on NBT tag
            boolean isMegaLongbow = telebow.hasTagCompound() && telebow.getTagCompound().hasKey("Mega Longbow");
            shootTelebow(isMegaLongbow);
        }
    }

    private ItemStack findBestTelebow() {
        ItemStack bestTelebow = null;
        int highestTier = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() != null) {
                // Check for Telebow NBT tags
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && tag.hasKey("Telebow")) {
                    int tier = getTelebowTier(tag);
                    if (tier > highestTier) {
                        highestTier = tier;
                        bestTelebow = stack;
                    }
                }
            }
        }

        return bestTelebow;
    }

    private int getTelebowTier(NBTTagCompound tag) {
        if (tag.hasKey("Tier")) {
            return tag.getInteger("Tier");
        }
        return 0; // Tier 1 by default if no specific tag is found
    }

    private void shootTelebow(boolean isMegaLongbow) {
        // Send the necessary packets to shoot the Telebow
        if (isMegaLongbow) {
            // For Mega Longbow, shoot for 0.3 seconds
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
        } else {
            // Full power shot
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        }
    }
}
