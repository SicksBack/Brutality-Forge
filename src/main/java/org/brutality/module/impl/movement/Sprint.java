package org.brutality.module.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

public class Sprint extends Module {

    private final BooleanSetting allDirections = new BooleanSetting("All Directions", this, true);

    public Sprint() {
        super("Sprint", "Automatically makes the player sprint", Category.MOVEMENT);
        addSettings(allDirections);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            if (allDirections.isEnabled()) {
                if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward != 0 || Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe != 0) {
                    if (!Minecraft.getMinecraft().thePlayer.isSprinting()) {
                        Minecraft.getMinecraft().thePlayer.setSprinting(true);
                    }
                }
            } else {
                if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0) {
                    if (!Minecraft.getMinecraft().thePlayer.isSprinting()) {
                        Minecraft.getMinecraft().thePlayer.setSprinting(true);
                    }
                }
            }
        }
    }
}
