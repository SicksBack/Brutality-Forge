package org.brutality.command.impl;

import org.brutality.command.Command;

import java.util.List;
import java.util.ArrayList;

public class FriendsCommand extends Command {
    private static final List<String> friends = new ArrayList<>();

    public FriendsCommand() {
        super("Friends", "Manages friends", "friend <add/remove/list> <name>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: " + getUsage());
            return;
        }

        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("add") && args.length >= 2) {
            String friendName = args[1];
            friends.add(friendName);
            System.out.println("Added friend: " + friendName);
        } else if (subCommand.equalsIgnoreCase("remove") && args.length >= 2) {
            String friendName = args[1];
            friends.remove(friendName);
            System.out.println("Removed friend: " + friendName);
        } else if (subCommand.equalsIgnoreCase("list")) {
            System.out.println("Friends: " + friends);
        } else {
            System.out.println("Usage: " + getUsage());
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        // Implement if needed
        return new ArrayList<>();
    }

    public static List<String> getFriends() {
        return friends;
    }
}
