package org.brutality.handlers;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;

public class ClientTickHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (Module module : ModuleManager.getInstance().getModules()) {
                if (module.getKeyBinding() != null && module.getKeyBinding().isKeyDown()) {
                    module.toggle();
                }
            }
        }
    }
}
