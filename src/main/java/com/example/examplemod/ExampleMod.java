package com.example.examplemod;

import com.example.examplemod.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import com.example.examplemod.modules.ModuleManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    public static final String VERSION = "0.1";
    public ModuleManager moduleManager;
    public static Minecraft mc = Minecraft.getMinecraft();

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        this.moduleManager = new ModuleManager();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        // Get the keycode of the pressed key
        int keyCode = Keyboard.getEventKey();

        // Only enables if the key is pressed down (rather then released)
        if (Keyboard.getEventKeyState()) {
            if (!ModuleManager.modules.isEmpty()) {
                for (Module module : ModuleManager.modules) {
                    // Check if the module's key matches the pressed key
                    if (module.getKey().isPressed()) {
                        module.toggle();
                    }
                }
            }
        }
    }
}
