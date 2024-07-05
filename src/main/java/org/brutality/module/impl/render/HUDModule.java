package org.brutality.module.impl.render;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD", "Shows the ingame Interface", Category.RENDER);

        setKey(Keyboard.KEY_P);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        mc.fontRendererObj.drawString("Brutality",5,5, -1);
    }
}
