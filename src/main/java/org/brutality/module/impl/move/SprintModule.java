package org.brutality.module.impl.move;

import org.brutality.module.Module;
import org.brutality.module.Category;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.input.Keyboard;

public class SprintModule extends Module {
    private final BooleanSetting omniSprintSetting = new BooleanSetting("Omni Sprint", this, false);

    public SprintModule() {
        super("Sprint", "Makes the player sprint (essentially toggle sprint)", Category.MOVEMENT);
        this.setKey(Keyboard.KEY_O);
    }

    @Override
    public void onDisable() {
        mc.thePlayer.setSprinting(mc.gameSettings.keyBindSprint.isKeyDown());
        super.onDisable();
    }

    private static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }
        boolean result = omniSprintSetting.enabled ? isMoving() : mc.thePlayer.moveForward > 0.0F;
        mc.thePlayer.setSprinting(result);
    }
}