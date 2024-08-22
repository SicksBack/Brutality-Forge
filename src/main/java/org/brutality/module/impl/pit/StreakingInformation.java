package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Mouse; // Import the LWJGL Mouse class

public class StreakingInformation extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private int kills = 0;
    private int gold = 0;
    private int xp = 0;

    private int x = 10;
    private int y = 10;
    private int width = 150;
    private int height = 60;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    public StreakingInformation() {
        super("StreakingInformation", "Tracks and displays kills, gold, and XP in The Hypixel Pit", Category.PIT);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null || !(mc.currentScreen instanceof GuiChat)) {
            return;
        }

        // Render the scoreboard
        renderScoreboard();
    }

    private void renderScoreboard() {
        // Draw background
        Gui.drawRect(x, y, x + width, y + height, 0x80000000);

        // Draw text
        FontRenderer fontRenderer = mc.fontRendererObj;
        fontRenderer.drawString("Kills: " + kills, x + 5, y + 10, 0xFFFFFF);
        fontRenderer.drawString("Gold: " + gold, x + 5, y + 25, 0xFFFF00);
        fontRenderer.drawString("XP: " + xp, x + 5, y + 40, 0x00FF00);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Track kills, gold, and XP changes
        trackPitStats();
    }

    private void trackPitStats() {
        // This is a placeholder. You need to implement logic to track kills, gold, and XP.
        // Example: Check specific conditions or listen to events to update these values.
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.entity;
        if (entity instanceof EntityPlayer && entity == mc.thePlayer) {
            // Reset stats when the player dies
            kills = 0;
            gold = 0;
            xp = 0;
        }
    }

    @SubscribeEvent
    public void onMouseInput(net.minecraftforge.fml.common.eventhandler.Event event) {
        if (!(mc.currentScreen instanceof GuiChat)) {
            return;
        }

        // Check if the player is dragging the scoreboard
        if (dragging) {
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();
            int scaledWidth = mc.displayWidth;
            int scaledHeight = mc.displayHeight;

            // Adjust the y coordinate for Minecraft's inverted y-axis
            y = scaledHeight - mouseY - dragOffsetY;
            x = mouseX - dragOffsetX;
        }
    }

    @SubscribeEvent
    public void onMouseClick(net.minecraftforge.fml.common.eventhandler.Event event) {
        if (!(mc.currentScreen instanceof GuiChat)) {
            return;
        }

        int mouseX = Mouse.getX();
        int mouseY = Mouse.getY();
        int scaledWidth = mc.displayWidth;
        int scaledHeight = mc.displayHeight;

        // Convert mouse coordinates to Minecraft screen coordinates
        mouseX = mouseX * scaledWidth / mc.displayWidth;
        mouseY = scaledHeight - mouseY * scaledHeight / mc.displayHeight;

        // Start dragging if the mouse is within the scoreboard and the left button is pressed
        if (isMouseWithinScoreboard(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            dragging = true;
            dragOffsetX = mouseX - x;
            dragOffsetY = mouseY - y;
        } else {
            dragging = false;
        }
    }

    private boolean isMouseWithinScoreboard(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
