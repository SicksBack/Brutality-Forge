package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
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
import net.minecraft.util.EnumChatFormatting;

public class FeastTimer extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private long cooldownEndTime = 0;
    private TimerUtils timer = new TimerUtils();
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private boolean timerActive = false; // Flag to track if the timer is active

    public FeastTimer() {
        super("FeastTimer", "Displays Feast cooldown.", Category.PIT);
        this.addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the interaction is a right-click on a block
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            ItemStack itemStack = Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem();
            if (itemStack != null && itemStack.getItem() == Item.getItemById(423)) { // 423 is the ID for mutton
                cooldownEndTime = System.currentTimeMillis() + 11 * 1000; // 11 seconds cooldown
                timer.reset();
                timerActive = true; // Set the timer as active
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

        if (timerActive) {
            if (cooldownEndTime > System.currentTimeMillis()) {
                long timeLeft = (cooldownEndTime - System.currentTimeMillis()) / 1000;
                if (timeLeft < 1) timeLeft = 1; // Ensure the timer is at least 1 second
                String text = EnumChatFormatting.WHITE + "Feast: " + EnumChatFormatting.GOLD + timeLeft; // White for "Feast:" and orange for the timer
                mc.fontRendererObj.drawStringWithShadow(text, posX, posY, 0xFFFFFF); // Draw with white shadow
            } else {
                timerActive = false; // Deactivate the timer when cooldown ends
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // Handle chat events if necessary, for now we'll just leave it empty
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth(EnumChatFormatting.WHITE + "Feast: " + EnumChatFormatting.GOLD + (cooldownEndTime - System.currentTimeMillis()) / 1000);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
