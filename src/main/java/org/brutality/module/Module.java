package org.brutality.module;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.brutality.settings.Setting;
import org.brutality.utils.interfaces.MC;
import org.brutality.utils.interfaces.MM;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Module implements MM, MC {
    public final String name;
    public final String description;
    private KeyBinding key;
    private final Category category;
    public boolean toggled;
    private final List<Setting> settings = new ArrayList<>();

    // Main constructor for the module - holds all the info about the module
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.key = null;
        this.category = category;
        this.toggled = false;
        mm.add(this);
    }

    public void addSettings(Setting... settings) {
        for (Setting setting : settings) {
            this.settings.add(setting);
        }
    }

    // TODO - Implement saving of this keybind on shutdown
    public void setKey(int newKey) {
        // Create a new keybinding
        key = new KeyBinding(this.name, newKey, "Brutality Client");
        // Register it so it will appear in the minecraft settings page
        ClientRegistry.registerKeyBinding(key);
    }

    // Switches the toggle
    public void toggle() {
        this.toggled = !this.toggled;

        if (this.toggled) {
            this.onEnable();
        } else {
            this.onDisable();
        }
    }

    // Registers the module onto the event bus
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Unregisters the module from the event bus
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    public void updateSettings(Setting s) {}
}
