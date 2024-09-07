package org.brutality.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class FriendManager {
    private static final File FRIENDS_FILE = new File(Minecraft.getMinecraft().mcDataDir, "brutality/players/friends.txt");
    private static final Gson GSON = new Gson();
    private static Set<String> friends = new HashSet<>();

    static {
        loadFriends();
    }

    public static void addFriend(String name) {
        friends.add(name);
        saveFriends();
    }

    public static void removeFriend(String name) {
        friends.remove(name);
        saveFriends();
    }

    public static boolean isFriend(EntityPlayer player) {
        return friends.contains(player.getName());
    }

    public static void clearFriends() {
        friends.clear();
        saveFriends();
    }

    private static void loadFriends() {
        if (!FRIENDS_FILE.exists()) {
            try {
                FRIENDS_FILE.getParentFile().mkdirs();
                FRIENDS_FILE.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (Reader reader = new FileReader(FRIENDS_FILE)) {
            Type type = new TypeToken<Set<String>>(){}.getType();
            friends = GSON.fromJson(reader, type);
            if (friends == null) {
                friends = new HashSet<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            friends = new HashSet<>();
        }
    }

    private static void saveFriends() {
        try (Writer writer = new FileWriter(FRIENDS_FILE)) {
            GSON.toJson(friends, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
