package org.brutality.events;

import net.minecraft.client.Minecraft;

public class MCClickEvent {
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean clicking = false;

    public void click() {
        mc.thePlayer.swingItem(); // Simulates the click visually
        mc.playerController.onPlayerDamageBlock(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit); // Simulates the actual click
        clicking = true;
    }

    public boolean isClicking() {
        return clicking;
    }

    public void reset() {
        clicking = false;
    }
}
