package org.brutality.module.impl.player;

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
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

import java.util.HashMap;
import java.util.Map;

public class MindAssaultDamage extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private final BooleanSetting showReduction = new BooleanSetting("Show Reduction", this, true);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private String mindsText = "";
    private double totalDamage = 0.0;
    private boolean shouldDisplay = false;
    private final Map<String, Boolean> playersWithLeather = new HashMap<>();

    public MindAssaultDamage() {
        super("MindAssaultDamage", "Displays Mind Assault damage and stats.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Minecraft.getMinecraft().addScheduledTask(() -> {
            if (Minecraft.getMinecraft().currentScreen instanceof GuiChat) {
                dragging = false; // Ensure dragging state is reset when the module is enabled
            }
        });
    }

    @Override
    public void onDisable() {
        super.onDisable();
        dragging = false; // Ensure dragging state is reset when the module is disabled
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

        if (shouldDisplay) {
            updateMindsText(player);
            int whiteColor = 0xFFFFFF; // White color for "Minds:"
            int redColor = 0xFF6666; // Red color for damage number
            int maxedColor = 0x00FFFF; // Cyan color for "MAXED!"

            // Displaying text with updated totalDamage value
            mc.fontRendererObj.drawStringWithShadow("Minds: ", (int) xPos.getValue(), (int) yPos.getValue(), whiteColor);
            mc.fontRendererObj.drawStringWithShadow(mindsText, (int) xPos.getValue() + mc.fontRendererObj.getStringWidth("Minds: "), (int) yPos.getValue(), redColor);

            if (totalDamage >= 8.00) {
                String maxedText = " MAXED!";
                mc.fontRendererObj.drawStringWithShadow(maxedText, (int) xPos.getValue() + mc.fontRendererObj.getStringWidth("Minds: " + mindsText), (int) yPos.getValue(), maxedColor);
            }
        } else {
            // Debugging message if not wearing Mind Assault
            mc.fontRendererObj.drawStringWithShadow("Not wearing Mind Assault", (int) xPos.getValue(), (int) yPos.getValue() + 20, 0xFF0000);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player == Minecraft.getMinecraft().thePlayer && event.phase == TickEvent.Phase.END) {
            this.shouldDisplay = checkForMindAssault((EntityPlayer) Minecraft.getMinecraft().thePlayer);
            if (this.shouldDisplay) {
                this.totalDamage = 0.0;
                this.playersWithLeather.clear();
                for (EntityPlayer player : Minecraft.getMinecraft().theWorld.playerEntities) {
                    double distanceSq;
                    if (player == Minecraft.getMinecraft().thePlayer || !((distanceSq = player.getDistanceToEntity(Minecraft.getMinecraft().thePlayer)) <= 11.0)) continue;
                    boolean hasLeatherArmor = checkForLeatherArmor(player);
                    this.playersWithLeather.put(player.getName(), hasLeatherArmor);
                    if (hasLeatherArmor) {
                        this.totalDamage += 0.75;
                        if (this.totalDamage >= 8.0) {
                            this.totalDamage = 8.0;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void updateMindsText(EntityPlayerSP player) {
        int nearbyPlayers = countNearbyPlayersWearingLeggings(player);
        double mindsValue = Math.min(8.00, 0.75 * nearbyPlayers);
        String reductionText = showReduction.isEnabled() ? "\u00A75-60% " : ""; // Purple color for -60%
        mindsText = reductionText + String.format("\u00A74+%.2f \u2764", mindsValue); // Red color for +X.XX ❤
    }

    private boolean checkForMindAssault(EntityPlayer player) {
        NBTTagCompound displayTag;
        NBTTagCompound tagCompound;
        ItemStack pants = player.inventory.armorInventory[1]; // Slot index 1 is for leggings
        if (pants != null && pants.getItem() instanceof ItemArmor && (tagCompound = pants.getTagCompound()) != null && tagCompound.hasKey("display", 10) && (displayTag = tagCompound.getCompoundTag("display")).hasKey("Lore", 9)) {
            NBTTagList loreList = displayTag.getTagList("Lore", 8);
            for (int i = 0; i < loreList.tagCount(); ++i) {
                String lore = loreList.getStringTagAt(i);
                if (lore.contains("Mind Assault")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkForLeatherArmor(EntityPlayer player) {
        for (ItemStack itemStack : player.inventory.armorInventory) {
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                // Check if the item is leather armor
                if (itemStack.getItem().equals(Item.getItemById(298)) || // Leather Helmet
                        itemStack.getItem().equals(Item.getItemById(299)) || // Leather Chestplate
                        itemStack.getItem().equals(Item.getItemById(300)) || // Leather Pants
                        itemStack.getItem().equals(Item.getItemById(301))) { // Leather Boots
                    return true;
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
                if (leggings != null && leggings.getItem() instanceof ItemArmor && ((ItemArmor) leggings.getItem()).armorType == 2) { // Check if the item is leggings
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
