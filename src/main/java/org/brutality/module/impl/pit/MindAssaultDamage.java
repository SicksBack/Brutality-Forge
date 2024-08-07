package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
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
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private final BooleanSetting showNegative = new BooleanSetting("Show -60%", this, true);

    public MindAssaultDamage() {
        super("MindAssaultDamage", "Shows Mind Assault Damage", Category.PIT);
        addSettings(showNegative, xPos, yPos);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();

        if (mc.currentScreen != null && mc.currentScreen instanceof GuiScreen) {
            if (dragging) {
                this.xPos.setValue(Mouse.getX() / 2 - dragX);
                this.yPos.setValue((mc.displayHeight - Mouse.getY()) / 2 - dragY);
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, mc)) {
                    dragging = true;
                    dragX = Mouse.getX() / 2 - (int) posX;
                    dragY = (mc.displayHeight - Mouse.getY()) / 2 - (int) posY;
                }
            } else {
                dragging = false;
            }
        }

        if (isWearingMindAssault(mc.thePlayer)) {
            double damage = calculateDamage(mc.thePlayer);

            String text = "Minds: ";
            if (showNegative.isEnabled()) {
                text += "§d-60% ";
            }
            text += "§c+" + damage + " §c❤";

            mc.fontRendererObj.drawStringWithShadow(text, posX, posY, 0xFFFFFF);
        }
    }

    private boolean isWearingMindAssault(EntityPlayerSP player) {
        ItemStack leggings = player.inventory.armorItemInSlot(3); // 2 corresponds to leggings slot
        if (leggings != null && leggings.hasTagCompound()) {
            NBTTagCompound tagCompound = leggings.getTagCompound();
            if (tagCompound != null) {
                String lore = tagCompound.toString();
                return lore.contains("Mind Assault") || tagCompound.toString().contains("mind_assault");
            }
        }
        return false;
    }

    private double calculateDamage(EntityPlayerSP player) {
        int nearbyPlayers = 0;
        for (Object entity : mc.theWorld.playerEntities) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) entity;
                if (p != player && p.getDistanceToEntity(player) <= 11 && isWearingLeatherLeggings(p)) {
                    nearbyPlayers++;
                }
            }
        }
        return Math.min(nearbyPlayers * 0.75, 8.0); // max damage is 8.0
    }

    private boolean isWearingLeatherLeggings(EntityPlayer player) {
        ItemStack leggings = player.inventory.armorItemInSlot(2); // 2 corresponds to leggings slot
        return leggings != null && leggings.getUnlocalizedName().contains("leather_leggings");
    }

    private boolean isMouseOverText(int mouseX, int mouseY, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth("Minds: " + (showNegative.isEnabled() ? "-60% +" : "+") + "0.0 ❤");
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
