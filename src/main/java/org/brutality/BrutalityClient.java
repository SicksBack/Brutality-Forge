package org.brutality;

import lombok.Getter;
import org.brutality.module.Module;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.brutality.module.ModuleManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.brutality.settings.SettingsManager;
import org.brutality.ui.clickGui.ClickGui;
import org.brutality.ui.font.FontManager;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.settings.KeyBinding;

@Mod(modid = "brutalityclient", version = "1.0 Beta")
public class BrutalityClient
{
    public static BrutalityClient INSTANCE;
    @Getter
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        INSTANCE = this;
        new FontManager().init();
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        moduleManager.init();
        clickGui = new ClickGui();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static BrutalityClient getInstance() {
        return INSTANCE;
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
}