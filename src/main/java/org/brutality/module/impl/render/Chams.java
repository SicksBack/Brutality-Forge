package org.brutality.module.impl.render;

import java.util.HashSet;

import net.minecraft.client.Minecraft;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Chams extends Module {
    private final BooleanSetting ignoreBots;
    private final HashSet<Entity> entities = new HashSet<>();

    public Chams() {
        super("Chams", "Render players with chams effect", Category.RENDER);
        this.ignoreBots = new BooleanSetting("Ignore bots", this, false);
        this.addSettings(ignoreBots);
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            return;
        }
        if (this.ignoreBots.isEnabled()) { // Use isEnabled() method
            this.entities.add(event.entity);
        }
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glPolygonOffset(1.0f, -1000000.0f);
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.entity == Minecraft.getMinecraft().thePlayer) {
            return;
        }
        if (this.ignoreBots.isEnabled()) { // Use isEnabled() method
            if (!this.entities.contains(event.entity)) {
                return;
            }
            this.entities.remove(event.entity);
        }
        GL11.glPolygonOffset(1.0f, 1000000.0f);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
    }

    @Override
    public void onDisable() {
        this.entities.clear();
    }
}
