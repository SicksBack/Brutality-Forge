package org.brutality.module.impl.movement;

import org.brutality.events.impl.KeepSprintEvent;
import org.brutality.module.Module;
import org.brutality.module.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

public class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", "Prevents stopping sprint when moving or attacking.", Category.MOVEMENT);
        setKey(Keyboard.KEY_B);
}

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == mc.thePlayer) {
            // Check if the player is not sprinting but moving forward
            if (!mc.thePlayer.isSprinting() && mc.thePlayer.moveForward > 0) {
                // Post the KeepSprintEvent
                KeepSprintEvent keepSprintEvent = new KeepSprintEvent();
                MinecraftForge.EVENT_BUS.post(keepSprintEvent);

                // If the event is not canceled, re-enable sprinting
                if (!keepSprintEvent.isCanceled()) {
                    mc.thePlayer.setSprinting(true);
                }
            }
        }
    }
}
