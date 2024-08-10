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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

public class DarkNotifications extends Module {

    private final List<String> darkPantsNames = Arrays.asList("Venom", "Misery", "Spite", "Needless Suffering", "Grim Reaper", "Nostalgia", "Hedge Fund", "Heartripper", "Sanguisuge", "Lycanthropy", "Mind Assault", "Golden Handcuffs");
    private final NumberSetting detectionRange = new NumberSetting("Detection Range", this, 10, 1, 100, 1);

    private long lastNotificationTime = 0;
    private long notificationEndTime = 0;
    private String notificationText = "";

    public DarkNotifications() {
        super("Dark Notifications", "Notify when players with dark pants are near", Category.PIT);
        addSettings(detectionRange);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player != mc.thePlayer) {
                if (isWearingDarkPants(player) && player.getDistanceToEntity(mc.thePlayer) <= detectionRange.getValue()) {
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

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (event.type == RenderGameOverlayEvent.ElementType.CHAT) {
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);
            FontRenderer fr = mc.fontRendererObj;

            if (!notificationText.isEmpty() && System.currentTimeMillis() < notificationEndTime) {
                int width = fr.getStringWidth(notificationText) * 3;
                int xPos = (sr.getScaledWidth() - width) / 2;
                int yPos = sr.getScaledHeight() / 2 - fr.FONT_HEIGHT * 3;

                GL11.glPushMatrix();
                GL11.glScalef(3.0F, 3.0F, 3.0F);
                fr.drawStringWithShadow(notificationText, xPos / 3, yPos / 3, 0xAA00FF);
                GL11.glPopMatrix();
            }
        }
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
