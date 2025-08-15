package org.brutality.module.impl.movement;

import net.minecraft.entity.Entity;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class KeepSprint extends Module {

    public static NumberSetting slow = new NumberSetting("Slow", null, 40, 0, 100, 1);

    public KeepSprint() {
        super("KeepSprint", "Keeps you sprinting even when hitting players", Category.MOVEMENT);
        this.addSettings(slow);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    public static void keepSprint(Entity entity) {
        float mult = (100.0f - (float) slow.getValue()) / 100.0f;
        mc.thePlayer.motionX *= mult;
        mc.thePlayer.motionZ *= mult;
    }
}
