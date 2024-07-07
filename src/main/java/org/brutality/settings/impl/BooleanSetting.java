package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
public class BooleanSetting extends Setting {
    private boolean enabled;

    public BooleanSetting(String name, Module parent, boolean defaultValue) {
        super(name, parent);
        this.enabled = defaultValue;
    }

    public BooleanSetting(String name, Mode<?> parent, boolean defaultValue) {
        super(name, parent);
        this.enabled = defaultValue;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        mm.updateSettings(this);
    }
}
