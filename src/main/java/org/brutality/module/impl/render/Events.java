package org.brutality.module.impl.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EventUtils;
import org.lwjgl.input.Mouse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Events extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<String> eventList = new ArrayList<>();
    private int timePassIndex = 0;
    private String eventResponse;
    private long lastFetchTime = 0;

    // NumberSetting to control the number of events to display
    private final NumberSetting numEvents = new NumberSetting("Number of Events", this, 5, 1, 10, 0);
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public Events() {
        super("Events", "Lists the next events.", Category.RENDER);
        // Add the setting to the module
        addSettings(numEvents, yPos, xPos);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFetchTime > 1000) { // Fetch every second
            fetchEventData();
            lastFetchTime = currentTime;
        }

        if (eventResponse != null) {
            try {
                JsonArray jsonArray = new JsonParser().parse(eventResponse).getAsJsonArray();
                eventList.clear();
                int numEventsToShow = (int) numEvents.getValue(); // Get the number of events to show

                for (int i = timePassIndex; i < timePassIndex + numEventsToShow; i++) {
                    try {
                        String eventName = jsonArray.get(i).getAsJsonObject().get("event").getAsString();
                        long eventTimestamp = jsonArray.get(i).getAsJsonObject().get("timestamp").getAsLong();
                        long timeUntilEventMillis = eventTimestamp - Instant.now().toEpochMilli();

                        if (timeUntilEventMillis < 0) {
                            timePassIndex++;
                            continue;
                        }

                        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeUntilEventMillis);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeUntilEventMillis) % 60;

                        // Format the event and time with brackets
                        eventList.add(eventName + " [" + String.format("%02d:%02d", minutes, seconds) + "]");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            eventResponse = null; // Reset to avoid repeated parsing
        }
    }

    private void fetchEventData() {
        new Thread(() -> eventResponse = EventUtils.fetchEvents()).start();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.type != RenderGameOverlayEvent.ElementType.CHAT || !isToggled()) {
            return;
        }

        if (mc.currentScreen instanceof GuiChat) {
            handleDragging();
        }

        FontRenderer fr = mc.fontRendererObj;
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();
        int maxEventNameWidth = 0;

        for (String eventInfo : eventList) {
            maxEventNameWidth = Math.max(maxEventNameWidth, fr.getStringWidth(eventInfo.split(" \\[")[0]));
        }

        for (String eventInfo : eventList) {
            String[] splitEvent = eventInfo.split(" \\[");
            String eventName = splitEvent[0];
            String timeRemaining = splitEvent[1].replace("]", "");

            int color = getColorForEvent(eventName);

            // Draw event name
            fr.drawStringWithShadow(eventName, posX, posY, color);

            // Draw time with light green color and grey brackets
            int timeXPosition = (int) (posX + maxEventNameWidth + 2); // Align time to the end of the longest event name

            // Draw brackets in grey
            fr.drawStringWithShadow("[", timeXPosition, posY, 0xAAAAAA); // Grey color for the opening bracket
            timeXPosition += fr.getStringWidth("["); // Move X position after the opening bracket

            // Draw time in light green
            fr.drawStringWithShadow(timeRemaining, timeXPosition, posY, 0x00FF00); // Light green color for the time
            timeXPosition += fr.getStringWidth(timeRemaining); // Move X position after the time

            // Draw closing bracket in grey
            fr.drawStringWithShadow("]", timeXPosition, posY, 0xAAAAAA); // Grey color for the closing bracket

            posY += fr.FONT_HEIGHT + 2;
        }
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - (int) xPos.getValue();
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - (int) yPos.getValue();
            }
            if (dragging) {
                xPos.setValue(Mouse.getX() / 2 - dragX);
                yPos.setValue(new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY);
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 200 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 20;
    }

    private int getColorForEvent(String eventName) {
        if (eventName.contains("Blockhead")) {
            return 0xFFAA00;
        }
        if (eventName.contains("Pizza")) {
            return 0xFF5555;
        }
        if (eventName.contains("Beast")) {
            return 0x55FF55;
        }
        if (eventName.contains("Robbery")) {
            return 0xFFAA00;
        }
        if (eventName.contains("Spire")) {
            return 0xAA00AA;
        }
        if (eventName.contains("Squads")) {
            return 0x55FFFF;
        }
        if (eventName.contains("Team Deathmatch")) {
            return 0xAA00AA;
        }
        if (eventName.contains("Raffle")) {
            return 0xFFAA00;
        }
        if (eventName.contains("Rage Pit")) {
            return 0xFF5555;
        }
        if (eventName.contains("2x Rewards")) {
            return 43520;
        }
        if (eventName.contains("Giant Cake")) {
            return 0xFF55FF;
        }
        if (eventName.contains("KOTL")) {
            return 0x55FF55;
        }
        if (eventName.contains("Dragon Egg")) {
            return 0xAA00AA;
        }
        if (eventName.contains("Auction")) {
            return 0xFFFF55;
        }
        if (eventName.contains("Quick Maths")) {
            return 0xAA00AA;
        }
        if (eventName.contains("KOTH")) {
            return 0x55FFFF;
        }
        if (eventName.contains("Care Package")) {
            return 0xFFAA00;
        }
        if (eventName.contains("All bounty")) {
            return 0xFFAA00;
        }
        return 0xFFFFFF; // Default color
    }
}
