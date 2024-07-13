package org.brutality.command;

import org.brutality.command.impl.FriendsCommand;
import org.brutality.command.impl.KOSCommand;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public CommandManager() {
        commands.add(new FriendsCommand());
        commands.add(new KOSCommand());
    }

    public void handleCommand(String input) {
        String[] args = input.split(" ");
        String commandName = args[0].substring(1); // Remove the prefix

        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                command.execute(args);
                return;
            }
        }

        System.out.println("Unknown command. Type '.help' for a list of commands.");
    }

    public List<String> getSuggestions(String input) {
        String[] args = input.split(" ");
        String commandName = args[0].substring(1); // Remove the prefix

        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                return command.getSuggestions(args);
            }
        }

        return new ArrayList<>();
    }

    public List<Command> getCommands() {
        return commands;
    }
}
