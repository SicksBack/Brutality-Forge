package com.example.examplemod.modules;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class Module {
    public String name;
    public String description;
    private KeyBinding key;
    private Category category;
    public boolean toggled;

    // Main constructor for the module - holds all the info about the module
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.key = null;
        this.category = category;
        this.toggled = false;
    }

    // Returns the description
    public String getDescription() {
        return this.description;
    }

    // Sets the description
    public void setDescription(String description) {
        this.description = description;
    }

    // Returns the key bind
    public KeyBinding getKey() {
        return this.key;
    }

    // TODO - Implement saving of this keybind on shutdown
    public void setKey(int newKey) {
        // Create a new keybinding
        key = new KeyBinding(this.name, newKey, "Brutality Client");
        // Register it so it will appear in the minecraft settings page
        ClientRegistry.registerKeyBinding(key);
    }

    // Returns if the module is toggled or not
    public boolean isToggled() {
        return this.toggled;
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

    // Gets the module name
    public String getName() {
        return this.name;
    }

    // Gets the module category
    public Category getCategory() {
        return this.category;
    }
}
