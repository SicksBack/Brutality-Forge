package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gamble extends Module {

    private final BooleanSetting giantTicketSetting = new BooleanSetting("Enable Giant Ticket", this, true);
    private final List<Vec3> waypoints = new ArrayList<>();

    private boolean tracking = false;

    public Gamble() {
        super("Gamble", "Track and render waypoints from chat events", Category.MISC);
        this.addSettings(giantTicketSetting);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.contains("MAJOR EVENT! GAMBLE starting now")) {
            tracking = true;
        } else if (message.contains("PIT EVENT ENDED: GAMBLE!")) {
            tracking = false;
            waypoints.clear();
        } else if (tracking) {
            // Extract coordinates from the chat message using regex
            Pattern pattern = Pattern.compile("-?\\d+\\s-?\\d+\\s-?\\d+");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String[] coords = matcher.group().split(" ");
                try {
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    int z = Integer.parseInt(coords[2]);
                    waypoints.add(new Vec3(x, y, z));
                } catch (NumberFormatException ignored) {
                }
            }
        }

        if (giantTicketSetting.isEnabled() && message.contains("GIANT TICKET! Claimed By")) {
            tracking = false;
            waypoints.clear();
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (tracking) {
            renderWaypoints(event.partialTicks);
        }
    }

    private void renderWaypoints(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        for (Vec3 waypoint : waypoints) {
            double x = waypoint.xCoord - (mc.getRenderManager().viewerPosX);
            double y = waypoint.yCoord - (mc.getRenderManager().viewerPosY);
            double z = waypoint.zCoord - (mc.getRenderManager().viewerPosZ);

            // Render the waypoint
            renderNameTag("Waypoint", x, y, z, partialTicks);
        }
    }

    private void renderNameTag(String text, double x, double y, double z, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        float scale = 0.016666668F * 1.6F;
        int width = mc.fontRendererObj.getStringWidth(text) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5F, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);

        mc.fontRendererObj.drawString(text, -width, 0, 0xFFFFFF);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.scale(1.0F / scale, 1.0F / scale, 1.0F / scale);
        GlStateManager.popMatrix();
    }

    public void onDisable() {
        tracking = false;
        waypoints.clear();
    }
}
