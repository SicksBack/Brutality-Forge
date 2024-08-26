package org.brutality.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderEvent extends Event {
    private final Minecraft mc;

    public RenderEvent(Minecraft mc) {
        this.mc = mc;
    }

    public Minecraft getMinecraft() {
        return mc;
    }
}
