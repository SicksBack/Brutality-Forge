package org.brutality.handlers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;

public class KeyBindingHandler {

    public void handleKeyBindings() {
        Minecraft mc = Minecraft.getMinecraft();
        for (Module module : ModuleManager.getInstance().getModules()) {
            KeyBinding keyBinding = module.getKeyBinding();
            if (keyBinding != null && keyBinding.isKeyDown()) {
                module.toggle();
            }
        }
    }
}
