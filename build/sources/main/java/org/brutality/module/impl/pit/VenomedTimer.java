package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.RenderUtils;

public class VenomedTimer extends Module {

    public VenomedTimer() {
        super("VenomedTimer", "stop venom hopping", Category.PIT);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player.isPotionActive(Potion.poison)) {
            PotionEffect effect = player.getActivePotionEffect(Potion.poison);
            int duration = effect.getDuration() / 20;
            String text = "\u00A75Venomed: \u00A7d" + duration + "s";
            int venomedTextX = mc.displayWidth / 4 - mc.fontRendererObj.getStringWidth(text) / 2;
            int venomedTextY = mc.displayHeight / 4 + 24;
            mc.fontRendererObj.drawStringWithShadow(text, venomedTextX, venomedTextY, -1);
        }
    }
}
