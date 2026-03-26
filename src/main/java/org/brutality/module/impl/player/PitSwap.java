package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class PitSwap extends Module {

    private final NumberSetting delaySetting;
    private final BooleanSetting holdLeftClick;
    private final BooleanSetting useDiamondAxe;
    private long lastSlotSet;
    private long lastSwap;
    private int swordSlot;
    private int spadeSlot;
    private final boolean toggle = true;

    public PitSwap() {
        super("PitSwap", "Automatically swaps between sword and spade", Category.PLAYER);
        this.delaySetting = new NumberSetting("Delay", this, 50, 1, 100, 0);
        this.holdLeftClick = new BooleanSetting("Hold Left Click", this, true);
        this.useDiamondAxe = new BooleanSetting("Use Diamond Axe", this, false);
        addSettings(delaySetting, holdLeftClick, useDiamondAxe);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastSlotSet = 0;
        this.lastSwap = 0;
    }

    @SubscribeEvent
    public void setSlots(TickEvent.ClientTickEvent e) {
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null || System.currentTimeMillis() - this.lastSlotSet < 1000L || !this.toggle) {
            return;
        }
        for (int i = 0; i <= 9; ++i) {
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i) == null) continue;
            String itemName = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i).getDisplayName();
            if (useDiamondAxe.isEnabled()) {
                if (itemName.contains("Diamond Axe")) {
                    this.swordSlot = i;
                }
            } else {
                if (itemName.contains("Diamond Sword")) {
                    this.swordSlot = i;
                }
            }
            if (itemName.contains("Combat Spade")) {
                this.spadeSlot = i;
            }
        }
        this.lastSlotSet = System.currentTimeMillis();
    }

    @SubscribeEvent
    public void swap(TickEvent.RenderTickEvent e) {
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null || System.currentTimeMillis() - this.lastSwap < this.delaySetting.getValue() || !this.toggle) {
            return;
        }

        if (holdLeftClick.isEnabled()) {
            if (!Mouse.isButtonDown(0) || Minecraft.getMinecraft().currentScreen instanceof GuiContainer || Minecraft.getMinecraft().thePlayer.getHeldItem() == null) {
                return;
            }
        }

        if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
            String heldItemName = Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName();
            if ((useDiamondAxe.isEnabled() && (heldItemName.contains("Diamond Axe") || heldItemName.contains("Combat Spade"))) ||
                    (!useDiamondAxe.isEnabled() && (heldItemName.contains("Diamond Sword") || heldItemName.contains("Combat Spade")))) {
                handleAutoSwap();
            }
        }
        this.lastSwap = System.currentTimeMillis();
    }

    private void handleAutoSwap() {
        int presentItems = 0;
        if (this.swordSlot != -1) {
            ++presentItems;
        }
        if (this.spadeSlot != -1) {
            ++presentItems;
        }
        if (presentItems >= 2) {
            if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
                String heldItemName = Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName();
                if (useDiamondAxe.isEnabled()) {
                    if (heldItemName.contains("Diamond Axe")) {
                        Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.spadeSlot;
                    } else if (heldItemName.contains("Combat Spade")) {
                        Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.swordSlot;
                    }
                } else {
                    if (heldItemName.contains("Diamond Sword")) {
                        Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.spadeSlot;
                    } else if (heldItemName.contains("Combat Spade")) {
                        Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.swordSlot;
                    }
                }
            }
            this.lastSwap = System.currentTimeMillis();
        }
    }
}
