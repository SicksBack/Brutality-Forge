package org.brutality.module.impl.pit;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.events.Event;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Timer;

public class AutoSteak extends Module {
    public NumberSetting delay = new NumberSetting("Delay", this, 100.0, 0.0, 100.0, 1);
    private Timer timer = new Timer();
    private Timer resettimer = new Timer();
    private int currentSlot = 0;
    private boolean useSteak = false;
    private int steakSlot;

    public AutoSteak() {
        super("AutoSteak", "me personally i love steak - Sick 2024", Category.PIT);
        this.addSettings(delay);
    }

    public void onDisable() {
        this.useSteak = false;
    }

    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (this.mc.currentScreen != null) {
                return;
            }
            if (!this.useSteak && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
                this.steakSlot = this.findSteakSlot();
                if (this.steakSlot != -1) {
                    this.currentSlot = this.mc.thePlayer.inventory.currentItem;
                    this.mc.thePlayer.inventory.currentItem = this.steakSlot;
                    this.useSteak = true;
                }
            } else if (this.useSteak && this.mc.thePlayer.inventory.currentItem == this.steakSlot) {
                ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(this.steakSlot);
                if (itemStack != null && itemStack.stackSize > 0) {
                    this.mc.playerController.sendUseItem(this.mc.thePlayer, this.mc.theWorld, itemStack);
                    this.mc.thePlayer.inventory.currentItem = this.currentSlot;
                    this.useSteak = false;
                }
            }
            if (this.resettimer.hasTimeElapsed(5000L, true)) {
                this.useSteak = false;
            }
        }
    }

    private int findSteakSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() == Items.cooked_beef) {  // Assuming "AAA-Rated Steak" uses the same item ID as cooked beef
                return i;
            }
        }
        return -1;
    }
}
