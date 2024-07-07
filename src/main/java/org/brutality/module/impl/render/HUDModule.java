package org.brutality.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.api.EventHandler;
import org.brutality.api.events.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class HUDModule extends Module {
    public HUDModule() {
        super("HUD", "Shows the ingame Interface", Category.RENDER);

        setKey(Keyboard.KEY_P);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        mc.fontRendererObj.drawString("Brutality",5,5, -1);
        @EventHandler
        public void onRender2D(EventRender2D eventRender2D) {
            this.mc.fontRendererObj.drawStringWithShadow("Brutality", 4.5f, 3.5f, new Color(0xF1F1F1).getRGB());
