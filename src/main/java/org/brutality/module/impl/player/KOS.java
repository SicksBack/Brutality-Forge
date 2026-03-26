package org.brutality.module.impl.player;

import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.KOSManager;

public class KOS extends Module {

    public KOS() {
        super("KOS", "Manages KOS targets.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        KOSManager.setEnabled(true); // Enable KOS system when the module is toggled on
    }

    @Override
    public void onDisable() {
        super.onDisable();
        KOSManager.setEnabled(false); // Disable KOS system when the module is toggled off
    }
}
