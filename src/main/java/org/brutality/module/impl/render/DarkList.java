package org.brutality.module.impl.render;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Mouse;

public class DarkList extends Module {
    private final ArrayList<String> darkPeople = new ArrayList<>();
    private final ArrayList<String> temp = new ArrayList<>();
    private final List<String> DARK_ENCHANTS = Arrays.asList("Venom", "Misery", "Spite", "Needless Suffering", "Grim Reaper", "Nostalgia", "Hedge Fund", "Heartripper", "Sanguisuge", "Lycanthropy", "Mind Assault", "Golden Handcuffs");

    private int darkListX = 10;  // Initial X position
    private int darkListY = 10;  // Initial Y position
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public DarkList() {
        super("Dark List", "Displays the people wearing dark pants in the lobby.", Category.RENDER);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent e) {
        if (mc.thePlayer == null) {
            return;
        }
        temp.clear();
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer == null || entityPlayer.inventory.armorInventory[1] == null || entityPlayer.inventory.armorInventory[1].getItem() != Item.getItemById(300)) continue;
            String itemName = entityPlayer.inventory.armorInventory[1].getDisplayName();
            if (itemName == null || (!itemName.contains("Evil") && !itemName.contains("I Dark"))) continue;
            String name = entityPlayer.getGameProfile().getName();  // Real name without custom colors
            String type = "";
            String nbtData = entityPlayer.inventory.armorInventory[1].serializeNBT().toString();
            if (nbtData.contains("Tier I ")) {
                type = "Plain Darks";
            } else {
                for (String enchant : DARK_ENCHANTS) {
                    if (nbtData.contains(enchant)) {
                        type = enchant;
                        break;
                    }
                }
            }

            // Determine location
            String location = "OutSkirts";
            if (entityPlayer.posY == 95) {
                location = "In Spawn";
            } else if (isInMid(entityPlayer)) {
                location = "In Mid";
            }

            // Calculate distance
            double distance = Math.sqrt(Math.pow(entityPlayer.posX - mc.thePlayer.posX, 2) + Math.pow(entityPlayer.posY - mc.thePlayer.posY, 2) + Math.pow(entityPlayer.posZ - mc.thePlayer.posZ, 2));
            String distanceStr = String.format("%.1f", distance);

            if (!temp.contains(name + " - " + type + " " + location + " (" + distanceStr + "m)")) {
                temp.add(name + " - " + type + " " + location + " (" + distanceStr + "m)");
            }
        }
        darkPeople.clear();
        darkPeople.addAll(temp);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);
            FontRenderer fr = mc.fontRendererObj;

            if (dragging) {
                darkListX = Mouse.getX() / sr.getScaleFactor() - dragX;
                darkListY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - dragY;
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverList(Mouse.getX() / sr.getScaleFactor(), (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor(), fr)) {
                    dragging = true;
                    dragX = Mouse.getX() / sr.getScaleFactor() - darkListX;
                    dragY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - darkListY;
                }
            } else {
                dragging = false;
            }

            float pos = darkListY;
            float posX = darkListX;
            int indexTing = (int) pos;
            fr.drawStringWithShadow("Dark List:", posX, indexTing, 0xAA00FF);
            for (String entityData : darkPeople) {
                String[] parts = entityData.split(" - ");
                String playerName = parts[0];
                String restOfText = " - " + parts[1];

                fr.drawStringWithShadow(playerName, posX, indexTing + fr.FONT_HEIGHT, 0xFFFFFF);
                fr.drawStringWithShadow(restOfText, posX + fr.getStringWidth(playerName), indexTing + fr.FONT_HEIGHT, 0xAA00FF);

                indexTing += fr.FONT_HEIGHT;
            }
        }
    }

    private boolean isMouseOverList(int mouseX, int mouseY, FontRenderer fr) {
        int textHeight = fr.FONT_HEIGHT * (darkPeople.size() + 1);
        int textWidth = 100; // Adjust as necessary
        return mouseX >= darkListX && mouseX <= darkListX + textWidth
                && mouseY >= darkListY && mouseY <= darkListY + textHeight;
    }

    private boolean isInMid(EntityPlayer player) {
        // Define the coordinates range for the Hypixel Pit mid
        double midX1 = -10;
        double midX2 = 10;
        double midZ1 = -10;
        double midZ2 = 10;
        return player.posX > midX1 && player.posX < midX2 && player.posZ > midZ1 && player.posZ < midZ2;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}
