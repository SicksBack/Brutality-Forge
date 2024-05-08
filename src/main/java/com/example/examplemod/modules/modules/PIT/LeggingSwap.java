package com.example.examplemod.modules.modules.PIT;

import com.example.examplemod.Client;
import com.example.examplemod.events.Event;
import com.example.examplemod.events.listeners.EventKey;
import com.example.examplemod.events.listeners.EventUpdate;
import com.example.examplemod.modules.Module;
import com.example.examplemod.settings.ModeSetting;
import com.example.examplemod.settings.NumberSetting;
import com.example.examplemod.settings.Setting;
import com.example.examplemod.utils.InventoryUtils;
import com.example.examplemod.utils.Timer;
import com.example.examplemod.utils.Wrapper;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

public class LeggingSwap extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Open Inv", new String[] { "Open Inv", "Silent", "Inv Only" });
    public NumberSetting delay = new NumberSetting("Delay", 100.0D, 0.0D, 1000.0D, 1.0D);
    public NumberSetting closedelay = new NumberSetting("Close Delay", 100.0D, 0.0D, 1000.0D, 1.0D);
    public NumberSetting keyBind = new NumberSetting("KeyBind", Keyboard.KEY_NONE, Keyboard.KEY_NONE, Keyboard.KEY_NONE, 1.0D);
    public boolean swap;
    public boolean close;
    Timer waitTimer;
    public boolean hasDiamondLeggings;
    public boolean hasLeatherLeggings;
    Timer timer;

    public LeggingSwap() {
        super("LeggingSwap", "None", 0, Module.Category.PIT, false);
        this.swap = false;
        this.close = false;
        this.waitTimer = new Timer();
        this.hasDiamondLeggings = false;
        this.hasLeatherLeggings = false;
        this.timer = new Timer();
        addSettings(new Setting[] { mode, delay, closedelay, keyBind });
    }

    public void disable() {
        this.swap = false;
        if (!this.mode.getMode().equals("Inv Only")) {
            disableKeys();
            if (this.waitTimer.hasTimeElapsed((long) closedelay.getValue(), true)) {
                if (this.mode.getMode().equals("Open Inv")) {
                    this.mc.thePlayer.closeScreen();
                    enableKeys();
                } else if (this.mode.getMode().equals("Silent")) {
                    // Implement silent closing of inventory
                }
                toggle();
            } else {
                disableKeys();
            }
        } else {
            toggle();
            enableKeys();
        }
    }

    public void onDisable() {
        this.waitTimer.reset();
        enableKeys();
    }

    public void onEnable() {
        this.close = false;
        if (this.mode.getMode().equals("Open Inv")) {
            Wrapper.openInventory();
        } else if (this.mode.getMode().equals("Silent")) {
            // Implement silent opening of inventory
        }
        this.hasDiamondLeggings = false;
        this.timer.reset();
        if (Wrapper.hasItem(this.mc.thePlayer, Items.diamond_leggings)) {
            this.swap = true;
            disableKeys();
        } else {
            Wrapper.addChatMessage("You don't have diamond leggings!");
            toggle();
        }
    }

    public void disableKeys() {
        if (this.mode.getMode().equals("Silent")) {
            // Implement disabling of movement keys
        }
    }

    public void swapLeggings() {
        if (!this.swap) {
            disable();
            return;
        }
        if (!this.mode.getMode().equals("Silent") && !Wrapper.isInventoryOpen()) {
            this.timer.reset();
            return;
        }
        ItemStack leggingsStack = this.mc.thePlayer.getEquipmentInSlot(2);
        if (leggingsStack != null && leggingsStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) leggingsStack.getItem();
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                this.hasDiamondLeggings = true;
                this.hasLeatherLeggings = false;
                InventoryUtils.sendSlotClick(5, 0, 0);
            }
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                this.hasLeatherLeggings = true;
                this.hasDiamondLeggings = false;
                InventoryUtils.sendSlotClick(5, 0, 0);
            }
        }
        if (this.hasDiamondLeggings && this.timer.hasTimeElapsed((long) delay.getValue(), true)) {
            int leatherLeggingsSlot = Wrapper.findItem(this.mc.thePlayer, Items.leather_leggings);
            if (leatherLeggingsSlot <= 9) leatherLeggingsSlot += 36;
            if (leatherLeggingsSlot == 45) leatherLeggingsSlot = 9;
            InventoryUtils.sendSlotClick(leatherLeggingsSlot, 0, 0);
            Wrapper.addChatMessage("Swapped from diamond to leather leggings");
            this.waitTimer.reset();
            disable();
        }
        if (this.hasLeatherLeggings && this.timer.hasTimeElapsed((long) delay.getValue(), true)) {
            int diamondLeggingsSlot = Wrapper.findItem(this.mc.thePlayer, Items.diamond_leggings);
            if (diamondLeggingsSlot <= 9) diamondLeggingsSlot += 36;
            if (diamondLeggingsSlot == 45) diamondLeggingsSlot = 9;
            InventoryUtils.sendSlotClick(diamondLeggingsSlot, 0, 0);
            Wrapper.addChatMessage("Swapped from leather to diamond leggings");
            this.waitTimer.reset();
            disable();
        }
    }

    public void enableKeys() {
        if (!this.mode.getMode().equals("Inv Only")) {
            // Implement enabling of movement keys
        }
    }

    public void onEvent(Event e) {
        if (e instanceof EventKey && this.swap) {
            EventKey eventKey = (EventKey) e;
            if (eventKey.getKey() == keyBind.getValue().intValue()) {
                swapLeggings();
            }
        }

        if (e instanceof EventUpdate) {
            for (Module mod : Client.modules) {
                if (mod.name.equals("LeggingSwap") && mod.isEnabled()) {
                    LeggingSwap leggingSwap = (LeggingSwap) mod;
                    this.delay.setValue(leggingSwap.delay.getValue());
                    this.closedelay.setValue(leggingSwap.closedelay.getValue());
                    this.mode.setMode(leggingSwap.mode.getMode());
                }
            }
            swapLeggings();
        }
    }
}
