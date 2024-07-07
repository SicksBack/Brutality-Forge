package org.brutality.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
@Setter
public class MultiBooleanSetting extends Setting {
    private BooleanSetting[] settings;
    public MultiBooleanSetting(String name, Module parent, BooleanSetting... settings) {
        super(name, parent);
        this.settings = settings;
        for (BooleanSetting setting : settings) {
            sm.settings.remove(setting);
        }
    }

    public MultiBooleanSetting(String name, Mode<?> parent, BooleanSetting... settings) {
        super(name, parent);
        this.settings = settings;
        for (BooleanSetting setting : settings) {
            sm.settings.remove(setting);
        }
    }
}
