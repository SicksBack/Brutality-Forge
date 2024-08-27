package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

        // Handle dragging of text on screen if in chat
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

        // Display Mind Assault stats if wearing appropriate armor
        if (isWearingMindAssault(player)) {
            updateMindsText(player);
            int color = 0xFFFFFF; // White color for "Minds:"
            int heartsColor = 0xFF0000; // Red color for hearts

            mc.fontRendererObj.drawStringWithShadow("Minds: ", (int) xPos.getValue(), (int) yPos.getValue(), color);
            mc.fontRendererObj.drawStringWithShadow(mindsText, (int) xPos.getValue() + mc.fontRendererObj.getStringWidth("Minds: "), (int) yPos.getValue(), heartsColor);
        } else {
            // Debugging message if not wearing Mind Assault
            mc.fontRendererObj.drawStringWithShadow("Not wearing Mind Assault", (int) xPos.getValue(), (int) yPos.getValue() + 20, 0xFF0000);
        }
    }

    private void updateMindsText(EntityPlayerSP player) {
        int nearbyPlayers = countNearbyPlayersWearingLeggings(player);
        double mindsValue = 0.00 + (0.75 * nearbyPlayers);
        String reductionText = showReduction.isEnabled() ? "\u00A75-60% " : ""; // Purple color for -60%
        mindsText = reductionText + String.format("\u00A74+%.2f \u2764", mindsValue); // Red color for +X.XX ❤
    }

    // Check if the player is wearing Mind Assault armor using NBT tags
    private boolean isWearingMindAssault(EntityPlayerSP player) {
        for (ItemStack armorStack : player.inventory.armorInventory) {
            if (armorStack != null && armorStack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = armorStack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display")) {
                    NBTTagCompound displayTag = tagCompound.getCompoundTag("display");
                    if (displayTag.hasKey("Lore")) {
                        NBTTagList loreList = displayTag.getTagList("Lore", 8); // 8 is the ID for string type
                        for (int i = 0; i < loreList.tagCount(); i++) {
                            String loreEntry = loreList.getStringTagAt(i);
                            if (loreEntry.contains("Mind Assault")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    // Count the number of nearby players wearing leather leggings (name check) within 11 meters
    private int countNearbyPlayersWearingLeggings(EntityPlayerSP player) {
        int count = 0;
        for (EntityPlayer entityPlayer : Minecraft.getMinecraft().theWorld.playerEntities) {
            if (entityPlayer != player && entityPlayer.getDistanceToEntity(player) <= 11) {
                ItemStack leggings = entityPlayer.getEquipmentInSlot(2); // Slot index 2 is for leggings
                if (leggings != null && leggings.getItem() != null && leggings.getItem().getUnlocalizedName().contains("leather_leggings")) {
                    count++;
                }
            }
        }
        return count;
    }

    // Check if the mouse is over the text for dragging purposes
    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Minds: " + mindsText);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + textWidth
                && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + textHeight;
    }
}
