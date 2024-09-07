package org.brutality.authentication;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Webhook {

    private static final String WEBHOOK_URL = "https://discordapp.com/api/webhooks/1281987714144796807/n2SPdlsvSuXOcYGlmqZhoz1OEAYrDirPobTqxJxtLQXVMPzrhOYJyu81phTBy8srE716";

    public static void sendUserInfo(String hwid, String key) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setDoOutput(true);

            String jsonPayload = String.format("{\"content\": \"HWID: %s, Key: %s\"}", hwid, key);
            byte[] outputBytes = jsonPayload.getBytes(StandardCharsets.UTF_8);

            try (OutputStream os = connection.getOutputStream()) {
                os.write(outputBytes);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Webhook sent successfully.");
            } else {
                System.out.println("Failed to send webhook. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception occurred while sending the webhook: " + e.getMessage());
        }
    }
}
