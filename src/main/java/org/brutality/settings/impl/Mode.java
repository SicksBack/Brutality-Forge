package org.brutality.settings.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.minecraftforge.common.MinecraftForge;
import org.brutality.settings.Setting;
import org.brutality.utils.interfaces.MM;
import org.brutality.utils.interfaces.SM;
import org.brutality.utils.interfaces.MC;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class Mode<T> implements MM, SM, MC {

    private final String name;
    private ModeSetting modeSettingParent;
    private final T parent;

    public void updateSettings(Setting setting) {}

    public void onEnable() {}
    public void onDisable() {}
    public abstract void setup();

    public final void registerEvents() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public final void unregisterEvents() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public String getName() {
        return this.name;
    }

    public ModeSetting getModeSettingParent() {
        return this.modeSettingParent;
    }

    public void setModeSettingParent(ModeSetting modeSettingParent) {
        this.modeSettingParent = modeSettingParent;
    }

    public T getParent() {
        return this.parent;
    }
}
