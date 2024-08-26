package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyboardUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void resetKeybindings(KeyBinding... keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            setPressed(keyBinding, false);
        }
    }

    private static void setPressed(KeyBinding keyBinding, boolean pressed) {
        // Minecraft 1.8.9 doesn't allow direct access; ensure you have the correct access method or use reflection.
        try {
            java.lang.reflect.Field field = KeyBinding.class.getDeclaredField("pressed");
            field.setAccessible(true);
            field.setBoolean(keyBinding, pressed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
