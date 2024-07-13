package org.brutality.module.impl.move;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Keyboard;

public class KeepSprint extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public KeepSprint() {
        super("KeepSprint", "Prevents slowing down when hitting entities", Category.MOVEMENT);
        this.setKey(Keyboard.KEY_K); // Set the default key for toggling the module, can be changed.
    }

    @Override
    public void onEnable() {
        // Code to execute when the module is enabled
    }

    @Override
    public void onDisable() {
        // Code to execute when the module is disabled
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        if (mc.thePlayer != null && mc.thePlayer.isSprinting()) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer != null && mc.thePlayer.isSprinting() && mc.thePlayer.hurtTime > 0) {
            mc.thePlayer.setSprinting(true);
        }
    }
}
