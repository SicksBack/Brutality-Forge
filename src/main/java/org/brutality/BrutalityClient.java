package org.brutality;

import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.brutality.commands.CommandManager;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.render.ClickGuiModule;
import org.brutality.settings.SettingsManager;
import org.brutality.ui.clickGui.ClickGui;
import org.brutality.ui.font.FontManager;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IChatComponent;
import org.brutality.events.listeners.EventChat;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

@Mod(modid = "brutalityclient", version = "1.0 Beta")
public class BrutalityClient {
    public static BrutalityClient INSTANCE;

    @Getter
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;
    public CommandManager commandManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        INSTANCE = this;
        new FontManager().init();
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        moduleManager.init();
        commandManager = new CommandManager();
        clickGui = new ClickGui();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static BrutalityClient getInstance() {
        return INSTANCE;
    }

    // New method to get the CommandManager
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        if (Keyboard.getEventKeyState()) {
            for (Module module : moduleManager.getModules()) {
                KeyBinding key = module.getKey();
                if (key == null) continue;
                if (key.isPressed()) {
                    module.toggle();
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        IChatComponent chatComponent = event.message;
        String message = chatComponent.getUnformattedText();

        EventChat eventChat = new EventChat(message);
        MinecraftForge.EVENT_BUS.post(eventChat);

        if (message.startsWith(".")) {
            commandManager.handleChat(message);
            event.setCanceled(true);
        }
    }
}
