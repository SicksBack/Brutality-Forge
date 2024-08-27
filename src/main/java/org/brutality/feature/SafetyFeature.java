package org.brutality.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.utils.Wrapper;

public class SafetyFeature {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final String TARGET_SERVER_IP = "mc.hypixel.net";

    public SafetyFeature() {
        // Constructor logic if needed
    }

    public void checkAndDisableKillAura() {
        // Ensure the player and world are valid
        if (mc.thePlayer != null && mc.theWorld != null) {
            ServerData serverData = mc.getCurrentServerData();
            if (serverData != null && TARGET_SERVER_IP.equalsIgnoreCase(serverData.serverIP)) {
                // Access the ModuleManager instance
                KillAura killAura = ModuleManager.getInstance().getModuleByClass(KillAura.class);

                // Check if KillAura is enabled and disable it
                if (killAura != null && killAura.isToggled()) {
                    killAura.toggle(); // This will disable the KillAura module
                    Wrapper.addChatMessage(Wrapper.Colors.black + "[" +
                            Wrapper.Colors.dark_red + "B" +
                            Wrapper.Colors.black + "] " +
                            Wrapper.Colors.red + "KILLAURA MODULE DETECTED! DISABLING...");
                }
            }
        }
    }
}
