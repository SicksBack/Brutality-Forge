package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.events.PreMotionEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Field;

public class FastPlace extends Module {
    public final NumberSetting tickDelay;
    public final BooleanSetting blocksOnly, pitchCheck;

    public FastPlace() {
        super("FastPlace", "Changes the right click delay timer", Category.PLAYER);
        this.tickDelay = new NumberSetting("Tick delay", this, 1, 0, 4, 1);
        this.blocksOnly = new BooleanSetting("Blocks only", this, true);
        this.pitchCheck = new BooleanSetting("Pitch check", this, false);
        this.addSettings(tickDelay, blocksOnly, pitchCheck);
    }

    @SubscribeEvent
    public void onPreMotion(PreMotionEvent event) {
        if (blocksOnly.isEnabled() && !(mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock))
            return;

        int delayValue = (int) tickDelay.getValue();

        try {
            Field rightClickDelayTimerField = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            rightClickDelayTimerField.setAccessible(true);

            if (delayValue == 0) {
                rightClickDelayTimerField.setInt(mc, 0);
            } else {
                int currentDelay = rightClickDelayTimerField.getInt(mc);
                if (currentDelay > delayValue) {
                    rightClickDelayTimerField.setInt(mc, delayValue);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
