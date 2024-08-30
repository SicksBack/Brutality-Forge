package org.brutality.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.CustomTimer;
import org.lwjgl.input.Keyboard;

public class AutoClicker extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting cps = new NumberSetting("CPS", this, 10, 1, 20, 0);
    private final CustomTimer timer = new CustomTimer();

    private long lastClickTime = 0;

    public AutoClicker() {
        super("AutoClicker", "Automatically clicks at a set CPS.", Category.COMBAT);
        addSettings(cps);
        setKey(Keyboard.KEY_O);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        long currentTime = System.currentTimeMillis();
        long delay = (long) (1000 / cps.getValue());

        if (currentTime - lastClickTime >= delay) {
            lastClickTime = currentTime;
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, mc.objectMouseOver.entityHit);
        }
    }
}
