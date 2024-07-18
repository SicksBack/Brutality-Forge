package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.ReflectionUtils;

public class FastPlace extends Module {

    private final NumberSetting tickDelay = new NumberSetting("Tick delay", this, 1, 1, 3, 1);
    private final BooleanSetting blocksOnly = new BooleanSetting("Blocks only", this, true);
    private final BooleanSetting pitchCheck = new BooleanSetting("Pitch check", this, false);

    private Minecraft mc = Minecraft.getMinecraft();
    private int delay;

    public FastPlace() {
        super("FastPlace", "Places blocks faster", Category.PLAYER);
        this.addSettings(tickDelay, blocksOnly, pitchCheck);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (blocksOnly.isEnabled() && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock)) {
                return;
            }

            if (pitchCheck.isEnabled() && mc.thePlayer.rotationPitch > 90) {
                return;
            }

            if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) {
                ReflectionUtils.setPrivateField(mc, "rightClickDelayTimer", delay);
            } else {
                ReflectionUtils.setPrivateField(mc, "rightClickDelayTimer", 6);
            }
        }
    }


    public void onEnable() {
        delay = (int) tickDelay.getValue();
    }


    public void onDisable() {
        ReflectionUtils.setPrivateField(mc, "rightClickDelayTimer", 6); // Reset to default
    }
}
