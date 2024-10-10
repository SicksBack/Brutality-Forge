package org.brutality.mixins.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.movement.KeepSprint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer {

    // Using the obfuscated name of attackTargetEntityWithCurrentItem (func_71059_n)
    @Inject(method = "func_71059_n", at = @At("HEAD"))
    public void onAttack(Entity target, CallbackInfo ci) {
        // Check if the KeepSprint module is toggled on
        KeepSprint keepSprint = ModuleManager.getInstance().getModuleByClass(KeepSprint.class);
        if (keepSprint != null && keepSprint.isToggled()) {
            // Keep the player sprinting
            ((EntityPlayer) (Object) this).setSprinting(true);
        }
    }
}
