package org.brutality.module.impl.harrys;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.opengl.GL11;

public class Gamble extends Module {

    private Minecraft mc = Minecraft.getMinecraft();
    private boolean trackingGiantTicket = false;
    private boolean gambleEventActive = false;
    private BlockPos ticketPos;
    private final BooleanSetting giantTicketSetting = new BooleanSetting("Enable Giant Ticket", this, true);

    public Gamble() {
        super("Gamble", "Tracks Gamble events and optionally Giant Tickets", Category.HARRYS);
        this.addSettings(giantTicketSetting);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.contains("GIANT TICKET! Spawned at") && giantTicketSetting.isEnabled()) {
            String[] parts = message.split(" ");
            int x = Integer.parseInt(parts[4]);
            int z = Integer.parseInt(parts[6].replace("!", ""));
            ticketPos = new BlockPos(x, 0, z);
            trackingGiantTicket = true;
            addWaypoint("Giant Ticket", ticketPos);
        }

        if (message.contains("GIANT TICKET! Claimed By") && giantTicketSetting.isEnabled()) {
            trackingGiantTicket = false;
            removeWaypoint("Giant Ticket");
        }

        if (message.contains("MAJOR EVENT! GAMBLE starting now")) {
            gambleEventActive = true;
        }

        if (message.contains("Gamble event ended")) {
            gambleEventActive = false;
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (trackingGiantTicket && ticketPos != null) {
            // Render waypoint or any additional tracking logic
        }

        if (gambleEventActive) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityItem) {
                    EntityItem entityItem = (EntityItem) entity;
                    ItemStack itemStack = entityItem.getEntityItem();

                    if (itemStack != null && itemStack.getItem() == Item.getItemById(421)) { // 421 is the ID for Name Tag
                        renderNameTag(entityItem, itemStack, event.partialTicks);
                    }
                }
            }
        }
    }

    private void handleGambleEvent() {
        // Handle the gamble event logic here
        // Example: Send notification, play sound, etc.
        System.out.println("Gamble event started!");
    }

    private void addWaypoint(String name, BlockPos pos) {
        // Add your waypoint logic here
        // Example: WaypointManager.addWaypoint(name, pos);
    }

    private void removeWaypoint(String name) {
        // Remove your waypoint logic here
        // Example: WaypointManager.removeWaypoint(name);
    }

    private void renderNameTag(EntityItem entityItem, ItemStack itemStack, float partialTicks) {
        double x = entityItem.prevPosX + (entityItem.posX - entityItem.prevPosX) * partialTicks;
        double y = entityItem.prevPosY + (entityItem.posY - entityItem.prevPosY) * partialTicks;
        double z = entityItem.prevPosZ + (entityItem.posZ - entityItem.prevPosZ) * partialTicks;

        GL11.glPushMatrix();
        GL11.glTranslated(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY + 0.5, z - mc.getRenderManager().viewerPosZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-0.025F, -0.025F, 0.025F);

        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();

        mc.fontRendererObj.drawString("Name Tag", -mc.fontRendererObj.getStringWidth("Name Tag") / 2, 0, 0xFFFFFF);

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

        GL11.glPopMatrix();
    }


    public void onDisable() {
        trackingGiantTicket = false;
        gambleEventActive = false;
        removeWaypoint("Giant Ticket");
    }
}
