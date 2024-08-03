package org.brutality.module.impl.pit;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
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

    @Override
    public void onEnable() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
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

    @Override
    public void onDisable() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
        this.waittimer.reset();
        this.enableKeys();
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
            } else if (armor.getArmorMaterial() == ItemArmor.ArmorMaterial.LEATHER) {
                this.hasLeather = true;
                this.hasDlegs = false;
            }
        }
        if (this.hasDlegs && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.leather_leggings);
            if (mysticSlot < 0) return;
            if (mysticSlot <= 9) {
                mysticSlot += 36;
            }
            sendSwapPacket(mysticSlot);
            Wrapper.addChatMessage("§0[§4B§0] - §6Successfully Swapped To Leather Leggings");
            this.waittimer.reset();
            this.disable();
        } else if (this.hasLeather && this.timer.hasTimeElapsed((long) this.delay.getValue(), true)) {
            int mysticSlot = Wrapper.findItem(this.mc.thePlayer, Items.diamond_leggings);
            if (mysticSlot < 0) return;
            if (mysticSlot <= 9) {
                mysticSlot += 36;
            }
            sendSwapPacket(mysticSlot);
            Wrapper.addChatMessage("§0[§4B§0] - §6Successfully Swapped To Diamond Leggings");
            this.waittimer.reset();
            this.disable();
        }
    }

    private void sendSwapPacket(int slot) {
        int windowId = this.mc.thePlayer.inventoryContainer.windowId;
        short actionNumber = this.mc.thePlayer.openContainer.getNextTransactionID(this.mc.thePlayer.inventory);
        ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(slot);
        C0EPacketClickWindow clickWindowPacket = new C0EPacketClickWindow(windowId, slot, 0, 2, stack, actionNumber);
        this.mc.thePlayer.sendQueue.addToSendQueue(clickWindowPacket);
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

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            swapLeggings();
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == this.mc.thePlayer) {
            if (this.swap) {
                this.disableKeys();
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (this.inInv && event.gui instanceof GuiInventory) {
            event.setCanceled(true);
        }
    }
}
