package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
public class SimpleModeSetting extends Setting {
    private final String[] options;
    private String selected;

    public SimpleModeSetting(String name, Module parent, String defaultValue, String[] options) {
        super(name, parent);
        this.selected = defaultValue;
        this.options = options;
    }

    public SimpleModeSetting(String name, Mode<?> parent, String defaultValue, String[] options) {
        super(name, parent);
        this.selected = defaultValue;
        this.options = options;
    }

    public void setSelected(String selected) {
        this.selected = selected;
        mm.updateSettings(this);
    }

    public boolean is(String checkFor) {
        return getSelected().equalsIgnoreCase(checkFor);
    }

    public String getValue() {
        return this.selected;
    }
}
