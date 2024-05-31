package org.brutality.module.impl.move;

import org.brutality.module.Module;
import org.brutality.module.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

// not tested, possibly detected
public class RageSprintModule extends Module {

    public SprintModule() {SprintModule.java
        super("RageSprint", "Makes the player sprint in all directions", Category.MOVEMENT);
        // Set keybinding
        this.setKey(Keyboard.KEY_P);
    }

    public void onDisable() {
        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        mc.thePlayer.setSprinting(true);
    }
}