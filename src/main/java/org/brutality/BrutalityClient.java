package org.brutality;

import org.brutality.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.brutality.modules.ModuleManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "brutalityclient", version = "1.0 Beta")
public class BrutalityClient
{
    public static BrutalityClient INSTANCE;
    public ModuleManager moduleManager;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        INSTANCE = this;
        moduleManager = new ModuleManager();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        // Get the keycode of the pressed key
        int keyCode = Keyboard.getEventKey();

        // Only enables if the key is pressed down (rather then released)
        if (Keyboard.getEventKeyState()) {
            if (!moduleManager.isEmpty()) {
                for (Module module : moduleManager) {
                    // Check if the module's key matches the pressed key
                    if (module.getKey().isPressed()) {
                        module.toggle();
                    }
                }
            }
        }
    }
}
