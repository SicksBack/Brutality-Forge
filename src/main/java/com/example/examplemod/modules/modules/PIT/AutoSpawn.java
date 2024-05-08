package com.example.examplemod.modules.modules.PIT;

import com.example.examplemod.events.Event;
import com.example.examplemod.events.listeners.EventKey;
import com.example.examplemod.modules.Module;
import com.example.examplemod.settings.KeybindSetting;
import com.example.examplemod.utils.Wrapper;
import org.lwjgl.input.Keyboard;

public class SpawnTeleport extends Module {

    private KeybindSetting keybind = new KeybindSetting("Teleport Key", Keyboard.KEY_P);

    public SpawnTeleport() {
        super("SpawnTeleport", "Teleports you to spawn", Category.MISC);
        addSettings(keybind);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventKey) {
            EventKey keyEvent = (EventKey) event;
            if (keyEvent.getKey() == keybind.getKey() && keyEvent.isPressed()) {
                sendSpawnCommand();
            }
        }
    }

    private void sendSpawnCommand() {
        Wrapper.getMinecraft().thePlayer.sendChatMessage("/spawn");
    }
}
