package org.brutality.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.notifications.NotificationManager;
import org.brutality.settings.impl.BooleanSetting;

import java.util.List;
import java.util.stream.Collectors;

public class KillauraModule extends Module {

    private BooleanSetting targetPlayers;
    private BooleanSetting targetMobs;

    public KillauraModule() {
        super("KillAura", "Folding The Opps", Category.COMBAT);
        targetPlayers = new BooleanSetting("TargetPlayers", this, true);
        targetMobs = new BooleanSetting("TargetMobs", this, false);
    }

    public BooleanSetting getTargetPlayersSetting() {
        return targetPlayers;
    }

    public BooleanSetting getTargetMobsSetting() {
        return targetMobs;
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
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        List<EntityLivingBase> targets = mc.theWorld.loadedEntityList.stream()
                .filter(this::isValidTarget)
                .map(entity -> (EntityLivingBase) entity)
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= 4.0f && entity.isEntityAlive())
                .collect(Collectors.toList());

        for (EntityLivingBase target : targets) {
            attackEntity(target);
        }
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
        try {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
