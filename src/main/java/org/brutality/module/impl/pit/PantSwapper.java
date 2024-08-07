package org.brutality.module.impl.pit;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.PacketUtils;
import org.brutality.utils.Timer;

public class PantSwapper extends Module {
    private final Timer timer = new Timer();
    private final NumberSetting delay = new NumberSetting("Delay", this, 100.0, 1.0, 500.0, 1);
    private boolean displayed = false;
    private boolean swapped = false;

    public PantSwapper() {
        super("PantSwapper", "Automatically swaps pants.", Category.PIT);
        this.addSettings(delay);
    }

    @Override
    public void onEnable() {
        timer.reset();
        displayed = false;
        swapped = false;
    }

    @SubscribeEvent
    public void onUpdate(EventUpdate event) {
        if (timer.hasTimeElapsed((long) delay.getValue(), false) && !swapped) {
            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, 7, 0, 2, mc.thePlayer);
            swapped = true;
        }
        if (timer.hasTimeElapsed((long) (delay.getValue() * 2), false)) {
            PacketUtils.sendPacket(new C0DPacketCloseWindow());
            mc.displayGuiScreen(null);
            this.toggle();
            return;
        }
        if (!displayed) {
            displayed = true;
            mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
            PacketUtils.sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
        }
    }

    public boolean move(int item, boolean isArmorSlot) {
        if (mc.currentScreen instanceof GuiInventory && item != -1) {
            boolean full = isArmorSlot;
            boolean openInventory = !(mc.currentScreen instanceof GuiInventory);
            if (openInventory) {
                PacketUtils.sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            }
            if (full) {
                ItemStack[] itemStackArray = mc.thePlayer.inventory.mainInventory;
                for (ItemStack iItemStack : itemStackArray) {
                    if (iItemStack == null) {
                        full = false;
                        break;
                    }
                }
            }
            if (full) {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, item, 1, 4, mc.thePlayer);
            } else {
                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, isArmorSlot ? item : (item < 9 ? item + 36 : item), 1, 1, mc.thePlayer);
            }
            if (openInventory) {
                PacketUtils.sendPacket(new C0DPacketCloseWindow());
            }
            return true;
        }
        return false;
    }
}
