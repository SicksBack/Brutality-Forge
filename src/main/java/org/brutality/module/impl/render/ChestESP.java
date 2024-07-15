package org.brutality.module.impl.render;

import org.brutality.events.EventTarget;
import org.brutality.events.listeners.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class ChestESP extends Module {

    private final BooleanSetting fill = new BooleanSetting("Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Outline", this, true);
    private final ColorSetting color = new ColorSetting("Color", this, Color.RED);
    private final NumberSetting lineWidth = new NumberSetting("Line Width", this, 1.0, 1.0, 5.0, 1);

    public ChestESP() {
        super("ChestESP", "Highlights chests", Category.RENDER);
        addSettings(fill, outline, color, lineWidth);
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        for (Object o : mc.theWorld.loadedTileEntityList) {
            if (o instanceof TileEntityChest) {
                TileEntityChest chest = (TileEntityChest) o;
                double x = chest.getPos().getX() - mc.getRenderManager().viewerPosX;
                double y = chest.getPos().getY() - mc.getRenderManager().viewerPosY;
                double z = chest.getPos().getZ() - mc.getRenderManager().viewerPosZ;

                if (fill.isEnabled()) {
                    RenderUtils.drawFilledBox(x, y, z, x + 1, y + 1, z + 1, color.getColor().getRed() / 255.0f, color.getColor().getGreen() / 255.0f, color.getColor().getBlue() / 255.0f, 0.5f);
                }

                if (outline.isEnabled()) {
                    RenderUtils.drawBox(x, y, z, x + 1, y + 1, z + 1, color.getColor().getRed() / 255.0f, color.getColor().getGreen() / 255.0f, color.getColor().getBlue() / 255.0f, 1.0f);
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        onRender(new EventRender3D(event.partialTicks));
    }
}
