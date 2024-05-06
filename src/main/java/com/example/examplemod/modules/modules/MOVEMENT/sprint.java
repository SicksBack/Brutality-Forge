package com.example.examplemod.modules.modules.MOVEMENT;

import com.example.examplemod.modules.Module;
import com.example.examplemod.modules.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import com.example.examplemod.ExampleMod;

public class sprint extends Module {

    public sprint() {
        super("Sprint", "Makes the player sprint (essentially toggle sprint)", Category.MOVEMENT);
        // Set keybinding
        this.setKey(Keyboard.KEY_O);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        ExampleMod.mc.thePlayer.setSprinting(ExampleMod.mc.gameSettings.keyBindSprint.isKeyDown());
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        ExampleMod.mc.thePlayer.setSprinting(true);
        if (ExampleMod.mc.thePlayer.moveForward <= 0.0f) {
            ExampleMod.mc.thePlayer.setSprinting(false);
        }
    }
}