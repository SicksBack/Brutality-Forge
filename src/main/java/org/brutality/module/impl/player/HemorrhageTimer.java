package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

public class HemorrhageTimer extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private long cooldownEndTime = 0;
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public HemorrhageTimer() {
        super("HemorrhageTimer", "Displays Hemorrhage cooldown.", Category.PLAYER);
        this.addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onAttackEntity(AttackEntityEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        ItemStack itemStack = player.getCurrentEquippedItem();

        if (itemStack != null && itemStack.hasDisplayName() && event.target instanceof EntityPlayer) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound displayTag = tagCompound.getCompoundTag("display");
                if (displayTag.hasKey("Lore", 9)) {
                    NBTTagList loreList = displayTag.getTagList("Lore", 8);
                    boolean hasHemorrhage = false;

                    for (int i = 0; i < loreList.tagCount(); ++i) {
                        String lore = loreList.getStringTagAt(i);
                        if (lore.contains("Hemorrhage")) {
                            hasHemorrhage = true;
                            break;
                        }
                    }

                    if (hasHemorrhage && tagCompound.hasKey("Key", 3)) {
                        int keyValue = tagCompound.getInteger("Key");
                        if (cooldownEndTime <= System.currentTimeMillis()) {
                            switch (keyValue) {
                                case 1:
                                    cooldownEndTime = System.currentTimeMillis() + 2 * 1000; // Hemorrhage I
                                    break;
                                case 2:
                                    cooldownEndTime = System.currentTimeMillis() + 4 * 1000; // Hemorrhage II
                                    break;
                                case 3:
                                    cooldownEndTime = System.currentTimeMillis() + 6 * 1000; // Hemorrhage III
                                    break;
                                default:
                                    cooldownEndTime = System.currentTimeMillis() + 6 * 1000; // Default to Hemorrhage III
                                    break;
                            }
                        }
                    }
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

        if (cooldownEndTime > System.currentTimeMillis()) {
            long timeLeft = (cooldownEndTime - System.currentTimeMillis()) / 1000;
            mc.fontRendererObj.drawStringWithShadow("Hemorrhage: ", posX, posY, 0xFFFFFF); // White text for "Hemorrhage:"
            mc.fontRendererObj.drawStringWithShadow(String.valueOf(timeLeft), posX + mc.fontRendererObj.getStringWidth("Hemorrhage: "), posY, 0x8B0000); // Dark red color for the timer
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (event.message.getUnformattedText().contains("DEATH!")) {
            cooldownEndTime = 0;
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Hemorrhage: " + (cooldownEndTime - System.currentTimeMillis()) / 1000);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
