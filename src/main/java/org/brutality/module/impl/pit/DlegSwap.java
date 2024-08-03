package org.brutality.module.impl.pit;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import org.brutality.events.Event;
import org.brutality.events.listeners.EventKey;
import org.brutality.events.listeners.EventPacketSend;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.InventoryUtils;
import org.brutality.utils.Timer;
import org.brutality.utils.Wrapper;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;

public class DlegSwap extends Module {
    public SimpleModeSetting mode = new SimpleModeSetting("Mode", this, "Open Inv", new String[]{"Open Inv", "Silent", "Inv Only"});
    public NumberSetting delay = new NumberSetting("Delay", this, 100, 0, 1000, 1);
    public NumberSetting closedelay = new NumberSetting("Close Delay", this, 100, 0, 1000, 1);
    public boolean swap = false;
    public boolean close = false;
    public boolean inInv = false;
    Timer waittimer = new Timer();
    public boolean hasDlegs = false;
    public boolean hasLeather = false;
    Timer timer = new Timer();

    public DlegSwap() {
        super("DlegSwap", "None", Category.PIT);
        addSettings(mode, delay, closedelay);
    }

    public void disable() {
        this.swap = false;
        if (!this.mode.getValue().equals("Inv Only")) {
            this.disableKeys();
            if (this.waittimer.hasTimeElapsed((long) this.closedelay.getValue(), true)) {
                if (this.mode.getValue().equals("Open Inv")) {
                    this.mc.thePlayer.closeScreen();
                    this.inInv = false;
                    this.enableKeys();
                } else if (this.mode.getValue().equals("Silent")) {
                    C0DPacketCloseWindow CLOSE_INV = new C0DPacketCloseWindow(this.mc.thePlayer.inventoryContainer.windowId);
                    this.mc.thePlayer.sendQueue.addToSendQueue(CLOSE_INV);
                    this.inInv = false;
                }
                this.toggle();
            } else {
                this.disableKeys();
            }
        } else {
            this.toggle();
            this.enableKeys();
        }
    }

    public void onDisable() {
        this.waittimer.reset();
        this.enableKeys();
    }

    private void setKeyPressedState(Object keyBind, boolean state) {
        try {
            Field field = keyBind.getClass().getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(keyBind, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableKeys() {
        if (this.mode.getValue().equals("Silent")) {
            setKeyPressedState(this.mc.gameSettings.keyBindForward, false);
            setKeyPressedState(this.mc.gameSettings.keyBindJump, false);
            setKeyPressedState(this.mc.gameSettings.keyBindLeft, false);
            setKeyPressedState(this.mc.gameSettings.keyBindRight, false);
            setKeyPressedState(this.mc.gameSettings.keyBindBack, false);
        }
    }

    public void swapLeggings() {
        if (!this.swap) {
            this.disable();
            return;
        }
        if (!this.mode.getValue().equals("Silent") && !(this.mc.currentScreen instanceof GuiInventory)) {
            this.timer.reset();
            return;
        }
        ItemStack leggingsStack = this.mc.thePlayer.getEquipmentInSlot(2);
        if (leggingsStack != null && leggingsStack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) leggingsStack.getItem();
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                this.hasDlegs = true;
                this.hasLeather = false;
                InventoryUtils.SendInventoryClick(7, false, true);
            }
            if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                this.hasLeather = true;
                this.hasDlegs = false;
                InventoryUtils.SendInventoryClick(7, false, true);
            }
        }
        if (this.hasDlegs && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.leather_leggings);
            if (mysticSlot <= 9) {
                mysticSlot += 36;
            }
            if (mysticSlot == 45) {
                mysticSlot = 9;
            }
            InventoryUtils.SendInventoryClick(mysticSlot, false, true);
            Wrapper.addChatMessage("§0[§4B§0] - §6Successfully Swapped To Dlegs");
            this.waittimer.reset();
            this.disable();
        }
        if (this.hasLeather && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.diamond_leggings);
            if (mysticSlot <= 9) {
                mysticSlot += 36;
            }
            if (mysticSlot == 45) {
                mysticSlot = 9;
            }
            InventoryUtils.SendInventoryClick(mysticSlot, false, true);
            Wrapper.addChatMessage("§0[§4B§0] - §6Successfully Swapped To Dlegs");
            this.waittimer.reset();
            this.disable();
        }
    }

    public void enableKeys() {
        if (!this.mode.getValue().equals("Inv Only")) {
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindForward.getKeyCode())) {
                setKeyPressedState(this.mc.gameSettings.keyBindForward, true);
            }
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindJump.getKeyCode())) {
                setKeyPressedState(this.mc.gameSettings.keyBindJump, true);
            }
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindLeft.getKeyCode())) {
                setKeyPressedState(this.mc.gameSettings.keyBindLeft, true);
            }
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindRight.getKeyCode())) {
                setKeyPressedState(this.mc.gameSettings.keyBindRight, true);
            }
            if (Keyboard.isKeyDown(this.mc.gameSettings.keyBindBack.getKeyCode())) {
                setKeyPressedState(this.mc.gameSettings.keyBindBack, true);
            }
        }
    }

    public void onEnable() {
        C0BPacketEntityAction OPEN_INV;
        this.close = false;
        if (this.mode.getValue().equals("Open Inv")) {
            OPEN_INV = new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY);
            this.mc.thePlayer.sendQueue.addToSendQueue(OPEN_INV);
            this.mc.displayGuiScreen(new GuiInventory(this.mc.thePlayer));
            this.inInv = true;
        }
        if (this.mode.getValue().equals("Silent")) {
            OPEN_INV = new C0BPacketEntityAction(this.mc.thePlayer, C0BPacketEntityAction.Action.OPEN_INVENTORY);
            this.mc.thePlayer.sendQueue.addToSendQueue(OPEN_INV);
            this.inInv = true;
        }
        this.hasDlegs = false;
        this.timer.reset();
        if (Wrapper.hasItem(this.mc.thePlayer, Items.diamond_leggings)) {
            this.swap = true;
            this.disableKeys();
        } else {
            Wrapper.addChatMessage("§0[§4B§0] - §cNo Dleg Found");
            this.toggle();
        }
    }

    public void onEvent(Event e) {
        if (e instanceof EventPacketSend && this.inInv) {
            Packet<?> packet = ((EventPacketSend) e).getPacket();
            if (packet instanceof C0BPacketEntityAction) {
                C0BPacketEntityAction actionPacket = (C0BPacketEntityAction) packet;
                if (actionPacket.getAction().equals(C0BPacketEntityAction.Action.OPEN_INVENTORY)) {
                    e.setCancelled(true);
                }
            }
        }
        if (e instanceof EventKey && this.swap) {
            this.disableKeys();
        }
        if (e instanceof EventUpdate) {
            this.swapLeggings();
        }
    }
}
