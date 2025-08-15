package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class PullbowTimer extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private long cooldownEndTime = 0;
    private TimerUtils timer = new TimerUtils();
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private boolean shotHitPlayer = false;

    public PullbowTimer() {
        super("PullbowTimer", "Displays Pullbow cooldown.", Category.RENDER);
        this.addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onArrowLoose(ArrowLooseEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.getHeldItem();

        if (itemStack != null && itemStack.hasDisplayName()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();

            // Check if the NBT tag contains "Pullbow"
            if (tagCompound != null && tagCompound.toString().contains("Pullbow")) {
                // Ray trace to check if the arrow hit a player
                Vec3 lookVec = player.getLookVec();
                Vec3 startVec = player.getPositionEyes(1.0F);
                Vec3 endVec = startVec.addVector(lookVec.xCoord * 100, lookVec.yCoord * 100, lookVec.zCoord * 100); // Adjust range if needed
                MovingObjectPosition rayTraceResult = player.worldObj.rayTraceBlocks(startVec, endVec, false, true, false);

                if (rayTraceResult != null && rayTraceResult.entityHit instanceof EntityPlayer) {
                    shotHitPlayer = true;
                    // Start cooldown timer
                    cooldownEndTime = System.currentTimeMillis() + 8 * 1000; // 8 seconds cooldown
                } else {
                    shotHitPlayer = false;
                }
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

        if (cooldownEndTime > System.currentTimeMillis() && shotHitPlayer) {
            long timeLeft = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            timeLeft = timeLeft == 0 ? 1 : timeLeft; // Ensure timer shows 1 instead of 0
            mc.fontRendererObj.drawStringWithShadow("Pullbow: ", posX, posY, 0xFFFFFF); // White color for "Pullbow:"
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(timeLeft), posX + mc.fontRendererObj.getStringWidth("Pullbow: "), posY, 0x00FFFF); // Cyan color for the timer
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains("DEATH!")) {
            cooldownEndTime = 0;
            shotHitPlayer = false;
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Pullbow: " + (cooldownEndTime - System.currentTimeMillis()) / 1000);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
