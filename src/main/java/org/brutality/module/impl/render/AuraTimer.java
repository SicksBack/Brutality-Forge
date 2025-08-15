package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class AuraTimer extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private long cooldownEndTime = 0;
    private TimerUtils timer = new TimerUtils();
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public AuraTimer() {
        super("AuraTimer", "Displays Aura cooldown.", Category.RENDER);
        this.addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.inventory.getCurrentItem();

        // Check for right-click actions
        if ((event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK)
                && itemStack != null && itemStack.getItem() == Item.getItemById(341)) { // 341 is the ID for slime_ball
            cooldownEndTime = System.currentTimeMillis() + 15 * 1000; // 15 seconds
            timer.reset();
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
            if (timeLeft > 0) {
                String text = "Aura: ";
                String timerText = String.format("%02d", timeLeft);
                mc.fontRendererObj.drawStringWithShadow(text + timerText, posX, posY, 0xFFFFFF);
                mc.fontRendererObj.drawStringWithShadow(timerText, posX + mc.fontRendererObj.getStringWidth(text), posY, 0x00FF00);
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains("DEATH!")) {
            cooldownEndTime = 0;
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        String timerText = String.format("%02d", (cooldownEndTime - System.currentTimeMillis()) / 1000);
        int textWidth = mc.fontRendererObj.getStringWidth("Aura: " + timerText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
