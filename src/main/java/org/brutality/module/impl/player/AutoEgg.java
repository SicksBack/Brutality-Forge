package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class AutoEgg extends Module {

    // Updated constructor to match the expected parameters
    private final NumberSetting minHealth = new NumberSetting("Min Health", this, 5.0, 1.0, 12.0, 0);

    public AutoEgg() {
        super("AutoEgg", "Automatically uses a First-Aid Egg when your health is low.", Category.PLAYER);
        this.addSettings(minHealth);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && mc.thePlayer != null) {
            double currentHealth = mc.thePlayer.getHealth() / 2.0f;
            double minHealthSetting = minHealth.getValue();

            if (currentHealth <= minHealthSetting) {
                useEgg();
            }
        }
    }

    private void useEgg() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer.inventory.hasItem(Item.getItemById(383))) {
            for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

                if (itemStack == null || itemStack.getItem() != Item.getItemById(383) || itemStack.getItemDamage() != 96) {
                    continue;
                }

                if (i > 8) {
                    return;  // Ensure the egg is in a hotbar slot
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
