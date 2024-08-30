package org.brutality.module.impl.combat;

import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;

public class Reach extends Module {
    private final NumberSetting reach = new NumberSetting("Reach", this, 4.0, 1.0, 6.0, 1);

    public Reach() {
        super("Reach", "Extends your reach distance.", Category.COMBAT);
        addSettings(reach);
        setKey(Keyboard.KEY_L);
    }

    public double getReachDistance() {
        return reach.getValue();
    }
}
