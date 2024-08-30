package org.brutality.command;

import org.brutality.command.impl.BindCommand;
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
        commands.add(new BindCommand());
        // Add other commands here if needed
    }

    public void handleChat(String message) {
        if (!message.startsWith(prefix)) {
            return;
        }
        message = message.substring(prefix.length()).trim();
        String[] parts = message.split(" ");
        if (parts.length == 0) return;

        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        boolean foundCommand = false;
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName) || command.getAliases().contains(commandName)) {
                command.onCommand(args, message);
                foundCommand = true;
                break;
            }
        }
        if (!foundCommand) {
            Wrapper.addChatMessage(Wrapper.Colors.aqua + "Invalid Command (" + message + ").");
        }
    }
}
