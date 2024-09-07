package org.brutality.module.impl.movement;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.Wrapper;

public class KeepSprint extends Module {

    public KeepSprint() {
        super("KeepSprint", "Keeps you sprinting even when hitting players", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (this.isToggled() && Wrapper.getPlayer() != null) {
            Wrapper.getPlayer().setSprinting(true);
        }
    }
}
