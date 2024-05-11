package org.brutality.module;

import org.brutality.module.impl.move.SprintModule;
import org.brutality.module.impl.render.ArrayListModule;
import org.brutality.module.impl.render.HUDModule;
import org.brutality.module.impl.render.TestModule;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class ModuleManager extends ArrayList<Module> {
    public void init() {
        new SprintModule();
        new TestModule();
        new HUDModule();
        new ArrayListModule();
    }

    public Module getModuleByName(String name) {
        for (Module mod : this) {
            if (mod.getName().equals(name)) {
                return mod;
            }
        }
        return null;
    }
}
