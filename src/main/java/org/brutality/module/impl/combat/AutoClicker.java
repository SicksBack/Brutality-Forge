package org.brutality.module.impl.combat;

import java.util.Random;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Utils;
import org.lwjgl.input.Mouse;

public class AutoClicker extends Module {
    public NumberSetting minCPS;
    public NumberSetting maxCPS;
    public BooleanSetting holdLeftClick;
    private Random rand = new Random();
    private long lastClickTime;
    private long nextClickTime;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you", Category.COMBAT);
        this.minCPS = new NumberSetting("Min CPS", this, 9.0, 1.0, 20.0, 1);
        this.maxCPS = new NumberSetting("Max CPS", this, 12.0, 1.0, 20.0, 1);
        this.holdLeftClick = new BooleanSetting("Hold Left Click", this, true);
        addSettings(minCPS, maxCPS, holdLeftClick);
    }

    @Override
    public void onEnable() {
        lastClickTime = System.currentTimeMillis();
        nextClickTime = lastClickTime + getNextClickDelay();
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        if (holdLeftClick.isEnabled() && Mouse.isButtonDown(0)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= nextClickTime) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), true);
                KeyBinding.onTick(mc.gameSettings.keyBindAttack.getKeyCode());
                nextClickTime = currentTime + getNextClickDelay();
            } else {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
            }
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindAttack.getKeyCode(), false);
        }
    }

    private long getNextClickDelay() {
        double cps = minCPS.getValue() + (maxCPS.getValue() - minCPS.getValue()) * rand.nextDouble();
        return (long) (1000.0 / cps);
    }
}
