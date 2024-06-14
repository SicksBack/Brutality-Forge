package org.brutality.module;

import org.brutality.module.impl.move.SprintModule;
import org.brutality.module.impl.render.ArrayListModule;
import org.brutality.module.impl.render.ClickGuiModule;
import org.brutality.module.impl.render.HUDModule;
import org.brutality.module.impl.render.TestModule;
import lombok.Getter;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.utils.exceptions.NotImplementedException;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class ModuleManager extends ArrayList<Module> {
    public void init() {
        new SprintModule();
        new TestModule();
        new HUDModule();
        new ArrayListModule();
        new ClickGuiModule();
    }

    public void updateSettings(Setting setting) {
        forEach(module -> module.updateSettings(setting));
    }

    public <V extends Module> V getModuleByClass(Class<V> clazz) {
        Module module = stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        return module == null ? null : clazz.cast(module);
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        return stream().filter(module -> module.getCategory().equals(category)).collect(Collectors.toCollection(ArrayList::new));
    }
}
