package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.RenderingTelebow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TelebowTimer extends Module {

    private final Map<String, Long> telebowTimers = new HashMap<>();
    private final RenderingTelebow renderingTelebow = new RenderingTelebow();

    public TelebowTimer() {
        super("TelebowTimer", "Tracks the cooldown of Telebow enchantments", Category.PIT);
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (event.entityPlayer == player && player.isSneaking()) {
            ItemStack itemStack = event.bow;
            if (itemStack != null && itemStack.getItem() instanceof ItemBow) {
                if (itemStack.hasDisplayName() && itemStack.getDisplayName().contains("Telebow")) {
                    int duration = getTelebowDuration(itemStack);
                    if (duration > 0) {
                        telebowTimers.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(duration));
                    }
                }
            }
        }
    }

    private int getTelebowDuration(ItemStack itemStack) {
        String displayName = itemStack.getDisplayName();
        if (displayName.contains("Telebow I")) return 90;
        if (displayName.contains("Telebow II")) return 45;
        if (displayName.contains("Telebow III")) return 20;
        return 0;
    }

    @SubscribeEvent
    public void onRenderGameOverlay(TickEvent.RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        long currentTime = System.currentTimeMillis();

        telebowTimers.entrySet().removeIf(entry -> currentTime >= entry.getValue());

        int y = 10;
        for (Map.Entry<String, Long> entry : telebowTimers.entrySet()) {
            String playerName = entry.getKey();
            long remainingTime = entry.getValue() - currentTime;
            String text = String.format("%s: %ds", playerName, TimeUnit.MILLISECONDS.toSeconds(remainingTime));
            renderingTelebow.renderTimer(text, 10, y, 0xFF55FF);
            y += 10;
        }
    }
}
