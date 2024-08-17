package org.brutality.module.impl.movement;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;
import org.brutality.utils.interfaces.MC;

public class KeepSprint extends Module implements MC {

    private static final int KEY_BINDING = Keyboard.KEY_R; // Bind to the 'R' key

    public KeepSprint() {
        super("KeepSprint", "Prevents you from stopping sprinting when attacking.", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        // Initialize or reset the module state when enabled
    }

    @Override
    public void onDisable() {
        super.onDisable();
        // Cleanup or reset the module state when disabled
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.PlayerTickEvent event) {
        if (!this.isToggled() || mc.thePlayer == null) {
            return;
        }

        // Check if the 'R' key is pressed
        if (Keyboard.isKeyDown(KEY_BINDING)) {
            keepSprint();
        }
    }

    private void keepSprint() {
        if (mc.thePlayer.isSprinting()) {
            // Ensure the player continues to sprint regardless of conditions
            mc.thePlayer.setSprinting(true);
        }
    }
}
