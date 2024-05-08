package com.example.examplemod.modules.modules.PIT;

import com.example.examplemod.events.Event;
import com.example.examplemod.events.listeners.EventRenderOverlay;
import com.example.examplemod.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class VenomAlert extends Module {

    private static final Potion VENOM_POTION = Potion.getPotionById(/* ID of the Venom/Poison effect */);
    private static final int VENOM_DURATION_THRESHOLD = 10; // Threshold in seconds to display alert

    public VenomAlert() {
        super("VenomAlert", "Alerts the player when affected by venom", Category.PIT);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRenderOverlay) {
            EventRenderOverlay overlayEvent = (EventRenderOverlay) event;
            if (overlayEvent.getType() == EventRenderOverlay.Type.TEXT) {
                renderVenomAlert();
            }
        }
    }

    private void renderVenomAlert() {
        Minecraft mc = Minecraft.getMinecraft();
        for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            if (effect.getPotionID() == VENOM_POTION.getId()) {
                int duration = effect.getDuration() / 20; // Convert ticks to seconds
                if (duration <= VENOM_DURATION_THRESHOLD) {
                    // Display alert in the middle of the screen
                    String alertMessage = "Venomed (" + duration + "s)";
                    int centerX = mc.displayWidth / 2;
                    int centerY = mc.displayHeight / 2;
                    mc.fontRendererObj.drawStringWithShadow(alertMessage, centerX, centerY, 0xFF0000);
                }
                break; // Stop looping through effects once venom effect is found
            }
        }
    }
}
