package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;

public class TelebowTimer extends Module {

    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 50, 0, 1200, 1);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5, 0, 1200, 1);

    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private TimerUtils timer = new TimerUtils();
    private int cooldown = 0;
    private boolean cooldownActive = false;

    public TelebowTimer() {
        super("TelebowTimer", "Displays a countdown for Telebow shots", Category.PIT);
        addSettings(xPosSetting, yPosSetting);
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (event.entityPlayer == player) {
            ItemStack itemStack = event.bow;
            if (itemStack != null && itemStack.hasDisplayName() && itemStack.getTagCompound() != null) {
                String displayName = itemStack.getDisplayName();
                if (displayName.contains("Telebow") && itemStack.getTagCompound().toString().contains("telebow")) {
                    if (cooldownActive) return;
                    if (itemStack.getTagCompound().toString().contains("telebow:3")) {
                        cooldown = 20; // Telebow III
                    } else if (itemStack.getTagCompound().toString().contains("telebow:2")) {
                        cooldown = 45; // Telebow II
                    } else {
                        cooldown = 90; // Telebow
                    }
                    timer.reset();
                    cooldownActive = true;
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution sr = new ScaledResolution(mc);
        float posX = (float) xPosSetting.getValue();
        float posY = (float) yPosSetting.getValue();

        if (mc.currentScreen != null) {
            if (dragging) {
                posX = Mouse.getX() / sr.getScaleFactor() - dragX;
                posY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - dragY;
                xPosSetting.setValue((int) posX);
                yPosSetting.setValue((int) posY);
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverText(Mouse.getX() / sr.getScaleFactor(), (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor(), "Telebow: " + cooldown, mc)) {
                    dragging = true;
                    dragX = Mouse.getX() / sr.getScaleFactor() - (int) posX;
                    dragY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - (int) posY;
                }
            } else {
                dragging = false;
            }
        }

        if (cooldownActive && timer.hasReached(cooldown * 1000L)) {
            cooldownActive = false;
        } else if (cooldownActive) {
            int secondsLeft = (int) ((cooldown * 1000L - timer.getTimePassed()) / 1000);
            String text = "Telebow: " + secondsLeft;
            mc.fontRendererObj.drawStringWithShadow(text, posX, posY, 0xFF55FF);
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, String text, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= xPosSetting.getValue() && mouseX <= xPosSetting.getValue() + textWidth
                && mouseY >= yPosSetting.getValue() && mouseY <= yPosSetting.getValue() + textHeight;
    }
}
