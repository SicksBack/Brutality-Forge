package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
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
import java.util.List;

public class SetList extends Module {

    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 80.0, 0.0, 1200.0, 1);
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private final List<String> regularityPlayers = new ArrayList<>();

    public SetList() {
        super("SetList", "Displays the players using Regularity in the lobby.", Category.PIT);
        addSettings(yPos, xPos);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            if (mc.thePlayer == null) {
                return;
            }
            regularityPlayers.clear();
            for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
                if (entityPlayer == null) continue;
                for (ItemStack armor : entityPlayer.inventory.armorInventory) {
                    if (armor != null && armor.hasTagCompound()) {
                        NBTTagCompound tagCompound = armor.getTagCompound();
                        if (tagCompound != null && tagCompound.toString().contains("Regularity")) {
                            String regularityLevel = getRegularityLevel(tagCompound);
                            String name = entityPlayer.getDisplayName().getFormattedText();
                            String location = entityPlayer.posY >= 85 ? "§a(Spawn)" : "§c(Down)";
                            double distance = mc.thePlayer.getDistanceToEntity(entityPlayer);
                            String distanceColor = getDistanceColor(distance);
                            String playerInfo = String.format("%s Is Using %s %s(%s%.1fm)", name, regularityLevel, location, distanceColor, distance);

                            regularityPlayers.add(playerInfo);
                            break;
                        }
                    }
                }
            }
        }
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

            fr.drawStringWithShadow("Set List:", posX, indexTing, 0xFF0000);
            for (String playerInfo : regularityPlayers) {
                indexTing += fr.FONT_HEIGHT;
                fr.drawStringWithShadow(playerInfo, posX, indexTing, 0xFFFFFF);
            }
        }
    }

    private String getRegularityLevel(NBTTagCompound tagCompound) {
        if (tagCompound.toString().contains("Regularity III")) {
            return "§4Regularity III";
        } else if (tagCompound.toString().contains("Regularity II")) {
            return "§4Regularity II";
        } else {
            return "§4Regularity I";
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
}
