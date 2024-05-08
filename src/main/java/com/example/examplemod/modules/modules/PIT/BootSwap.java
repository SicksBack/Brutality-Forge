package com.example.examplemod.modules.modules.PIT;

import com.example.examplemod.Client;
import com.example.examplemod.events.Event;
import com.example.examplemod.events.listeners.EventKey;
import com.example.examplemod.events.listeners.EventUpdate;
import com.example.examplemod.modules.Module;
import com.example.examplemod.settings.ModeSetting;
import com.example.examplemod.settings.NumberSetting;
import com.example.examplemod.settings.Setting;
import com.example.examplemod.utils.Colors;
import com.example.examplemod.utils.InventoryUtils;
import com.example.examplemod.utils.Timer;
import com.example.examplemod.utils.Wrapper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

public class BootSwap extends Module {

    public ModeSetting mode = new ModeSetting("Mode", "Open Inv", new String[] { "Open Inv", "Silent", "Inv Only" });
    public NumberSetting delay = new NumberSetting("Delay", 100.0D, 0.0D, 1000.0D, 1.0D);
    public NumberSetting closedelay = new NumberSetting("Close Delay", 100.0D, 0.0D, 1000.0D, 1.0D);
    public NumberSetting keyBind = new NumberSetting("KeyBind", Keyboard.KEY_NONE, Keyboard.KEY_NONE, Keyboard.KEY_NONE, 1.0D);
    public boolean swap;
    public boolean close;
    Timer waittimer;
    public boolean hasDBoots;
    public boolean hasArmas;
    Timer timer;

    public BootSwap() {
        super("BootSwap", "None", 0, Module.Category.PIT, false);
        this.swap = false;
        this.close = false;
        this.waittimer = new Timer();
        this.hasDBoots = false;
        this.hasArmas = false;
        this.timer = new Timer();
        addSettings(new Setting[] { mode, delay, closedelay, keyBind });
    }

    public void disable() {
        this.swap = false;
        if (!this.mode.getMode().equals("Inv Only")) {
            disableKeys();
            if (this.waittimer.hasTimeElapsed((long)closedelay.getValue(), true)) {
                if (this.mode.getMode().equals("Open Inv")) {
                    this.mc.thePlayer.closeScreen();
                    enableKeys();
                } else if (this.mode.getMode().equals("Silent")) {
                    Wrapper.sendPacket(new C0DPacketCloseWindow(this.mc.thePlayer.inventoryContainer.windowId));
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
        this.waittimer.reset();
        enableKeys();
    }

    public void onEnable() {
        this.close = false;
        if (this.mode.getMode().equals("Open Inv")) {
            Wrapper.sendPacket(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
        }
        if (this.mode.getMode().equals("Silent")) {
            Wrapper.sendPacket(new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY));
        }
        this.hasDBoots = false;
        this.timer.reset();
        if (Wrapper.hasItem(this.mc.thePlayer, Items.diamond_boots)) {
            this.swap = true;
            disableKeys();
        } else {
            Wrapper.addChatMessage("You don't have diamond boots!");
            toggle();
        }
    }

    public void disableKeys() {
        if (this.mode.getMode().equals("Silent")) {
            this.mc.gameSettings.keyBindForward.pressed = false;
            this.mc.gameSettings.keyBindJump.pressed = false;
            this.mc.gameSettings.keyBindLeft.pressed = false;
            this.mc.gameSettings.keyBindRight.pressed = false;
            this.mc.gameSettings.keyBindBack.pressed = false;
        }
    }

    public void swapBoots() {
        if (!this.swap) {
            disable();
            return;
        }
        if (!this.mode.getMode().equals("Silent") && !(this.mc.currentScreen instanceof GuiInventory)) {
            this.timer.reset();
            return;
        }
        ItemStack bootsStack = this.mc.thePlayer.getEquipmentInSlot(1);
        if (bootsStack != null && bootsStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor)bootsStack.getItem();
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                this.hasDBoots = true;
                this.hasArmas = false;
                InventoryUtils.SendInventoryClick(8, false, true);
            }
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                this.hasArmas = true;
                this.hasDBoots = false;
                InventoryUtils.SendInventoryClick(8, false, true);
            }
        }
        if (this.hasDBoots && this.timer.hasTimeElapsed((long)delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.leather_boots);
            if (mysticSlot <= 9) mysticSlot += 36;
            if (mysticSlot == 45) mysticSlot = 9;
            InventoryUtils.SendInventoryClick(mysticSlot, false, true);
            Wrapper.addChatMessage("Swapped from " + Colors.aqua + "diamond " + Colors.gray + "to " + Colors.red + "armageddon " + Colors.gray + "boots");
            this.waittimer.reset();
            disable();
        }
        if (this.hasArmas && this.timer.hasTimeElapsed((long)delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.diamond_boots);
            if (mysticSlot <= 9) mysticSlot += 36;
            if (mysticSlot == 45) mysticSlot = 9;
            InventoryUtils.SendInventoryClick(mysticSlot, false, true);
            Wrapper.addChatMessage("Swapped from " + Colors.red + "armageddon " + Colors.gray + "to " + Colors.aqua + "diamond " + Colors.gray + "boots");
            this.waittimer.reset();
            disable();
        }
    }

    public void enableKeys() {
        if (!this.mode.getMode().equals("Inv Only")) {
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindForward.getKeyCode())) this.mc.gameSettings.keyBindForward.pressed = true;
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode())) this.mc.gameSettings.keyBindJump.pressed = true;
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindLeft.getKeyCode())) this.mc.gameSettings.keyBindLeft.pressed = true;
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindRight.getKeyCode()))
                this.mc.gameSettings.keyBindRight.pressed = true;
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindBack.getKeyCode()))
                this.mc.gameSettings.keyBindBack.pressed = true;
        }
    }

    public void onEvent(Event e) {
        if (e instanceof EventKey && this.swap) {
            EventKey eventKey = (EventKey) e;
            if (eventKey.getKey() == keyBind.getValue().intValue()) {
                swapBoots();
            }
        }

        if (e instanceof EventUpdate) {
            for (Module mod : Client.modules) {
                if (mod.name.equals("DlegSwap") && mod.isEnabled()) {
                    DlegSwap dlegswap = (DlegSwap)mod;
                    this.delay.setValue(dlegswap.delay.getValue());
                    this.closedelay.setValue(dlegswap.closedelay.getValue());
                    this.mode.setMode(dlegswap.mode.getMode());
                }
            }
            swapBoots();
        }
    }
}
