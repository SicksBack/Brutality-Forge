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

public class SogeSwap extends Module {

    private final NumberSetting delaySetting;
    private final BooleanSetting holdLeftClick;
    private long lastSlotSet;
    private long lastSwap;
    private int swordSlot;
    private int tierIIISwordSlot;
    private boolean toggle = true;

    public SogeSwap() {
        super("SogeSwap", "Soge Swap", Category.PLAYER);
        this.delaySetting = new NumberSetting("Delay", this, 50, 1, 100, 1);
        this.holdLeftClick = new BooleanSetting("Hold Left Click", this, true);
        addSettings(delaySetting, holdLeftClick);
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
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i).getDisplayName().contains("Diamond Sword")) {
                this.swordSlot = i;
            }
            if (Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i).getDisplayName().contains("Tier III Sword")) {
                this.tierIIISwordSlot = i;
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

        if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null &&
                (Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().contains("Tier III Sword") ||
                        Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().contains("Diamond Sword"))) {
            handleAutoSwap();
        }
        this.lastSwap = System.currentTimeMillis();
    }

    private void handleAutoSwap() {
        int presentItems = 0;
        if (this.swordSlot != -1) {
            ++presentItems;
        }
        if (this.tierIIISwordSlot != -1) {
            ++presentItems;
        }
        if (presentItems >= 2) {
            if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().contains("Diamond Sword")) {
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.tierIIISwordSlot;
            } else if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null && Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().contains("Tier III Sword")) {
                Minecraft.getMinecraft().thePlayer.inventory.currentItem = this.swordSlot;
            }
            this.lastSwap = System.currentTimeMillis();
        }
    }
}