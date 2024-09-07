package org.brutality.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AuthManager {

    public static boolean isHWIDWhitelisted(String hwid) {
        try (BufferedReader br = new BufferedReader(new FileReader("whitelist.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(hwid)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
