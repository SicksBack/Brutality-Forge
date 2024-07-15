package org.brutality.module.impl.render;

import org.brutality.events.EventTarget;
import org.brutality.events.listeners.EventChat;
import org.brutality.events.listeners.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.RenderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.Color;

public class SewerESP extends Module {

    private final BooleanSetting fill = new BooleanSetting("Fill", this, true);
    private final BooleanSetting outline = new BooleanSetting("Outline", this, true);
    private final BooleanSetting tracers = new BooleanSetting("Tracers", this, true);
    private final ColorSetting color = new ColorSetting("Color", this, Color.YELLOW);
    private final NumberSetting lineWidth = new NumberSetting("Line Width", this, 1.0, 1.0, 5.0, 1);
    private boolean treasureDetected = false;
    private long detectionTime = 0;
    private static final long DISPLAY_DURATION = 3000; // Display duration in milliseconds

    public SewerESP() {
        super("SewerESP", "Highlights sewer treasures", Category.PIT);
        addSettings(fill, outline, tracers, color, lineWidth);
    }

    @EventTarget
    public void onChat(EventChat event) {
        if (event.getMessage().contains("SEWERS! A new treasure spawned somewhere!")) {
            treasureDetected = true;
            detectionTime = System.currentTimeMillis();
            Minecraft.getMinecraft().theWorld.playSoundEffect(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.levelup", 1.0F, 1.0F);
            System.out.println("Treasure detected!");
            enableESP();
        } else if (event.getMessage().contains("SEWERS! ") && event.getMessage().contains(" found the treasure")) {
            treasureDetected = false;
            System.out.println("Treasure found, disabling ESP.");
            disableESP();
        }
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        if (treasureDetected) {
            for (Object o : mc.theWorld.loadedTileEntityList) {
                if (o instanceof TileEntityChest) { // Adjust this to the specific TileEntity representing sewer treasures
                    TileEntityChest treasure = (TileEntityChest) o;
                    double x = treasure.getPos().getX() - mc.getRenderManager().viewerPosX;
                    double y = treasure.getPos().getY() - mc.getRenderManager().viewerPosY;
                    double z = treasure.getPos().getZ() - mc.getRenderManager().viewerPosZ;

                    if (fill.isEnabled()) {
                        RenderUtils.drawFilledBox(x, y, z, x + 1, y + 1, z + 1, color.getColor().getRed() / 255.0f, color.getColor().getGreen() / 255.0f, color.getColor().getBlue() / 255.0f, 0.5f);
                    }

                    if (outline.isEnabled()) {
                        RenderUtils.drawBox(x, y, z, x + 1, y + 1, z + 1, color.getColor().getRed() / 255.0f, color.getColor().getGreen() / 255.0f, color.getColor().getBlue() / 255.0f, 1.0f);
                    }

                    if (tracers.isEnabled()) {
                        drawTracerToTreasure(treasure.getPos(), color.getColor().getRGB(), (float) lineWidth.getValue());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (treasureDetected && System.currentTimeMillis() - detectionTime < DISPLAY_DURATION) {
            drawTreasureSpawnedText();
        }
    }

    private void drawTracerToTreasure(BlockPos pos, int color, float lineWidth) {
        EntityPlayerSP player = mc.thePlayer;
        Vec3 playerPos = new Vec3(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3 treasurePos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        RenderUtils.drawLine(playerPos, treasurePos, color, lineWidth);
    }

    private void drawTreasureSpawnedText() {
        FontRenderer fontRenderer = mc.fontRendererObj;
        String text = "TREASURE SPAWNED!";
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int width = scaledResolution.getScaledWidth();
        int height = scaledResolution.getScaledHeight();
        int x = (width / 2) - (fontRenderer.getStringWidth(text) / 2);
        int y = height / 4;
        fontRenderer.drawStringWithShadow(text, x, y, Color.YELLOW.getRGB());
    }

    private void enableESP() {
        // Any additional logic needed to enable ESP can be added here
        treasureDetected = true;
    }

    private void disableESP() {
        // Any additional logic needed to disable ESP can be added here
        treasureDetected = false;
    }
}
