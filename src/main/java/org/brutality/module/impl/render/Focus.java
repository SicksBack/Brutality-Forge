package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.utils.FriendManager;
import org.brutality.utils.KOSManager;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

public class Focus extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private ItemStack[] previousArmor;
    private ItemStack previousHeldItem;

    public Focus() {
        super("Focus", "Hides all other players, armor, and held items", Category.RENDER);
        setKey(Keyboard.KEY_H);
    }

    @SubscribeEvent
    public void onRenderLivingPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (event.entity != null && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player != mc.thePlayer &&
                    !FriendManager.isFriend(player) &&
                    !KOSManager.isKOS(player)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Hide mc.thePlayer and their held items in 2nd and 3rd person perspectives
        if (mc.thePlayer != null) {
            previousArmor = mc.thePlayer.inventory.armorInventory.clone(); // Save current armor
            previousHeldItem = mc.thePlayer.inventory.getCurrentItem(); // Save current held item
            updateVisibility(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Restore mc.thePlayer's armor and held items
        if (mc.thePlayer != null) {
            updateVisibility(false);
            mc.thePlayer.inventory.armorInventory = previousArmor; // Restore armor
            mc.thePlayer.inventory.setInventorySlotContents(mc.thePlayer.inventory.currentItem, previousHeldItem); // Restore held item
        }
    }

    @SubscribeEvent
    public void onRenderItem(RenderLivingEvent.Pre<EntityLivingBase> event) {
        if (event.entity != null && event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player == mc.thePlayer && mc.gameSettings.thirdPersonView != 0) {
                event.setCanceled(true); // Cancel rendering of items for the player
            }
        }
    }

    @SubscribeEvent
    public void onPerspectiveChange(PlayerEvent event) {
        updateVisibility(mc.gameSettings.thirdPersonView != 0);
    }

    private void updateVisibility(boolean hide) {
        if (mc.thePlayer != null) {
            mc.thePlayer.setInvisible(hide);
            if (hide) {
                // Hide armor and held items
                mc.thePlayer.inventory.armorInventory[0] = null; // Hide helmet
                mc.thePlayer.inventory.armorInventory[1] = null; // Hide chestplate
                mc.thePlayer.inventory.armorInventory[2] = null; // Hide leggings
                mc.thePlayer.inventory.armorInventory[3] = null; // Hide boots
                mc.thePlayer.inventory.setInventorySlotContents(mc.thePlayer.inventory.currentItem, null); // Hide held item
            }
            // No need to handle restoring armor and items here since it's done in onDisable()
        }
    }
}
