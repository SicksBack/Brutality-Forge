package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class SharkDamage extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private String sharkText = "Shark: 0%";

    public SharkDamage() {
        super("SharkDamage", "Displays Shark damage percentage.", Category.RENDER);
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

        if (isHoldingSharkSword(mc.thePlayer)) {
            updateSharkDamageText(mc.thePlayer);
            mc.fontRendererObj.drawStringWithShadow("Shark: ", posX, posY, 0xFFFFFF);
            mc.fontRendererObj.drawStringWithShadow(sharkText, posX + mc.fontRendererObj.getStringWidth("Shark: "), posY, 0xFF5555);
        }
    }

    private void updateSharkDamageText(EntityPlayerSP player) {
        ItemStack itemStack = player.getHeldItem();
        if (itemStack != null && itemStack.hasDisplayName()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null) {
                int sharkLevel = 0;
                if (tagCompound.toString().contains("Shark III")) {
                    sharkLevel = 3;
                } else if (tagCompound.toString().contains("Shark II")) {
                    sharkLevel = 2;
                } else if (tagCompound.toString().contains("Shark")) {
                    sharkLevel = 1;
                }

                if (sharkLevel > 0) {
                    int nearbyPlayers = countPlayersBelowSixHearts(player);
                    int percentage = 0;
                    if (sharkLevel == 1) {
                        percentage = 2 * nearbyPlayers;
                    } else if (sharkLevel == 2) {
                        percentage = 4 * nearbyPlayers;
                    } else if (sharkLevel == 3) {
                        percentage = 7 * nearbyPlayers;
                    }
                    sharkText = percentage + "%";
                } else {
                    sharkText = "0%";
                }
            } else {
                sharkText = "0%";
            }
        } else {
            sharkText = "0%";
        }
    }

    private boolean isHoldingSharkSword(EntityPlayerSP player) {
        ItemStack itemStack = player.getHeldItem();
        if (itemStack != null && itemStack.hasDisplayName()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null) {
                return tagCompound.toString().contains("Shark");
            }
        }
        return false;
    }

    private int countPlayersBelowSixHearts(EntityPlayerSP player) {
        int count = 0;
        for (EntityPlayer entityPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (entityPlayer != player && entityPlayer.getDistanceToEntity(player) <= 12 && entityPlayer.getHealth() < 12.0f) {
                count++;
            }
        }
        return count;
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Shark: " + sharkText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
