package org.brutality.module.impl.render;

import org.brutality.BrutalityClient;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.ColorSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class ClickGuiModule extends Module {
    public ColorSetting setting = new ColorSetting("Accent Color", this, new Color(120, 255, 255));

    public ClickGuiModule() {
        super("ClickGui", "Opens the ClickGui", Category.RENDER);

        setKey(Keyboard.KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(BrutalityClient.INSTANCE.clickGui);
        this.toggle();
    }
}
