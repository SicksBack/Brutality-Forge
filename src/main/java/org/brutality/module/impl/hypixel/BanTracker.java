package org.brutality.module.impl.hypixel;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class BanTracker extends Module {
    private final BooleanSetting watchdogTracking = new BooleanSetting("Watchdog Tracking", this, true);

    private static final String API_KEY_FILE = "brutality/api/key.txt";
    private static final String WATCHDOG_BAN_URL = "https://api.hypixel.net/watchdogstats?key=";

    private String apiKey;
    private long lastUpdate = 0;
    private int watchdogBans = 0;

    public BanTracker() {
        super("BanTracker", "Tracks Hypixel bans.", Category.HYPIXEL);
        addSettings(watchdogTracking);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        checkAndCreateApiKeyFile();
        readApiKey();
    }


    public void onUpdate() {
        if (!isToggled() || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdate >= 60000) { // 1 minute update interval
            lastUpdate = currentTime;

            if (watchdogTracking.isEnabled()) {
                updateWatchdogBans();
            }

            displayBanCount(currentTime - lastUpdate);
        }
    }

    private void checkAndCreateApiKeyFile() {
        File file = new File(API_KEY_FILE);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs(); // Create directories if not existing
                file.createNewFile(); // Create the file

                // Allow the user to enter the API key manually
                try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                    writer.println("EnterYourAPIKeyHere");
                }

                System.out.println("API key file created at " + API_KEY_FILE + ". Please enter your API key in the file.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readApiKey() {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(API_KEY_FILE)))) {
            apiKey = reader.readLine().trim();
        } catch (IOException e) {
            apiKey = null;
            e.printStackTrace();
        }
    }

    private void updateWatchdogBans() {
        if (apiKey == null || apiKey.equalsIgnoreCase("EnterYourAPIKeyHere")) {
            return;
        }

        try {
            URL url = new URL(WATCHDOG_BAN_URL + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            Scanner scanner = new Scanner(connection.getInputStream());
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            if (response.contains("\"watchdog_lastMinute\":")) {
                String[] parts = response.split("\"watchdog_lastMinute\":");
                String bansStr = parts[1].split(",")[0];
                watchdogBans = Integer.parseInt(bansStr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayBanCount(long timeSinceLastUpdate) {
        String message = EnumChatFormatting.GRAY + "Watchdog " + EnumChatFormatting.RED + "Last Minute: " + watchdogBans +
                EnumChatFormatting.GRAY + " Last Updated " + EnumChatFormatting.YELLOW + (timeSinceLastUpdate / 1000) + "s Ago";

        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(message));
    }
}
