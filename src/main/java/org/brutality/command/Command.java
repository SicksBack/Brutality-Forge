package org.brutality.command;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public abstract class Command {
    private final String name;
    private final String[] aliases;

    public Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract void execute(String[] args);

    protected void sendMessage(String message) {
        // Send message to player (implement your own method)
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
    }
}
