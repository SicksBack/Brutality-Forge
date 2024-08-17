package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class AutoHeal extends Module {
    private final NumberSetting healthThreshold = new NumberSetting("Health Threshold", this, 10.0, 1.0, 20.0, 1);

    public AutoHeal() {
        super("AutoHeal", "Automatically uses healing items when health is low", Category.PLAYER);
        addSettings(healthThreshold);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        EntityPlayerSP player = mc.thePlayer;

        if (player.getHealth() <= healthThreshold.getValue()) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() == Items.golden_apple) {
                    mc.playerController.sendUseItem(player, mc.theWorld, stack);
                    break;
                }
            }
        }
    }
}
