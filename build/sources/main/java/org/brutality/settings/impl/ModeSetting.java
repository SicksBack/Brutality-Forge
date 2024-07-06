package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Getter
public class ModeSetting extends Setting {
    private final Mode<?>[] options;
    private Mode<?> selected;
    public HashMap<Mode<?>, ArrayList<Setting>> settings = new HashMap<>(); // Bitte lass meine Eltern am leben

    public ModeSetting(String name, Module parent, Mode<?> defaultValue, Mode<?>[] options) {
        super(name, parent);
        this.selected = defaultValue;
        this.options = options;
        for (Mode<?> option : options) {
            option.setModeSettingParent(this);
            settings.put(option, new ArrayList<>());
            option.setup();
        }
    }

    public ModeSetting(String name, Module parent, Mode<?>[] options) {
        super(name, parent);
        this.selected = options[0];
        this.options = options;
        for (Mode<?> option : options) {
            option.setModeSettingParent(this);
            settings.put(option, new ArrayList<>());
            option.setup();
        }
    }

    public ModeSetting(String name, Mode<?> parent, Mode<?> defaultValue, Mode<?>[] options) {
        super(name, parent);
        this.selected = defaultValue;
        this.options = options;
        for (Mode<?> option : options) {
            option.setModeSettingParent(this);
            settings.put(option, new ArrayList<>());
            option.setup();
        }
    }

    public ModeSetting(String name, Mode<?> parent, Mode<?>[] options) {
        super(name, parent);
        this.selected = options[0];
        this.options = options;
        for (Mode<?> option : options) {
            option.setModeSettingParent(this);
            settings.put(option, new ArrayList<>());
            option.setup();
        }
    }

    public Mode<?> getByName(String name) {
        return Arrays.stream(options).filter(mode -> mode.getName().equals(name)).findFirst().get();
    }

    public String[] getNames() {
        return Arrays.stream(options).map(Mode::getName).toArray(String[]::new);
    }

    public void updateParentToggled(boolean newState) {
        try {
            if (newState) {
                selected.onEnable();
                selected.registerEvents();
            } else {
                selected.unregisterEvents();
                selected.onDisable();
            }
        } catch (NullPointerException e) {}
    }

    public void setSelected(Mode<?> selected) {
        if (selected != this.selected) {
            if (this.getParent().isToggled()) {
                this.selected.unregisterEvents();
                this.selected.onDisable();
                selected.onEnable();
                selected.registerEvents();
            }
            this.selected = selected;
            mm.updateSettings(this);
        }
    }
}
