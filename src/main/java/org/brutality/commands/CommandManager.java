package org.brutality.commands;

import org.brutality.commands.impl.FriendCommand;
import org.brutality.commands.impl.KOSCommand;
import org.brutality.utils.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

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

    public void handleChat(String message) {
        // Access the client player (EntityPlayerSP)
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        if (player == null || message == null || message.isEmpty()) {
            return; // Ensure player and message exist
        }

        if (!message.startsWith(prefix)) {
            // If the message does not start with the command prefix, send it normally
            player.sendChatMessage(message);
            return;
        }

        // Remove the prefix and process the command
        message = message.substring(prefix.length());
        String[] parts = message.split(" ");

        if (parts.length > 0) {
            String commandName = parts[0];
            Command command = commands.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(commandName) || c.getAliases().contains(commandName.toLowerCase()))
                    .findFirst()
                    .orElse(null);

            if (command != null) {
                // Execute the command with the remaining arguments
                command.onCommand(Arrays.copyOfRange(parts, 1, parts.length), message);
            } else {
                // Send an error message if the command is not found
                Wrapper.addChatMessage("Unknown command: " + commandName);
            }
        }
    }
}
