package org.brutality;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.command.CommandManager;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.lwjgl.input.Keyboard;

public class KeyBindingHandler {

    // Singleton instance of CommandManager
    private static final CommandManager commandManager = new CommandManager();

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Module module : ModuleManager.getInstance().getModules()) {
                KeyBinding keyBinding = module.getKey();
                if (keyBinding != null && keyBinding.isPressed()) {
                    module.toggle();
                }
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.startsWith(".")) {
            commandManager.handleChat(message);
            event.setCanceled(true); // Prevent the message from being processed further
        }
    }
}
