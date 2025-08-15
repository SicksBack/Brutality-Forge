package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class GambleSwapper extends Module {
    private final NumberSetting healthTolerance;
    private final NumberSetting gambleSlot;
    private final NumberSetting otherSwordSlot;
    private boolean switchedToOtherSword = false;

    public GambleSwapper() {
        super("Gamble Swapper", "Swap from Gamble to Regular Sword when reaching low health", Category.PLAYER);
        this.healthTolerance = new NumberSetting("Health", this, 10, 1, 20, 0);
        this.gambleSlot = new NumberSetting("Gamble Slot", this, 1, 1, 9, 0);
        this.otherSwordSlot = new NumberSetting("Other Slot", this, 2, 1, 9, 0);
        addSettings(healthTolerance, gambleSlot, otherSwordSlot);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (player != null) {
            double currentHealth = player.getHealth();
            if (!switchedToOtherSword && currentHealth <= healthTolerance.getValue()) {
                player.inventory.currentItem = (int) (otherSwordSlot.getValue() - 1);
                switchedToOtherSword = true;
            } else if (switchedToOtherSword && currentHealth >= healthTolerance.getValue() + 2) {
                player.inventory.currentItem = (int) (gambleSlot.getValue() - 1);
                switchedToOtherSword = false;
            }
        }
    }
}
