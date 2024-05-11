package org.brutality.modules.modules.render;

import org.brutality.modules.Module;
import org.brutality.modules.Category;
import org.lwjgl.input.Keyboard;

public class TestModule extends Module {

    public TestModule() {
        super("Test module", "Test module to show usage", Category.RENDER);
        // Set keybinding
        this.setKey(Keyboard.KEY_B);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
