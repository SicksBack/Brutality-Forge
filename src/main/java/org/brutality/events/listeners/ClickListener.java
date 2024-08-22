package org.brutality.events.listeners;

import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClickListener {
    private static final List<Long> clicks = new ArrayList<>();
    private boolean hasClickedThisTick = false;

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        if (event.button != 0) {
            return;
        }
        if (event.buttonstate && !hasClickedThisTick) {
            this.hasClickedThisTick = true;
            addClick();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        this.hasClickedThisTick = false;
    }

    public static void addClick() {
        clicks.add(System.currentTimeMillis());
    }

    public static int getClicks() {
        long currentTime = System.currentTimeMillis();
        clicks.removeIf(clickTime -> clickTime < currentTime - 1000L);
        return clicks.size();
    }
}
