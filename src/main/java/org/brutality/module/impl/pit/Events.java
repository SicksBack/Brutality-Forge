package org.brutality.module.impl.pit;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EventUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Events extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<String> eventList = new ArrayList<>();
    private int timePassIndex = 0;
    private volatile String eventResponse;
    private long lastUpdateTimestamp = 0;

    // NumberSetting to control the number of events displayed (1 to 10)
    private final NumberSetting eventCountSetting = new NumberSetting("Event Count", this, 5, 1, 10, 0);

    public Events() {
        super("Events", "Lists the next events.", Category.PIT);
        addSettings(eventCountSetting); // Add the setting to the module
        startEventUpdater();
    }

    private void startEventUpdater() {
        Thread eventUpdaterThread = new Thread(() -> {
            while (this.isToggled()) {
                eventResponse = EventUtils.fetchEvents();
                try {
                    Thread.sleep(1000); // Fetch new data every 1 second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        eventUpdaterThread.start();
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (eventResponse != null && System.currentTimeMillis() - lastUpdateTimestamp > 1000) { // Update every 1 second
            try {
                JsonArray jsonArray = new JsonParser().parse(eventResponse).getAsJsonArray();
                eventList.clear();
                int eventsToShow = (int) eventCountSetting.getValue(); // Get the number of events to display
                for (int i = timePassIndex; i < timePassIndex + eventsToShow; i++) {
                    try {
                        String eventName = jsonArray.get(i).getAsJsonObject().get("event").getAsString();
                        long eventTimestamp = jsonArray.get(i).getAsJsonObject().get("timestamp").getAsLong();
                        long timeUntilEvent = (eventTimestamp - Instant.now().getEpochSecond() * 1000L) / 60000L;

                        if (timeUntilEvent < 0) {
                            timePassIndex++;
                            continue;
                        }

                        eventList.add(eventName + " [" + timeUntilEvent + "m]");
                    } catch (Exception ex) {
                        // Handle individual parsing errors here if needed
                    }
                }
                lastUpdateTimestamp = System.currentTimeMillis();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || mc.theWorld == null || event.type != RenderGameOverlayEvent.ElementType.CHAT || !this.isToggled()) {
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);
        FontRenderer fr = mc.fontRendererObj;
        float posY = 50.0f; // Y position, can be made configurable
        float posX = 5.0f; // X position, can be made configurable

        for (String eventInfo : eventList) {
            String[] splitEvent = eventInfo.split(" \\[");
            String eventName = splitEvent[0];
            String timeRemaining = "[" + splitEvent[1];

            int color = getColorForEvent(eventName);

            fr.drawStringWithShadow(eventName, posX, posY, color);
            fr.drawStringWithShadow(timeRemaining, posX + fr.getStringWidth(eventName + " "), posY, 0xAAAAAA); // Grey brackets
            posY += fr.FONT_HEIGHT; // Move to the next line
        }
    }

    private int getColorForEvent(String event) {
        if (event.contains("Auction")) {
            return 0xFFFF55;
        }
        if (event.contains("All bounty")) {
            return 0xFFAA00;
        }
        if (event.contains("Night Quest")) {
            return 0x5555FF;
        }
        if (event.contains("Raffle")) {
            return 0x55FF55;
        }
        if (event.contains("Care Package")) {
            return 0xFFAA00;
        }
        if (event.contains("KOTH")) {
            return 0x55FFFF;
        }
        // Add more event types and colors here as needed
        return 0xFFFFFF; // Default color
    }
}
