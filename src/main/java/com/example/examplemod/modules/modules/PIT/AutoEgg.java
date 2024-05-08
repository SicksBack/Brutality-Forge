package com.example.examplemod.modules.modules.PIT;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoRightClickModule {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Item itemToRightClick;
    private int healthThreshold;

    public AutoRightClickModule(Item itemToRightClick, int healthThreshold) {
        this.itemToRightClick = itemToRightClick;
        setHealthThreshold(healthThreshold); // Validate and set the health threshold
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setHealthThreshold(int healthThreshold) {
        // Ensure health threshold is within the range 1-10
        this.healthThreshold = Math.max(1, Math.min(10, healthThreshold));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null)
            return;

        if (mc.thePlayer.getHealth() <= healthThreshold) {
            // Check if the specified item is in the player's inventory
            for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() == itemToRightClick) {
                    // Perform right-click action
                    // Example: mc.playerController.processRightClick(mc.thePlayer, mc.theWorld, stack, EnumHand.MAIN_HAND);
                    break;
                }
            }
        }
    }
}
