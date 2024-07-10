package org.brutality.module.impl.pit;

import org.brutality.events.Event;
import org.brutality.events.EventListener;
import org.brutality.events.EventManager;
import org.brutality.events.listeners.EventChat;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimeHelper;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhoGotDogged extends Module {
    private ArrayList<String> prev = new ArrayList<>();
    private TimeHelper banCooldown = new TimeHelper();
    private NumberSetting yPos = new NumberSetting("Y Pos", this, 5.0, 0.0, 1200.0, 1);
    private NumberSetting xPos = new NumberSetting("X Pos", this, 40.0, 0.0, 1200.0, 1);

    public WhoGotDogged() {
        super("WhoGotDogged", "Checks who left the lobby on ban message.", Category.PIT);
        addSettings(yPos, xPos);

        EventManager.register(EventUpdate.class, new EventListener<EventUpdate>() {
            @Override
            public void onEvent(EventUpdate event) {
                if (mc.thePlayer == null) {
                    return;
                }
                getDifference();
            }
        });

        EventManager.register(EventChat.class, new EventListener<EventChat>() {
            @Override
            public void onEvent(EventChat event) {
                String msg = event.getMessage();
                if (msg.contains("A player has been removed from your game.")) {
                    banCooldown.reset();
                }
            }
        });
    }

    private void getDifference() {
        Collection<NetworkPlayerInfo> currentPlayers = mc.getNetHandler().getPlayerInfoMap();
        if (!prev.isEmpty()) {
            for (String playerName : prev) {
                boolean stillHere = currentPlayers.stream().anyMatch(info -> playerName.equals(info.getGameProfile().getName()));
                if (!stillHere && !banCooldown.hasReached(1500L)) {
                    logToChat(playerName);
                }
            }
        }
        prev.clear();
        for (NetworkPlayerInfo info : currentPlayers) {
            prev.add(info.getGameProfile().getName());
        }
    }

    private void logToChat(String playerName) {
        String message = "§0[§4B§0] §6" + playerName;
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
