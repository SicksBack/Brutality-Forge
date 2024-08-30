package org.brutality.utils;

import net.minecraft.client.Minecraft;

import java.lang.reflect.Field;

public class ReflectionUtils {
    public static Field rightClickDelayTimerField;

    static {
        try {
            rightClickDelayTimerField = Minecraft.class.getDeclaredField("rightClickDelayTimer");
            rightClickDelayTimerField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
