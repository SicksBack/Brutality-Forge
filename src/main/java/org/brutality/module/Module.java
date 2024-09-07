package org.brutality.module;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.brutality.events.Event;
import org.brutality.module.impl.render.Notifications;
import org.brutality.settings.Setting;
import org.brutality.utils.interfaces.MC;
import org.brutality.utils.interfaces.MM;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public abstract class Module implements MM, MC {
    private final String name;
    private final String description;
    private KeyBinding keyBinding;
    private final Category category;
    private boolean toggled;
    private final List<Setting> settings = new ArrayList<>();

    // Main constructor for the module - holds all the info about the module
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.toggled = false;
        mm.add(this);
    }

    public void addSettings(Setting... settings) {
        for (Setting setting : settings) {
            this.settings.add(setting);
        }
    }

    public void setKey(int keyCode) {
        Minecraft mc = Minecraft.getMinecraft();

        if (this.keyBinding != null) {
            // Unregister existing key binding if it exists
            KeyBinding[] keyBindings = mc.gameSettings.keyBindings;
            keyBindings = removeKeyBinding(keyBindings, this.keyBinding);
        }

        // Create and register new KeyBinding
        this.keyBinding = new KeyBinding(this.name, keyCode, "Brutality Client");
        KeyBinding[] keyBindings = mc.gameSettings.keyBindings;
        mc.gameSettings.keyBindings = addKeyBinding(keyBindings, this.keyBinding);

        // Register the new KeyBinding
        ClientRegistry.registerKeyBinding(this.keyBinding);
        mc.gameSettings.saveOptions();
    }

    private KeyBinding[] addKeyBinding(KeyBinding[] keyBindings, KeyBinding newKeyBinding) {
        KeyBinding[] newKeyBindings = new KeyBinding[keyBindings.length + 1];
        System.arraycopy(keyBindings, 0, newKeyBindings, 0, keyBindings.length);
        newKeyBindings[keyBindings.length] = newKeyBinding;
        return newKeyBindings;
    }

    private KeyBinding[] removeKeyBinding(KeyBinding[] keyBindings, KeyBinding toRemove) {
        int indexToRemove = -1;
        for (int i = 0; i < keyBindings.length; i++) {
            if (keyBindings[i] == toRemove) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove == -1) {
            return keyBindings;
        }

        KeyBinding[] newKeyBindings = new KeyBinding[keyBindings.length - 1];
        System.arraycopy(keyBindings, 0, newKeyBindings, 0, indexToRemove);
        System.arraycopy(keyBindings, indexToRemove + 1, newKeyBindings, indexToRemove, keyBindings.length - indexToRemove - 1);
        return newKeyBindings;
    }

    public KeyBinding getKey() {
        return this.keyBinding;
    }

    public void toggle() {
        this.toggled = !this.toggled;
        if (this.toggled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        Notifications.sendNotification(this.name + " \u00A7aEnabled\u00A7f."); // Green color code
    }

    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        Notifications.sendNotification(this.name + " \u00A7cDisabled\u00A7f."); // Red color code
    }

    public void updateSettings(Setting s) {
    }

    public void onEvent() {
    }
}
