package org.brutality;

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
// test commit
@Mod(modid = "brutalityclient", version = "1.0 Beta")
public class BrutalityClient
{
    public static BrutalityClient INSTANCE;
    public ModuleManager moduleManager;
    public SettingsManager settingsManager;
    public ClickGui clickGui;


    static {

        BrutalityClient.INSTANCE = new BrutalityClient();
    }


    public void initiate() {
        new FontManager().init();
        settingsManager = new SettingsManager();
        moduleManager = new ModuleManager();
        moduleManager.init();
        clickGui = new ClickGui();
        MinecraftForge.EVENT_BUS.register(this);

    }



    @SubscribeEvent
    public void onKeyPress(KeyInputEvent event) {
        // Only enables if the key is pressed down (rather then released)
        if (Keyboard.getEventKeyState()) {
            for (Module module : moduleManager) {
                // Check if the module's key matches the pressed key
                if (module.getKey() == null) continue;
                if (module.getKey().isPressed()) {
                    module.toggle();
                }
            }
        }
    }
}
