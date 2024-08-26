package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

public class MindAssaultDamage extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private final BooleanSetting showReduction = new BooleanSetting("Show Reduction", this, true);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private String mindsText = "";

    public MindAssaultDamage() {
        super("MindAssaultDamage", "Displays Mind Assault damage and stats.", Category.PIT);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;

        if (mc.currentScreen instanceof GuiChat) {
            if (dragging) {
                this.xPos.setValue(Mouse.getX() / 2 - dragX);
                this.yPos.setValue((mc.displayHeight - Mouse.getY()) / 2 - dragY);
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, mc)) {
                    dragging = true;
                    dragX = Mouse.getX() / 2 - (int) xPos.getValue();
                    dragY = (mc.displayHeight - Mouse.getY()) / 2 - (int) yPos.getValue();
                }
            } else {
                dragging = false;
            }
        }

        if (isWearingMindAssault(player)) {
            updateMindsText(player);
            int color = 0xFFFFFF; // White color for "Minds:"
            int reductionColor = showReduction.isEnabled() ? 0xA020F0 : color; // Purple color for reduction
            int heartsColor = 0xFF0000; // Red color for hearts

            mc.fontRendererObj.drawStringWithShadow("Minds: ", (int) xPos.getValue(), (int) yPos.getValue(), color);
            mc.fontRendererObj.drawStringWithShadow(mindsText, (int) xPos.getValue() + mc.fontRendererObj.getStringWidth("Minds: "), (int) yPos.getValue(), heartsColor);
        }
    }

    private void updateMindsText(EntityPlayerSP player) {
        int nearbyPlayers = countNearbyPlayersWearingLeggings(player);
        double mindsValue = 0.00 + (0.75 * nearbyPlayers);
        String reductionText = showReduction.isEnabled() ? "§5-60% " : "";
        mindsText = reductionText + String.format("+%.2f ❤", mindsValue);
    }

    private boolean isWearingMindAssault(EntityPlayerSP player) {
        for (ItemStack armorStack : player.inventory.armorInventory) {
            if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
                if (armorStack.hasDisplayName()) {
                    NBTTagCompound tagCompound = armorStack.getTagCompound();
                    if (tagCompound != null && tagCompound.hasKey("Mind Assault")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int countNearbyPlayersWearingLeggings(EntityPlayerSP player) {
        int count = 0;
        for (EntityPlayer entityPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (entityPlayer != player && entityPlayer.getDistanceToEntity(player) <= 11) {
                ItemStack leggings = entityPlayer.getEquipmentInSlot(2); // Slot index 2 is for leggings
                if (leggings != null && leggings.getItem() == Item.getItemById(7)) { // Item ID 7 is for leather leggings
                    count++;
                }
            }
        }
        return count;
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Minds: " + mindsText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + textWidth
                && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + textHeight;
    }
}
