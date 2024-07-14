package org.brutality.module.impl.move;

import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "Makes you sprint at all times", Category.MOVEMENT);
    }

    public void onUpdate() {
        if(this.isToggled()) {
            mc.thePlayer.setSprinting(true);
        }
    }

    public void onDisable() {
        mc.thePlayer.setSprinting(false);
        super.onDisable();
    }
}