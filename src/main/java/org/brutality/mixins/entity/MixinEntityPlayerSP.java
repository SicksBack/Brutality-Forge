package org.brutality.mixins.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.brutality.BrutalityClient;
import org.brutality.events.listeners.EventChat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    @Shadow
    public int sprintingTicksLeft;

    public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    @Override
    @Shadow
    public abstract void setSprinting(boolean p_setSprinting_1_);

    @Shadow
    protected int sprintToggleTimer;
    @Shadow
    public float prevTimeInPortal;
    @Shadow
    public float timeInPortal;
    @Shadow
    protected Minecraft mc;
    @Shadow
    public MovementInput movementInput;

    @Override
    @Shadow
    public abstract void sendPlayerAbilities();

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Shadow
    public abstract boolean isRidingHorse();

    @Shadow
    private int horseJumpPowerCounter;
    @Shadow
    private float horseJumpPower;

    @Shadow
    protected abstract void sendHorseJump();

    @Shadow
    private boolean serverSprintState;
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;

    @Override
    @Shadow
    public abstract boolean isSneaking();

    @Shadow
    private boolean serverSneakState;
    @Shadow
    private double lastReportedPosX;
    @Shadow
    private double lastReportedPosY;
    @Shadow
    private double lastReportedPosZ;
    @Shadow
    private float lastReportedYaw;
    @Shadow
    private float lastReportedPitch;
    @Shadow
    private int positionUpdateTicks;

    // Using obfuscated method name for 1.8.9
    @Inject(method = "func_71165_d", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci) {
        EventChat event = new EventChat(message);
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCancelled()) {
            ci.cancel();
            return;
        }

        if (message.startsWith(".")) {
            BrutalityClient.getInstance().getCommandManager().handleChat(message);
            EntityPlayerSP player = (EntityPlayerSP) (Object) this;
            player.addChatMessage(new ChatComponentText("Clientside Command: " + message));
            ci.cancel();
        }
    }
}