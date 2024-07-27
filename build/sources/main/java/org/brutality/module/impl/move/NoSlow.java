package org.brutality.module.impl.move;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import org.brutality.events.SlowDownEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;

public class NoSlow extends Module {
    private final SimpleModeSetting modeSetting;
    private boolean allowed;

    public NoSlow() {
        super("NoSlow", "Allows you to move at normal speed while using an item", Category.MOVEMENT);
        modeSetting = new SimpleModeSetting("Mode", this, "Vanilla", new String[]{"Vanilla", "Blink"});
        addSettings(modeSetting);
    }

    @SubscribeEvent
    public void onSlowDown(SlowDownEvent event) {
        if (modeSetting.is("Vanilla")) {
            event.setCanceled(true);
        } else if (modeSetting.is("Hypixel Blink")) {
            if (allowed) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = mc.thePlayer;
        if (player != null) {
            ItemStack item = player.getHeldItem();
            boolean usingItem = player.isUsingItem();

            if (usingItem && item != null) {
                allowed = !item.getItem().toString().contains("ItemPotion") && !item.getItem().toString().contains("ItemAppleGold");
            } else {
                allowed = true;
            }
        }
    }

    @SubscribeEvent
    public boolean onPacketSent(ClientCustomPacketEvent event) {
        if (modeSetting.is("Hypixel Blink")) {
            // Add the necessary packet handling code here
        }
        return true;
    }
}
