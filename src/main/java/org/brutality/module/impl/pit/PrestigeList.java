package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PrestigeList extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private final BooleanSetting showPrestige0 = new BooleanSetting("Show Prestige 0", this, true);
    private final BooleanSetting showPrestige1_4 = new BooleanSetting("Show Prestige 1-4", this, true);
    private final BooleanSetting showPrestige5_9 = new BooleanSetting("Show Prestige 5-9", this, true);
    private final BooleanSetting showPrestige10_14 = new BooleanSetting("Show Prestige 10-14", this, true);
    private final BooleanSetting showPrestige15_19 = new BooleanSetting("Show Prestige 15-19", this, true);
    private final BooleanSetting showPrestige20_24 = new BooleanSetting("Show Prestige 20-24", this, true);
    private final BooleanSetting showPrestige25_29 = new BooleanSetting("Show Prestige 25-29", this, true);
    private final BooleanSetting showPrestige30_34 = new BooleanSetting("Show Prestige 30-34", this, true);
    private final BooleanSetting showPrestige35_39 = new BooleanSetting("Show Prestige 35-39", this, true);

    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private final Minecraft mc = Minecraft.getMinecraft();

    public PrestigeList() {
        super("PrestigeList", "Lists players with specific prestige brackets and their armor type.", Category.PIT);
        addSettings(yPos, xPos, showPrestige0, showPrestige1_4, showPrestige5_9, showPrestige10_14, showPrestige15_19, showPrestige20_24, showPrestige25_29, showPrestige30_34, showPrestige35_39);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();

        if (mc.currentScreen != null) {
            handleDragging();
        }

        List<EntityPlayer> heretics = getHeretics();
        int y = (int) posY;
        for (EntityPlayer player : heretics) {
            String name = player.getDisplayName().getFormattedText();
            String armorType = getArmorType(player);
            String location = player.posY >= 85 ? "In Spawn" : "Down";

            int color = armorType.equals("Chain") ? Color.GRAY.getRGB() : Color.BLUE.getRGB();
            String displayText = String.format("%s - %s %s", name, armorType, getColorCodedLocation(location));
            mc.fontRendererObj.drawStringWithShadow(displayText, posX, y, color);
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    private List<EntityPlayer> getHeretics() {
        return mc.theWorld.playerEntities.stream()
                .filter(this::isHeretic)
                .collect(Collectors.toList());
    }

    private boolean isHeretic(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();
        return (showPrestige0.isEnabled() && name.contains("§7[")) ||
                (showPrestige1_4.isEnabled() && name.contains("§9[")) ||
                (showPrestige5_9.isEnabled() && name.contains("§e[")) ||
                (showPrestige10_14.isEnabled() && name.contains("§6[")) ||
                (showPrestige15_19.isEnabled() && name.contains("§c[")) ||
                (showPrestige20_24.isEnabled() && name.contains("§5[")) ||
                (showPrestige25_29.isEnabled() && name.contains("§d[")) ||
                (showPrestige30_34.isEnabled() && name.contains("§f[")) ||
                (showPrestige35_39.isEnabled() && name.contains("§b["));
    }

    private String getArmorType(EntityPlayer player) {
        for (ItemStack armor : player.getInventory()) {
            if (armor != null && armor.getItem() instanceof ItemArmor) {
                ItemArmor itemArmor = (ItemArmor) armor.getItem();
                if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.CHAIN) {
                    return "Chain";
                } else if (itemArmor.getArmorMaterial() == ItemArmor.ArmorMaterial.DIAMOND) {
                    return "Diamond";
                }
            }
        }
        return "None";
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

    private String getColorCodedLocation(String location) {
        if (location.equals("In Spawn")) {
            return "§aIn Spawn";
        } else {
            return "§cDown";
        }
    }
}
