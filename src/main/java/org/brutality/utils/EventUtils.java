package org.brutality.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EventUtils {

    public static String fetchEvents() {
        StringBuilder response = new StringBuilder();

        try {
            String line;
            URL url = new URL("https://events.mcpqndq.dev/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            InputStream responseStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(responseStream));

            while ((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return response.toString();
    }
}
