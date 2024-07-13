package org.brutality.command.impl;

import org.brutality.command.Command;

import java.util.List;
import java.util.ArrayList;

public class KOSCommand extends Command {
    private static final List<String> kosList = new ArrayList<>();

    public KOSCommand() {
        super("KOS", "Manages enemies (Kill On Sight)", "kos <add/remove/list> <name>");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: " + getUsage());
            return;
        }

        String subCommand = args[0];
        if (subCommand.equalsIgnoreCase("add") && args.length >= 2) {
            String kosName = args[1];
            kosList.add(kosName);
            System.out.println("Added to KOS: " + kosName);
        } else if (subCommand.equalsIgnoreCase("remove") && args.length >= 2) {
            String kosName = args[1];
            kosList.remove(kosName);
            System.out.println("Removed from KOS: " + kosName);
        } else if (subCommand.equalsIgnoreCase("list")) {
            System.out.println("KOS List: " + kosList);
        } else {
            System.out.println("Usage: " + getUsage());
        }
    }

    @Override
    public List<String> getSuggestions(String[] args) {
        // Implement if needed
        return new ArrayList<>();
    }

    public static List<String> getKOSList() {
        return kosList;
    }
}
