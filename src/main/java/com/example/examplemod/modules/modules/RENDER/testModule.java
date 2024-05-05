package com.example.examplemod.modules.modules.RENDER;

import com.example.examplemod.modules.Module;
import com.example.examplemod.modules.Category;

public class testModule extends Module {

    public testModule() {
        super("Test module", "Test module to show usage", Category.RENDER);
        this.setKey(54);
    }

    public void onEnable() {
        super.onEnable();
        System.out.println("Test module says -> Hello, World!");
    }
}
