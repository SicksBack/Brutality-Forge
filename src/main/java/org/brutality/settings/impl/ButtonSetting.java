package org.brutality.settings.impl;

import com.google.gson.JsonObject;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

public class ButtonSetting extends Setting {
    private String name;
    public boolean isEnabled;
    public boolean isMethodButton;
    private Runnable method;

    public ButtonSetting(String name, Module parent, boolean isEnabled) {
        super(name, parent);
        this.name = name;
        this.isEnabled = isEnabled;
        this.isMethodButton = false;
    }

    public ButtonSetting(String name, Module parent, Runnable method) {
        super(name, parent);
        this.name = name;
        this.isEnabled = false;
        this.isMethodButton = true;
        this.method = method;
    }

    public void runMethod() {
        if (this.method != null) {
            this.method.run();
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    public boolean isToggled() {
        return this.isEnabled;
    }

    public void toggle() {
        this.isEnabled = !this.isEnabled;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    public void setEnabled(boolean b) {
        this.isEnabled = b;
    }

    public void loadProfile(JsonObject data) {
        if (data.has(this.getName()) && data.get(this.getName()).isJsonPrimitive() && !this.isMethodButton) {
            boolean booleanValue = data.getAsJsonPrimitive(this.getName()).getAsBoolean();
            this.setEnabled(booleanValue);
        }
    }
}
