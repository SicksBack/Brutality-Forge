package org.brutality.module.impl.combat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;

public class AutoClicker extends Module {
    private final NumberSetting cps = new NumberSetting("CPS", this, 10, 1, 20, 1); // Clicks per second
    private long lastClickTime = 0;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks at a specified CPS rate.", Category.COMBAT);
        addSettings(cps);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        long currentTime = System.currentTimeMillis();
        long clickDelay = (long) (1000 / cps.getValue()); // Calculate delay between clicks

        if (currentTime - lastClickTime >= clickDelay) {
            mc.thePlayer.swingItem(); // Simulate swing
            mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit); // Attack the entity under the crosshair
            lastClickTime = currentTime; // Update the last click time
        }
    }
}
