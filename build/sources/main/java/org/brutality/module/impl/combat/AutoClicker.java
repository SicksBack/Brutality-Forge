package org.brutality.module.impl.combat;

import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.events.EventTarget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import java.lang.reflect.Method;
import java.util.Random;

public class AutoClicker extends Module {

    private final NumberSetting minAps = new NumberSetting("Min APS", this, 10, 1, 20, 1);
    private final NumberSetting maxAps = new NumberSetting("Max APS", this, 15, 1, 20, 1);
    private final BooleanSetting holdLeftClick = new BooleanSetting("Hold Left Click", this, true);

    private final Random random = new Random();
    private long lastClickTime;
    private long nextClickDelay;

    public AutoClicker() {
        super("AutoClicker", "Auto Clicks", Category.COMBAT);
        addSettings(minAps, maxAps, holdLeftClick);
        updateNextClickDelay();
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (!isValidCondition()) {
            return;
        }

        if (System.currentTimeMillis() - lastClickTime >= nextClickDelay) {
            performClick();
            updateNextClickDelay();
            lastClickTime = System.currentTimeMillis();
        }
    }

    private boolean isValidCondition() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        return player != null && mc.currentScreen == null && (!holdLeftClick.isEnabled() || mc.gameSettings.keyBindAttack.isKeyDown());
    }

    private void performClick() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            Method clickMouse = Minecraft.class.getDeclaredMethod("clickMouse");
            clickMouse.setAccessible(true);
            clickMouse.invoke(mc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNextClickDelay() {
        int minCps = (int) minAps.getValue();
        int maxCps = (int) maxAps.getValue();
        if (minCps > maxCps) {
            minCps = maxCps;
        }
        nextClickDelay = 1000L / (random.nextInt(maxCps - minCps + 1) + minCps);
    }
}
