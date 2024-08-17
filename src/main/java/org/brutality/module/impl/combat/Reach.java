package org.brutality.module.impl.combat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class Reach extends Module {
    private final NumberSetting reach = new NumberSetting("Reach", this, 4.0, 1.0, 10.0, 1); // Reach distance

    public Reach() {
        super("Reach", "Adjusts the reach distance for interacting with entities.", Category.COMBAT);
        addSettings(reach);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Update reach distance
        // For now, the reach distance is not actively applied here; this is handled where needed
    }

    public double getReach() {
        return reach.getValue();
    }
}
