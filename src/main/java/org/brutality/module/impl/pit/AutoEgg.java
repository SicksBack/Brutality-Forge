package org.brutality.module.impl.pit;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.events.Event;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Timer;

public class AutoEgg extends Module {
    public NumberSetting health = new NumberSetting("Health", this, 10.0, 1.0, 10.0, 1);
    public NumberSetting delay = new NumberSetting("Delay", this, 100.0, 0.0, 100.0, 1);
    private Timer timer = new Timer();
    private Timer resettimer = new Timer();
    private int currentSlot = 0;
    private boolean useFirstAidEgg = false;
    private int eggSlot;

    public AutoEgg() {
        super("AutoEgg", "probably dogs just swap normally hol shit", Category.PIT);
        this.addSettings(health, delay);
    }

    public void onDisable() {
        this.useFirstAidEgg = false;
    }

    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            if (this.mc.currentScreen != null) {
                return;
            }
            if ((this.mc.thePlayer.getHealth() / 2.0f) <= this.health.getValue()) {
                if (!this.useFirstAidEgg && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
                    this.eggSlot = this.findEggSlot();
                    if (this.eggSlot != -1) {
                        this.currentSlot = this.mc.thePlayer.inventory.currentItem;
                        this.mc.thePlayer.inventory.currentItem = this.eggSlot;
                        this.useFirstAidEgg = true;
                    }
                } else if (this.useFirstAidEgg && this.mc.thePlayer.inventory.currentItem == this.eggSlot) {
                    ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(this.eggSlot);
                    if (itemStack != null && itemStack.stackSize > 0) {
                        this.mc.playerController.sendUseItem(this.mc.thePlayer, this.mc.theWorld, itemStack);
                        this.mc.thePlayer.inventory.currentItem = this.currentSlot;
                        this.useFirstAidEgg = false;
                    }
                }
            }
            if (this.resettimer.hasTimeElapsed(5000L, true)) {
                this.useFirstAidEgg = false;
            }
        }
    }

    private int findEggSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() == Items.egg) {  // Assuming "First-Aid Egg" uses the same item ID as an egg
                return i;
            }
        }
        return -1;
    }
}
