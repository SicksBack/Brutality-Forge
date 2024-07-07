package org.brutality.module.impl.combat;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.notifications.NotificationManager;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;

import java.util.List;
import java.util.stream.Collectors;

public class KillauraModule extends Module {

    private BooleanSetting targetPlayers;
    private BooleanSetting targetMobs;
    private BooleanSetting showTarget;
    private NumberSetting attackRange;
    private NumberSetting swingRange;
    private EntityLivingBase currentTarget;

    public KillauraModule() {
        super("KillAura", "Folding The Opps", Category.COMBAT);
        targetPlayers = new BooleanSetting("TargetPlayers", this, true);
        targetMobs = new BooleanSetting("TargetMobs", this, false);
        showTarget = new BooleanSetting("Show Target", this, false);
        attackRange = new NumberSetting("Attack Range", this, 3.0, 3.0, 6.0, 1);
        swingRange = new NumberSetting("Swing Range", this, 6.0, 3.0, 6.0, 1);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        NotificationManager.sendNotification("Enabled " + this.getName());
    }

    @Override
    public void onDisable() {
        super.onDisable();
        NotificationManager.sendNotification("Disabled " + this.getName());
        currentTarget = null;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        constrainRanges();

        List<EntityLivingBase> targets = mc.theWorld.loadedEntityList.stream()
                .filter(this::isValidTarget)
                .map(entity -> (EntityLivingBase) entity)
                .collect(Collectors.toList());

        currentTarget = null;
        for (EntityLivingBase target : targets) {
            double distanceToTarget = mc.thePlayer.getDistanceToEntity(target);
            if (distanceToTarget <= attackRange.getValue() && target.isEntityAlive()) {
                attackEntity(target);
                currentTarget = target;
            }
            if (distanceToTarget <= swingRange.getValue() && target.isEntityAlive()) {
                swingAtEntity();
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (showTarget.isEnabled() && currentTarget != null) {
            renderTargetOverlay(currentTarget, event.partialTicks);
        }
    }

    private void renderTargetOverlay(EntityLivingBase entity, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0F, 0.0F, 0.0F, 0.5F); // Red color with some transparency

        AxisAlignedBB bb = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1);
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(
                bb.minX - entity.posX + entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks,
                bb.minY - entity.posY + entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks,
                bb.minZ - entity.posZ + entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks,
                bb.maxX - entity.posX + entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks,
                bb.maxY - entity.posY + entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks,
                bb.maxZ - entity.posZ + entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks);

        mc.renderGlobal.drawOutlinedBoundingBox(axisalignedbb, 255, 0, 0, 255);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private boolean isValidTarget(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return targetPlayers.isEnabled();
        } else if (entity instanceof EntityLivingBase) {
            return targetMobs.isEnabled() && !(entity instanceof EntityPlayer);
        }
        return false;
    }

    private void attackEntity(EntityLivingBase entity) {
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, entity);
    }

    private void swingAtEntity() {
        mc.thePlayer.swingItem();
    }

    private void constrainRanges() {
        if (attackRange.getValue() > swingRange.getValue()) {
            attackRange.setValue(swingRange.getValue());
        }
        if (swingRange.getValue() < attackRange.getValue()) {
            swingRange.setValue(attackRange.getValue());
        }
    }
}
