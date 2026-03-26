package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

public class AutoAura extends Module {

    private final BooleanSetting useAgainResistance = new BooleanSetting("Use Again When Resistance Runs Out", this, true);
    private final BooleanSetting useAgainImmunity = new BooleanSetting("Use Again When Immunity Runs Out", this, true);

    private long lastUseTimeResistance = 0;
    private long lastUseTimeImmunity = 0;

    public AutoAura() {
        super("AutoAura", "Automatically uses a slimeball when the conditions are met.", Category.PLAYER);
        this.addSettings(useAgainResistance, useAgainImmunity);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && mc.thePlayer != null) {
            long currentTime = System.currentTimeMillis();

            // Prioritize the 15-second cooldown
            if (useAgainImmunity.isEnabled() && currentTime - lastUseTimeImmunity >= 15000) {
                useSlimeball();
                lastUseTimeImmunity = currentTime;
            } else if (useAgainResistance.isEnabled() && currentTime - lastUseTimeResistance >= 4000) {
                useSlimeball();
                lastUseTimeResistance = currentTime;
            } else if (!useAgainResistance.isEnabled() && !useAgainImmunity.isEnabled()) {
                useSlimeball();
            }
        }
    }

    private void useSlimeball() {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.thePlayer.inventory.hasItem(Item.getItemById(341))) {  // 341 is the ID for slimeball
            for (int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
                ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];

                if (itemStack == null || itemStack.getItem() != Item.getItemById(341)) {
                    continue;
                }

                if (i > 8) {
                    return;  // Ensure the slimeball is in a hotbar slot
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
