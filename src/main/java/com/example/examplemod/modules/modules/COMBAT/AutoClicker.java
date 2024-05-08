package com.example.examplemod.modules.modules.COMBAT;

import brutality.client.events.Event;
import brutality.client.events.listeners.EventUpdate;
import brutality.client.modules.Module;
import brutality.client.settings.BooleanSetting;
import brutality.client.settings.NumberSetting;
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {

    private NumberSetting cpsSetting = new NumberSetting("CPS", 10, 0, 20, 1);
    private BooleanSetting holdLeftClick = new BooleanSetting("Hold Left Click", true);
    private long lastClickTime;
    private long delay;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you", 0, Module.Category.COMBAT, false);
        addSettings(cpsSetting, holdLeftClick);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            int cps = cpsSetting.getValue().intValue();
            delay = 1000 / cps;
            int button = 0; // Left mouse button
            if (holdLeftClick.isEnabled()) {
                // Check if the left mouse button is held down
                if (Mouse.isButtonDown(button)) {
                    if (System.currentTimeMillis() - lastClickTime >= delay) {
                        // Perform a mouse click
                        Mouse.clickEvent(0);
                        lastClickTime = System.currentTimeMillis();
                    }
                }
            } else {
                // If hold left click option is disabled, auto-click without the need to hold down left click
                if (System.currentTimeMillis() - lastClickTime >= delay) {
                    // Perform a mouse click
                    Mouse.clickEvent(0);
                    lastClickTime = System.currentTimeMillis();
                }
            }
        }
    }
}
