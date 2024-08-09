package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DarkList extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private final List<String> darkPeople = new ArrayList<>();
    private final List<String> temp = new ArrayList<>();
    private final List<String> DARK_ENCHANTS = Arrays.asList("Venom", "Misery", "Spite", "Needless Suffering", "Grim Reaper", "Nostalgia", "Hedge Fund", "Heartripper", "Sanguisuge", "Lycanthropy", "Mind Assault", "Golden Handcuffs");

    public DarkList() {
        super("Dark List", "Displays the people wearing dark pants in the lobby.", Category.PIT);
        addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }
        temp.clear();
        for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (entityPlayer == null || entityPlayer.inventory.armorInventory[1] == null || entityPlayer.inventory.armorInventory[1].getItem() == null) continue;
            String itemName = entityPlayer.inventory.armorInventory[1].getDisplayName();
            if (itemName == null || (!itemName.contains("Evil") && !itemName.contains("I Dark"))) continue;
            String name = entityPlayer.getDisplayName().getUnformattedText();
            String type = getDarkEnchantName(entityPlayer.inventory.armorInventory[1]);

            String location = entityPlayer.posY >= 85 ? "§a(Spawn)" : "§c(Down)";

            double distance = mc.thePlayer.getDistanceToEntity(entityPlayer);
            String distanceColor = getDistanceColor(distance);
            String playerInfo = String.format("%s Is Using %s %s(%s%.1fm)", name, type, location, distanceColor, distance);

            temp.add(playerInfo);
        }
        darkPeople.clear();
        darkPeople.addAll(temp);
    }

    private String getDarkEnchantName(ItemStack item) {
        NBTTagCompound tagCompound = item.getTagCompound();
        if (tagCompound != null) {
            for (String enchant : DARK_ENCHANTS) {
                if (tagCompound.toString().contains(enchant)) {
                    return "§d" + enchant;
                }
            }
        }
        return "§dDark";
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

            if (mc.currentScreen instanceof GuiChat) {
                handleDragging();
            }

            float posX = (float) this.xPos.getValue();
            float posY = (float) this.yPos.getValue();
            int indexTing = (int) posY;

            fr.drawStringWithShadow("Dark List:", posX, indexTing, 0xAA00FF);
            for (String playerInfo : darkPeople) {
                indexTing += fr.FONT_HEIGHT;
                fr.drawStringWithShadow(playerInfo, posX, indexTing, 0xFFFFFF);
            }
        }
    }

    private String getDistanceColor(double distance) {
        if (distance >= 70) {
            return "§a";
        } else if (distance >= 40) {
            return "§e";
        } else {
            return "§c";
        }
    }

    private void handleDragging() {
        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOver()) {
                dragging = true;
                dragX = Mouse.getX() / 2 - (int) xPos.getValue();
                dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - (int) yPos.getValue();
            }
            if (dragging) {
                xPos.setValue(Mouse.getX() / 2 - dragX);
                yPos.setValue(new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY);
            }
        } else {
            dragging = false;
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= xPos.getValue() && mouseX <= xPos.getValue() + 200 && mouseY >= yPos.getValue() && mouseY <= yPos.getValue() + 20;
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
