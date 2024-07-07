package org.brutality.settings.impl;

import lombok.Getter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

import java.awt.*;

@Getter
public class ColorSetting extends Setting {
    private Color color;
    private ColorPicker picker;

    public ColorSetting(String name, Module parent, Color defaultValue) {
        super(name, parent);
        this.color = defaultValue;
        this.picker = new ColorPicker(50, getColor(), this::setColor);
    }

    public ColorSetting(String name, Mode<?> parent, Color defaultValue) {
        super(name, parent);
        this.color = defaultValue;
        this.picker = new ColorPicker(50, getColor(), this::setColor);
    }

    public void setColor(Color color) {
        this.color = color;
        mm.updateSettings(this);
    }

}
