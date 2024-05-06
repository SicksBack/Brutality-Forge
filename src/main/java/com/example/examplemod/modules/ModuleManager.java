package com.example.examplemod.modules;

import com.example.examplemod.modules.modules.MOVEMENT.sprint;
import com.example.examplemod.modules.modules.RENDER.testModule;

import java.util.ArrayList;

public class ModuleManager {
    public static ArrayList<Module> modules;

    // Initialises the modules
    public ModuleManager() {
        this.modules = new ArrayList<Module>();
        this.modules.add(new testModule());
        this.modules.add(new sprint());
    }

    // Returns the list of modules
    public ArrayList<Module> getModuleList() {
        return this.modules;
    }
}
