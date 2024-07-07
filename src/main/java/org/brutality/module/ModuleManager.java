package org.brutality.module;

import org.brutality.module.impl.combat.KillauraModule;
import org.brutality.module.impl.move.SprintModule;
import org.brutality.module.impl.pit.PitSwap;
import org.brutality.module.impl.render.*;
import lombok.Getter;
import org.brutality.settings.Setting;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class ModuleManager extends ArrayList<Module> {
    public void init() {
        new SprintModule();
        new HUDModule();
        new ArrayListModule();
        new ClickGuiModule();
        new KillauraModule(); // Register the KillAura module
        new NameTagModule();
        new PitSwap();
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
