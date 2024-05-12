package org.brutality.injection.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import org.brutality.api.EventBus;
import org.brutality.api.events.EventRender2D;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.Gui;

@SideOnly(Side.CLIENT)
@Mixin({ GuiIngame.class })
public class MixinGuiIngame extends Gui
{
    public Minecraft mc;
    @Shadow
    public String displayedTitle;
    @Shadow
    public String displayedSubTitle;
    @Shadow
    public int titlesTimer;
    @Shadow
    public int titleFadeIn;
    @Shadow
    public int titleDisplayTime;
    @Shadow
    public int titleFadeOut;
    
    public MixinGuiIngame() {
        this.displayedTitle = "";
        this.displayedSubTitle = "";
    }
    

    @Inject(method = "renderGameOverlay", at = { @At("RETURN") }, cancellable = true)
    private void renderGameOverlay(float partialTicks, CallbackInfo ci) {
        ScaledResolution scaledresolution = new ScaledResolution(this.mc);
        EventBus.getInstance().call(new EventRender2D(scaledresolution, partialTicks));
    }




    @Inject(method = "renderTooltip", at = { @At("HEAD") }, cancellable = true)
    private void renderTooltip(ScaledResolution sr, float partialTicks, CallbackInfo ci) {
        EventBus.getInstance().call(new EventRender2D(sr, partialTicks));
    }
}