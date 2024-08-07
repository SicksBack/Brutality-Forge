package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.input.Mouse;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DarkNotifications extends Module {

    private final List<String> venomedPlayers = new ArrayList<>();
    private final List<String> darkPantsNames = Arrays.asList("Venom", "Misery", "Spite", "Needless Suffering", "Grim Reaper", "Nostalgia", "Hedge Fund", "Heartripper", "Sanguisuge", "Lycanthropy", "Mind Assault", "Golden Handcuffs");
    private final BooleanSetting notifications = new BooleanSetting("Notifications", this, true);

    private int venomedListX = 10;  // Initial X position
    private int venomedListY = 10;  // Initial Y position
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private long lastNotificationTime = 0;
    private long notificationEndTime = 0;
    private String notificationText = "";

    public DarkNotifications() {
        super("Dark Notifications", "Venomed", Category.PIT);
        addSettings(notifications);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }

        venomedPlayers.clear();
        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer) {
                PotionEffect effect = player.getActivePotionEffect(Potion.poison);
                if (effect != null) {
                    String playerName = player.getGameProfile().getName(); // Real name without custom colors
                    String entry = playerName + " is Venomed.";
                    venomedPlayers.add(entry);
                }

                if (notifications.isEnabled()) {
                    if (isWearingDarkPants(player) && player.getDistanceToEntity(mc.thePlayer) <= 10) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastNotificationTime >= 10000) { // 10 second cooldown
                            notificationText = getDarkPantsName(player) + " NEAR YOU!";
                            notificationEndTime = currentTime + 3000; // 3 seconds duration
                            lastNotificationTime = currentTime;
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.levelup"), 1.0F));
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
            for (String entityData : venomedPlayers) {
                String[] parts = entityData.split(" is Venomed.");
                String playerName = parts[0];
                String restOfText = " is Venomed.";

                fr.drawStringWithShadow(playerName, posX, indexTing + fr.FONT_HEIGHT, 0xFFFFFF);
                fr.drawStringWithShadow(restOfText, posX + fr.getStringWidth(playerName), indexTing + fr.FONT_HEIGHT, 0xAA00FF);

                indexTing += fr.FONT_HEIGHT;
            }

            if (!notificationText.isEmpty() && System.currentTimeMillis() < notificationEndTime) {
                int width = fr.getStringWidth(notificationText);
                fr.drawStringWithShadow(notificationText, (sr.getScaledWidth() - width) / 2, sr.getScaledHeight() / 2 - fr.FONT_HEIGHT, 0xAA00FF);
            }
        }
    }

    private boolean isMouseOverList(int mouseX, int mouseY, FontRenderer fr) {
        int textHeight = fr.FONT_HEIGHT * (venomedPlayers.size() + 1);
        int textWidth = 100; // Adjust as necessary
        return mouseX >= venomedListX && mouseX <= venomedListX + textWidth
                && mouseY >= venomedListY && mouseY <= venomedListY + textHeight;
    }

    private boolean isWearingDarkPants(EntityPlayer player) {
        for (ItemStack armor : player.inventory.armorInventory) {
            if (armor != null && armor.hasTagCompound()) {
                NBTTagCompound tagCompound = armor.getTagCompound();
                if (tagCompound != null) {
                    for (String name : darkPantsNames) {
                        if (tagCompound.toString().contains(name)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private String getDarkPantsName(EntityPlayer player) {
        for (ItemStack armor : player.inventory.armorInventory) {
            if (armor != null && armor.hasTagCompound()) {
                NBTTagCompound tagCompound = armor.getTagCompound();
                if (tagCompound != null) {
                    for (String name : darkPantsNames) {
                        if (tagCompound.toString().contains(name)) {
                            return name;
                        }
                    }
                }
            }
        }
        return "Unknown";
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
