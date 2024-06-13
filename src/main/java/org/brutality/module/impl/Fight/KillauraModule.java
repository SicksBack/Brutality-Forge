package org.brutality.module.impl.Fight;

import org.brutality.module.Module;
import org.brutality.module.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

public class KillauraModule extends Module {

    public KillauraModule() {
        super("Sprint", "Makes the player sprint (essentially toggle sprint)", Category.MOVEMENT);
        // Set keybinding
        this.setKey(Keyboard.KEY_O);
    }

    public void onDisable() {
        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        super.onDisable();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        mc.thePlayer.setSprinting(true);
        if (mc.thePlayer.moveForward <= 0.0f) {
            mc.thePlayer.setSprinting(false);
        }