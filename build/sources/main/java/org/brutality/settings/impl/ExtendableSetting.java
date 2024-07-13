package org.brutality.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
@Setter
public class ExtendableSetting extends Setting {
    private Setting[] subSettings;
    private boolean expanded = false;
    public ExtendableSetting(String name, Module parent, Setting... subSettings) {
        super(name, parent);
        this.subSettings = subSettings;
        for (Setting setting : subSettings) {
            sm.settings.remove(setting);
        }
    }

    public ExtendableSetting(String name, Mode<?> parent, Setting... subSettings) {
        super(name, parent);
        this.subSettings = subSettings;
        for (Setting setting : subSettings) {
            sm.settings.remove(setting);
        }
    }
}
