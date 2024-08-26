package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.module.impl.render.interfaces.PrimeCheatsInterface;

public class Interface extends Module {
    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Theme", this, "PrimeCheats", new String[]{"PrimeCheats"});
    private final PrimeCheatsInterface primeCheatsInterface;

    public Interface() {
        super("Interface", "Shows information about Brutality", Category.RENDER);
        addSettings(modeSetting);
        this.primeCheatsInterface = new PrimeCheatsInterface(Minecraft.getMinecraft());
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (modeSetting.getValue().equals("PrimeCheats")) {
            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            primeCheatsInterface.render(sr); // Pass ScaledResolution instance to render method
        }
    }
}
