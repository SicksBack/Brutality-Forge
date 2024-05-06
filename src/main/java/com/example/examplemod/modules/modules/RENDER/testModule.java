package com.example.examplemod.modules.modules.RENDER;

import com.example.examplemod.modules.Module;
import com.example.examplemod.modules.Category;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class testModule extends Module {
    private KeyBinding keyBindings;
    public testModule() {
        super("Test module", "Test module to show usage", Category.RENDER);
        // Set keybinding
        this.setKey(Keyboard.KEY_B);
    }

    public void onEnable() {
        super.onEnable();
        System.out.println("Test module says -> Hello, World!");
    }
}
