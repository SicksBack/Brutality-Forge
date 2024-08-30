package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.brutality.module.impl.render.huds.PrimeCheatsHUD;


import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HUD extends Module {
    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Mode", this, "PrimeCheats", new String[]{"PrimeCheats"});

    private CustomFontRenderer smallFontRenderer;
    private CustomFontRenderer bigFontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();
    private PrimeCheatsHUD primeCheatsHUD;

    private final List<Color> colors = Arrays.asList(Color.GREEN, Color.YELLOW, Color.RED, new Color(255, 105, 180), Color.CYAN);

    public HUD() {
        super("HUD", "Shows a list of toggled modules", Category.RENDER);

        // Initialize font renderers without using non-existent methods
        smallFontRenderer = new CustomFontRenderer(new java.awt.Font("Arial", java.awt.Font.PLAIN, 18), true, true);
        bigFontRenderer = smallFontRenderer; // Use the same font renderer for both

        primeCheatsHUD = new PrimeCheatsHUD(mc, smallFontRenderer, bigFontRenderer, modeSetting, colors,
                870, 0, 90, 10); // Updated dimensions and position
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (modeSetting.getValue().equals("PrimeCheats")) {
            primeCheatsHUD.setPosition(870, 0); // Set fixed position
            primeCheatsHUD.setBoxDimensions(90, 10); // Set fixed dimensions
            primeCheatsHUD.render(event, getSortedModules());
        }

        // Handle dragging in chat screen
        if (mc.currentScreen instanceof GuiChat) {
            primeCheatsHUD.handleDragging();
        }
    }

    private List<Module> getSortedModules() {
        return mm.stream()
                .filter(Module::isToggled)
                .sorted((o1, o2) -> Integer.compare(o2.getName().length(), o1.getName().length())) // Longest names first
                .collect(Collectors.toList());
    }
}
