package org.brutality.events;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class EventManager {
    public static final EventBus EVENT_BUS = new EventBus();

    public static void register(Object object) {
        MinecraftForge.EVENT_BUS.register(object);
    }

    public static void unregister(Object object) {
        MinecraftForge.EVENT_BUS.unregister(object);
    }
}
