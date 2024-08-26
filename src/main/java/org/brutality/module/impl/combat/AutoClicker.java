package org.brutality.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.brutality.events.Listener;
import org.brutality.events.MCClickEvent;
import org.brutality.events.RenderEvent;
import org.brutality.events.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;

import java.util.concurrent.ThreadLocalRandom;

public class AutoClicker extends Module {

    private final NumberSetting minCPS = new NumberSetting("Min CPS", this, 10, 1, 20, 0);
    private final NumberSetting maxCPS = new NumberSetting("Max CPS", this, 15, 1, 20, 0);
    private final TimerUtils timer = new TimerUtils();
    private final MCClickEvent mcClickEvent = new MCClickEvent();
    private boolean wasHoldingMouse = false;
    private boolean clickingTick = false;
    private boolean breakingBlock = false;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks for you.", Category.COMBAT);
        this.addSettings(minCPS, maxCPS);
    }

    @Listener
    public void onRender(RenderEvent event) {
        if (this.wasHoldingMouse) {
            long minDelay = 1000L / (long) this.maxCPS.getValue();  // Casting to long
            long maxDelay = 1000L / (long) this.minCPS.getValue();  // Casting to long
            long delay = maxDelay > minDelay ? ThreadLocalRandom.current().nextLong(minDelay, maxDelay) : minDelay;
            if (this.timer.getElapsedTime() >= delay) {
                this.clickingTick = true;
                this.timer.reset();
            }
        }
    }

    @Listener
    public void onTick(TickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (Mouse.isButtonDown(0) && !Mouse.isButtonDown(1) && !mc.thePlayer.isUsingItem() && !(mc.currentScreen instanceof GuiInventory)) {
            if (this.wasHoldingMouse && this.clickingTick) {
                mcClickEvent.click(); // Use custom click event
                this.clickingTick = false;
            }
            this.wasHoldingMouse = true;
        } else {
            this.wasHoldingMouse = false;
            mcClickEvent.reset(); // Reset the clicking status
        }

        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            this.breakingBlock = true;
        } else {
            this.breakingBlock = false;
        }
    }
}
