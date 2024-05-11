package org.brutality.modules.modules.render;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.modules.Category;
import org.brutality.modules.Module;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD", "Shows the ingame Interface", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            mc.fontRendererObj.drawString("Brutality", 10, 10, -1);
        }
    }
}
