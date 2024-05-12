package org.brutality.injection.mixins;

import org.brutality.BrutalityClient;
import org.brutality.injection.interfaces.IMixinMinecraft;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mutable;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@SideOnly(Side.CLIENT)
@Mixin({ Minecraft.class })
public abstract class MixinMinecraft implements IMixinMinecraft
{
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    private Timer timer;
    @Shadow
    public int rightClickDelayTimer;
    @Shadow
    @Mutable
    @Final
    private Session session;
    @Shadow
    private int leftClickCounter;
    
    @Shadow
    protected abstract void clickMouse();
    
    @Override
    public void runCrinkMouse() {
        this.clickMouse();
    }
    
    @Override
    public void setClickCounter(final int a) {
        this.leftClickCounter = a;
    }
    
    @Inject(method = "startGame", at = { @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER) })
    private void startGame(final CallbackInfo ci) {
        Display.setTitle("Brutality");
        BrutalityClient.INSTANCE.initiate();
    }

    @Override
    public Timer getTimer() {
        return this.timer;
    }
    
    @Override
    public Session getSession() {
        return this.session;
    }
    
    @Override
    public void setSession(final Session session) {
        this.session = session;
    }
}