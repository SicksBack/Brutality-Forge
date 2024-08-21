package org.brutality.utils;

import org.brutality.events.listeners.EventRender3D;

public class TargetUtil {
    private static float partialTicks = 0.0F;

    public static void setPartialTicks(float partialTicks) {
        TargetUtil.partialTicks = partialTicks;
    }

    public static float getPartialTicks() {
        return partialTicks;
    }
}
