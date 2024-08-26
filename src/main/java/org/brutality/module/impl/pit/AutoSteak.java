package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

public class AutoSteak extends Module {

    private final BooleanSetting useAgainOnceEffectsRunOut = new BooleanSetting("Use Again Once Effects Run Out", this, true);

    private long lastUseTime = 0;

    public AutoSteak() {
        super("AutoSteak", "Automatically uses mutton when the conditions are met.", Category.PIT);
        this.addSettings(useAgainOnceEffectsRunOut);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && mc.thePlayer != null) {
            long currentTime = System.currentTimeMillis();

            // Check if 10 seconds have passed since the last use
            if (useAgainOnceEffectsRunOut.isEnabled() && currentTime - lastUseTime >= 10000) {
                useMutton();
                lastUseTime = currentTime;
            }
        }
    }

    private void useMutton() {
        Minecraft mc = Minecraft.getMinecraft();

        // 423 is the ID for mutton (raw)
        if (mc.thePlayer.inventory.hasItem(Item.getItemById(423))) {
            for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

                if (itemStack == null || itemStack.getItem() != Item.getItemById(423)) {
                    continue;
                }

                if (i > 8) {
                    return;  // Ensure the mutton is in a hotbar slot
                }

                int previousSlot = mc.thePlayer.inventory.currentItem;
                mc.thePlayer.inventory.currentItem = i;
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                mc.thePlayer.inventory.currentItem = previousSlot;

                break;
            }
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Optional: Implement any player interaction logic here if needed
    }
}
