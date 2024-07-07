package org.brutality.module.impl.render;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import java.util.stream.Collectors;

public class ArrayListModule extends Module {
    public final SimpleModeSetting side = new SimpleModeSetting("Side", this, "Left", new String[]{"Left", "Right"});

    public ArrayListModule() {
        super("ArrayList", "Shows a list of toggled modules", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (side.is("Left")) {
            int y = mc.fontRendererObj.FONT_HEIGHT + 5;
            for (Module m : mm.stream().sorted((o1, o2) -> Integer.compare(mc.fontRendererObj.getStringWidth(o2.getName()), mc.fontRendererObj.getStringWidth(o1.getName()))).collect(Collectors.toList())) {
                if (m.isToggled()) {
                    mc.fontRendererObj.drawStringWithShadow(m.getName(), 5, y, -1);
                    y += mc.fontRendererObj.FONT_HEIGHT;
                }
            }
        } else if (side.is("Right")) {
            int y = 5;
            for (Module m : mm.stream().sorted((o1, o2) -> Integer.compare(mc.fontRendererObj.getStringWidth(o2.getName()), mc.fontRendererObj.getStringWidth(o1.getName()))).collect(Collectors.toList())) {
                if (m.isToggled()) {
                    mc.fontRendererObj.drawStringWithShadow(m.getName(), event.resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(m.getName()) - 5, y, -1);
                    y += mc.fontRendererObj.FONT_HEIGHT;
                }
            }
        }
    }
}
