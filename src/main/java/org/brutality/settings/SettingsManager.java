package org.brutality.settings;

import lombok.Getter;
import lombok.Setter;
import org.brutality.module.Module;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Getter
@Setter
public class SettingsManager {

    public ArrayList<Setting> settings = new ArrayList<>();

    public void add(Setting setting) {
        settings.add(setting);
    }

    public ArrayList<Setting> getValuesByMod(Module module) {
        return settings.stream().filter(setting -> setting.getParent() == module).collect(Collectors.toCollection(ArrayList::new));
    }
}
