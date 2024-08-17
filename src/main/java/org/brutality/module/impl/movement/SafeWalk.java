package org.brutality.module.impl.movement;

import org.brutality.module.Category;
import org.brutality.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;

public class SafeWalk extends Module {

    private boolean isSneaking;

    public SafeWalk() {
        super("SafeWalk", "Prevents falling off edges by automatically sneaking near edges.", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        setSneakState(false);
        this.isSneaking = false;
    }


    public void onUpdate() {
        // Check if the module is enabled using a different method or field
        // Assume there's a boolean field or method to check if the module is active
        if (this.isModuleActive()) { // Replace this line with actual check
            if (mc.thePlayer.onGround && isNearEdge()) {
                setSneakState(true);
            } else if (this.isSneaking) {
                setSneakState(false);
            }
        }
    }

    private boolean isNearEdge() {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY - 1;
        double z = mc.thePlayer.posZ;

        // Check if any block around the player is air, indicating the edge
        return mc.theWorld.isAirBlock(new BlockPos(x + 0.3, y, z + 0.3)) ||
                mc.theWorld.isAirBlock(new BlockPos(x - 0.3, y, z + 0.3)) ||
                mc.theWorld.isAirBlock(new BlockPos(x + 0.3, y, z - 0.3)) ||
                mc.theWorld.isAirBlock(new BlockPos(x - 0.3, y, z - 0.3));
    }

    private void setSneakState(boolean down) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), down);
        if (this.isSneaking == down) {
            return;
        }
        this.isSneaking = down;
    }

    // Placeholder method to check if the module is active
    private boolean isModuleActive() {
        // Implement the logic to check if the module is enabled.
        // This could be a direct boolean field, or another method in your framework.
        // Example: return this.isEnabled(); if isEnabled() was available
        return true; // Replace with actual implementation
    }
}
