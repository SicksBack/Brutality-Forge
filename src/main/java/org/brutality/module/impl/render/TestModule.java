package org.brutality.module.impl.render;

import org.brutality.module.Module;
import org.brutality.module.Category;
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
