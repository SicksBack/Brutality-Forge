package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class VenomList extends Module {

    private final List<String> venomedPlayers = new ArrayList<>();
    private final BooleanSetting showVenomed = new BooleanSetting("Show Venomed", this, true);

    private int venomedListX = 10;  // Initial X position
    private int venomedListY = 10;  // Initial Y position
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    public VenomList() {
        super("Venom List", "Displays a list of players affected by Venom", Category.RENDER);
        addSettings(showVenomed);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || !showVenomed.isEnabled()) {
            return;
        }

        updateVenomedList();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null || !showVenomed.isEnabled()) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);
            FontRenderer fr = mc.fontRendererObj;

            if (mc.currentScreen instanceof GuiChat) {
                if (dragging) {
                    venomedListX = Mouse.getX() / sr.getScaleFactor() - dragX;
                    venomedListY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - dragY;
                }

                if (Mouse.isButtonDown(0)) {
                    if (!dragging && isMouseOverList(Mouse.getX() / sr.getScaleFactor(), (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor(), fr)) {
                        dragging = true;
                        dragX = Mouse.getX() / sr.getScaleFactor() - venomedListX;
                        dragY = (mc.displayHeight - Mouse.getY()) / sr.getScaleFactor() - venomedListY;
                    }
                } else {
                    dragging = false;
                }
            }

            float pos = venomedListY;
            float posX = venomedListX;
            int indexTing = (int) pos;
            fr.drawStringWithShadow("Venomed List:", posX, indexTing, 0x00FF00);
            for (String playerName : venomedPlayers) {
                String venomedText = " Is Currently Venomed.";

                // Draw the player's name in white
                fr.drawStringWithShadow(playerName, posX, indexTing + fr.FONT_HEIGHT, 0xFFFFFF);

                // Draw the "Is Currently Venomed." text in green
                fr.drawStringWithShadow(venomedText, posX + fr.getStringWidth(playerName), indexTing + fr.FONT_HEIGHT, 0x00FF00);

                indexTing += fr.FONT_HEIGHT;
            }
        }
    }

    private void updateVenomedList() {
        venomedPlayers.clear();
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer) {
                PotionEffect effect = player.getActivePotionEffect(Potion.poison);
                if (effect != null) {
                    String playerName = player.getName();
                    venomedPlayers.add(playerName);
                }
            }
        }
    }

    private boolean isMouseOverList(int mouseX, int mouseY, FontRenderer fr) {
        int textHeight = fr.FONT_HEIGHT * (venomedPlayers.size() + 1);
        int textWidth = 100; // Adjust as necessary
        return mouseX >= venomedListX && mouseX <= venomedListX + textWidth
                && mouseY >= venomedListY && mouseY <= venomedListY + textHeight;
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
