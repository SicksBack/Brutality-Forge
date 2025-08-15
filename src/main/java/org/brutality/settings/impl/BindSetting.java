package org.brutality.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.lwjgl.input.Keyboard;

@Getter
@Setter
public class BindSetting extends Setting {
    private int keyCode;
    private boolean listening = false;

    public BindSetting(String name, Module parent, int defaultKeyCode) {
        super(name, parent);
        this.keyCode = defaultKeyCode;
    }

    public BindSetting(String name, Mode<?> parent, int defaultKeyCode) {
        super(name, parent);
        this.keyCode = defaultKeyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        this.listening = false;
        mm.updateSettings(this);
    }

    public String getKeyName() {
        if (keyCode == Keyboard.KEY_NONE) {
            return "None";
        }
        return Keyboard.getKeyName(keyCode);
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    public int getKeyCode() {
        return keyCode;
    }
}