package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

@Getter
public class InputSetting extends Setting {
    private String content;
    public InputSetting(String name, Module parent, String defaultValue) {
        super(name, parent);
        this.content = defaultValue;
    }

    public InputSetting(String name, Mode<?> parent, String defaultValue) {
        super(name, parent);
        this.content = defaultValue;
    }

    public void setContent(String content) {
        this.content = content;
        mm.updateSettings(this);
    }
}
