package org.brutality.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class KOSManager {
    private static final File KOS_FILE = new File(Minecraft.getMinecraft().mcDataDir, "brutality/players/kos.txt");
    private static final Gson GSON = new Gson();
    private static Set<String> kosPlayers = new HashSet<>();
    private static boolean enabled = false; // Track whether KOS system is enabled

    static {
        loadKOS();
    }

    public static void setEnabled(boolean state) {
        enabled = state;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void addKOS(String name) {
        kosPlayers.add(name);
        saveKOS();
    }

    public static void removeKOS(String name) {
        kosPlayers.remove(name);
        saveKOS();
    }

    public static boolean isKOS(EntityPlayer player) {
        return enabled && kosPlayers.contains(player.getName()); // Check if KOS is enabled before checking KOS list
    }

    public static void clearKOS() {
        kosPlayers.clear();
        saveKOS();
    }

    private static void loadKOS() {
        if (!KOS_FILE.exists()) {
            try {
                KOS_FILE.getParentFile().mkdirs();
                KOS_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (Reader reader = new FileReader(KOS_FILE)) {
            Type type = new TypeToken<Set<String>>(){}.getType();
            kosPlayers = GSON.fromJson(reader, type);
            if (kosPlayers == null) {
                kosPlayers = new HashSet<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            kosPlayers = new HashSet<>();
        }
    }

    private static void saveKOS() {
        try (Writer writer = new FileWriter(KOS_FILE)) {
            GSON.toJson(kosPlayers, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
