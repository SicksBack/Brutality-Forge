package org.brutality.module.impl.player;

import org.brutality.events.Event;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

public class AutoSpawn extends Module {

    public AutoSpawn() {
        super("AutoSpawn", "Automatically types /spawn in chat", Category.PLAYER);
        setKey(Keyboard.KEY_RIGHT);
    }


    public void onEnable() {
        super.onEnable();
        if (mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/spawn");
            toggle();
        }
    }


    public void onEvent(Event<Event> e) {
        if (e instanceof EventUpdate) {
            if (mc.thePlayer == null || mc.theWorld == null) {
                return;
            }
            // Optionally, add any other logic here if needed
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
