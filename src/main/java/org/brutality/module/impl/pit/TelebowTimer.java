package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class TelebowTimer extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private long cooldownEndTime = 0;
    private TimerUtils timer = new TimerUtils();
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public TelebowTimer() {
        super("TelebowTimer", "Displays Telebow cooldown.", Category.PIT);
        this.addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.getHeldItem();
        if (itemStack != null && itemStack.hasDisplayName() && System.currentTimeMillis() > cooldownEndTime && player.isSneaking()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.toString().contains("telebow")) {
                if (tagCompound.toString().contains("telebow:3")) {
                    cooldownEndTime = System.currentTimeMillis() + 21 * 1000; // Telebow III
                } else if (tagCompound.toString().contains("telebow:2")) {
                    cooldownEndTime = System.currentTimeMillis() + 46 * 1000; // Telebow II
                } else {
                    cooldownEndTime = System.currentTimeMillis() + 91 * 1000; // Telebow
                }
                timer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();

        if (mc.currentScreen instanceof GuiChat) {
            if (dragging) {
                this.xPos.setValue(Mouse.getX() / 2 - dragX);
                this.yPos.setValue((mc.displayHeight - Mouse.getY()) / 2 - dragY);
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, mc)) {
                    dragging = true;
                    dragX = Mouse.getX() / 2 - (int) posX;
                    dragY = (mc.displayHeight - Mouse.getY()) / 2 - (int) posY;
                }
            } else {
                dragging = false;
            }
        }

        if (cooldownEndTime > System.currentTimeMillis()) {
            long timeLeft = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            String text = "Telebow: " + timeLeft;
            mc.fontRendererObj.drawStringWithShadow("Telebow: ", posX, posY, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(timeLeft), posX + mc.fontRendererObj.getStringWidth("Telebow: "), posY, 0xFF55FF);
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains("DEATH!")) {
            cooldownEndTime = 0;
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Telebow: " + (cooldownEndTime - System.currentTimeMillis()) / 1000);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
