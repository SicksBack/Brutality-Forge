package org.brutality.events.listeners;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.command.CommandManager;

public class EventChat {

    private final CommandManager commandManager = new CommandManager();

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.startsWith(".")) {
            commandManager.handleChat(message); // Process the command
            event.setCanceled(true); // Prevent the message from appearing in chat
        }
    }
}
