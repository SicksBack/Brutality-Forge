package org.brutality.module.impl.combat;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;


public class Velocity extends Module {

    private final NumberSetting horizontal;
    private final NumberSetting vertical;
    private final NumberSetting chance;
    private final BooleanSetting onlyWhileTargeting;
    private final BooleanSetting disableS;

    public Velocity() {
        super("Velocity", "Modifies knockback velocity", Category.COMBAT);
        horizontal = new NumberSetting("Horizontal", this, 90, 0, 100, 0);
        vertical = new NumberSetting("Vertical", this, 100, 0, 100, 0);
        chance = new NumberSetting("Chance", this, 100, 0, 100, 0);
        onlyWhileTargeting = new BooleanSetting("Only while targeting", this, false);
        disableS = new BooleanSetting("Disable while holding S", this, false);

        addSettings(horizontal, vertical, chance, onlyWhileTargeting, disableS);
    }

    public String getInfo() {
        return (horizontal.getValue() == 100.0 ? "" : (int) horizontal.getValue() + "h") +
                (horizontal.getValue() != 100.0 && vertical.getValue() != 100.0 ? " " : "") +
                (vertical.getValue() == 100.0 ? "" : (int) vertical.getValue() + "v");
    }

    @SubscribeEvent
    public void onLivingUpdate(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Check if both horizontal and vertical settings are 0
        boolean zeroHorizontal = horizontal.getValue() == 0;
        boolean zeroVertical = vertical.getValue() == 0;

        if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.hurtTime == mc.thePlayer.maxHurtTime) {
            if (onlyWhileTargeting.isEnabled() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) {
                return;
            }

            if (disableS.isEnabled() && Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
                return;
            }

            if (chance.getValue() != 100.0 && Math.random() * 100 > chance.getValue()) {
                return;
            }

            if (zeroHorizontal && zeroVertical) {
                // Set all motion components to 0
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionY = 0;
                mc.thePlayer.motionZ = 0;
            } else {
                if (horizontal.getValue() != 100.0) {
                    mc.thePlayer.motionX *= horizontal.getValue() / 100.0;
                }

                if (vertical.getValue() != 100.0) {
                    mc.thePlayer.motionY *= vertical.getValue() / 100.0;
                }

                // The vertical motion should always be affected
                if (horizontal.getValue() == 100.0) {
                    mc.thePlayer.motionZ *= horizontal.getValue() / 100.0;
                }
            }
        }
    }
}
