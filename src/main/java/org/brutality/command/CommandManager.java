package org.brutality.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private final List<Command> commands = new ArrayList<>();

    public void register(Command command) {
        commands.add(command);
    }

    public void executeCommand(String input) {
        if (!input.startsWith(".")) return;

        String[] split = input.substring(1).split(" ");
        String commandName = split[0];
        String[] args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, args.length);

        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(commandName)) {
                command.execute(args);
                return;
            }
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    command.execute(args);
                    return;
                }
            }
        }
        // Optionally, send a message if the command was not found
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Unknown command: " + commandName));
    }
}
