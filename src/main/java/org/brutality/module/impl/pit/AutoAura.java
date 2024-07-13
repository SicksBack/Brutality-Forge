package org.brutality.module.impl.pit;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.events.Event;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Timer;

public class AutoAura extends Module {
    public NumberSetting delay = new NumberSetting("Delay", this, 100.0, 0.0, 100.0, 1);
    private Timer timer = new Timer();
    private Timer resettimer = new Timer();
    private int currentSlot = 0;
    private boolean useAura = false;
    private int auraSlot;

    public AutoAura() {
        super("AutoAura", "so uh u go in mid and u fuck baddies", Category.PIT);
        this.addSettings(delay);
    }

    public void onDisable() {
        this.useAura = false;
    }

    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (this.mc.currentScreen != null) {
                return;
            }
            if (!this.useAura && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
                this.auraSlot = this.findAuraSlot();
                if (this.auraSlot != -1) {
                    this.currentSlot = this.mc.thePlayer.inventory.currentItem;
                    this.mc.thePlayer.inventory.currentItem = this.auraSlot;
                    this.useAura = true;
                }
            } else if (this.useAura && this.mc.thePlayer.inventory.currentItem == this.auraSlot) {
                ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(this.auraSlot);
                if (itemStack != null && itemStack.stackSize > 0) {
                    this.mc.playerController.sendUseItem(this.mc.thePlayer, this.mc.theWorld, itemStack);
                    this.mc.thePlayer.inventory.currentItem = this.currentSlot;
                    this.useAura = false;
                }
            }
            if (this.resettimer.hasTimeElapsed(5000L, true)) {
                this.useAura = false;
            }
        }
    }

    private int findAuraSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() == Items.slime_ball) {  // Assuming "Aura of Protection" uses the same item ID as slime ball
                return i;
            }
        }
        return -1;
    }
}
