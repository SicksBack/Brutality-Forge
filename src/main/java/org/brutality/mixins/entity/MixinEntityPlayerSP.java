package org.brutality.mixins.entity;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ChatComponentText;
import org.brutality.BrutalityClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci) {

        if (message.startsWith(".")) {

            BrutalityClient.getInstance().getCommandManager().handleChat(message);


            ci.cancel();


            EntityPlayerSP player = (EntityPlayerSP) (Object) this;
            player.addChatMessage(new ChatComponentText("Clientside Command: " + message));
        }
    }
}
