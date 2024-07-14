package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class SlotSwap extends Module {

    private final NumberSetting slot1Setting;
    private final NumberSetting slot2Setting;
    private final NumberSetting delaySetting;
    private long lastTime;
    private int slot;

    public SlotSwap() {
        super("SlotSwap", "Automatically swaps between two slots", Category.PIT);
        this.slot1Setting = new NumberSetting("Slot #1", this, 1, 1, 9, 0);
        this.slot2Setting = new NumberSetting("Slot #2", this, 2, 1, 9, 0);
        this.delaySetting = new NumberSetting("Delay (ms)", this, 50, 1, 1000, 0);
        addSettings(slot1Setting, slot2Setting, delaySetting);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastTime = System.currentTimeMillis();
        this.slot = (int) this.slot1Setting.getValue() - 1;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= delaySetting.getValue()) {
            lastTime = currentTime;
            swapSlot();
        }
    }

    private void swapSlot() {
        int slot1 = (int) slot1Setting.getValue() - 1;
        int slot2 = (int) slot2Setting.getValue() - 1;
        slot = slot == slot1 ? slot2 : slot1;
        Minecraft.getMinecraft().thePlayer.inventory.currentItem = slot;
    }
}
