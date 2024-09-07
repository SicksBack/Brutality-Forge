package org.brutality.commands;

import org.brutality.events.listeners.EventChat;
import org.brutality.commands.impl.FriendCommand;
import org.brutality.commands.impl.KOSCommand;
import org.brutality.utils.Wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();
    private final String prefix = ".";

    public CommandManager() {
        setup();
    }

    private void setup() {
        commands.add(new FriendCommand());
        commands.add(new KOSCommand());
    }

    public void handleChat(EventChat event) {
        String message = event.getMessage();
        if (!message.startsWith(prefix)) {
            return;
        }
        event.setCancelled(true);
        message = message.substring(prefix.length());
        String[] parts = message.split(" ");
        if (parts.length > 0) {
            String commandName = parts[0];
            Command command = commands.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(commandName) || c.getAliases().contains(commandName.toLowerCase()))
                    .findFirst()
                    .orElse(null);
            if (command != null) {
                command.onCommand(Arrays.copyOfRange(parts, 1, parts.length), message);
            } else {
                Wrapper.addChatMessage("Unknown command: " + commandName);
            }
        }
    }
}
