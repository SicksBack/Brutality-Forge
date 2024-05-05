package com.example.examplemod.modules;

import net.minecraftforge.common.MinecraftForge;

public class Module {
    public String name;
    public String description;
    private int key;
    private Category category;
    public boolean toggled;

    // Main constructor for the module - holds all the info about the module
    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.key = 0;
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
    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
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
