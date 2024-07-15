package org.brutality.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.brutality.BrutalityClient;
import org.brutality.module.Module;
import org.brutality.module.impl.pit.Events;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CheckEventsRunnable implements Runnable {
    private static final String EVENTS_URL = "https://events.mcpqndq.dev/";
    private static List<String> eventList = new ArrayList<>();

    @Override
    public void run() {
        try {
            while (true) {
                URL url = new URL(EVENTS_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(reader).getAsJsonArray();

                List<String> newEventList = new ArrayList<>();
                for (JsonElement element : jsonArray) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    String name = jsonObject.get("name").getAsString();
                    long timestamp = jsonObject.get("timestamp").getAsLong();
                    newEventList.add(name + " - " + (timestamp - System.currentTimeMillis()) / 1000 + "s");
                }

                newEventList.sort(Comparator.comparingLong(e -> Long.parseLong(e.split(" - ")[1].replace("s", ""))));
                eventList = newEventList.size() > 10 ? newEventList.subList(0, 10) : newEventList;

                for (Module tempModule : BrutalityClient.getInstance().getModuleManager().getModules()) {
                    if (tempModule instanceof Events) {
                        ((Events) tempModule).setEventList(eventList);
                    }
                }

                Thread.sleep(600000); // Refresh every 10 minutes
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
