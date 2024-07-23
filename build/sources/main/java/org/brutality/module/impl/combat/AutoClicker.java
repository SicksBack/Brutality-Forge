package org.brutality.module.impl.combat;

import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.events.EventTarget;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class AutoClicker extends Module {

    private BooleanSetting holdLeftClick = new BooleanSetting("Hold Left Click", this, true);
    private NumberSetting clickPerSecond = new NumberSetting("Clicks Per Second", this, 10, 1, 20, 1);
    private final Minecraft mc = Minecraft.getMinecraft();
    private Robot robot;
    private long lastClickTime;

    public AutoClicker() {
        super("AutoClicker", "", Category.COMBAT);
        addSettings(holdLeftClick, clickPerSecond);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (holdLeftClick.isEnabled() && mc.gameSettings.keyBindAttack.isKeyDown()) {
            int cps = (int) clickPerSecond.getValue();
            long delay = 1000L / cps;

            if (System.currentTimeMillis() - lastClickTime >= delay) {
                simulateLeftClick();
                lastClickTime = System.currentTimeMillis();
            }
        }
    }

    private void simulateLeftClick() {
        if (robot != null) {
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }


    public void onDisable() {
        super.onDisable();
        // Release the left click when the module is disabled
        if (robot != null) {
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }
}
