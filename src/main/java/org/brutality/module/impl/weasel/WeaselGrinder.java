package org.brutality.module.impl.weasel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class WeaselGrinder extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final SimpleModeSetting modeSetting;
    private final NumberSetting fovTolerance;
    private final SimpleModeSetting sorting;
    private final NumberSetting attackRange;
    private double spawnY = 0;
    private int ticks = 0;

    public WeaselGrinder() {
        super("Weasel Grinder", "Automatically grind kills", Category.WEASEL);
        modeSetting = new SimpleModeSetting("Mode", this, "Legit", new String[]{
                "Legit", "Blatant"
        });
        sorting = new SimpleModeSetting("Sorting", this, "Distance", new String[]{
                "Health", "Distance"
        });
        attackRange = new NumberSetting("Attack Range", this, 4.0, 3.0, 6.0, 1);
        fovTolerance = new NumberSetting("FOV", this, 30.0, 1.0, 360.0, 1);
        addSettings(modeSetting, sorting, attackRange, fovTolerance);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ticks++;
            runGrinder();
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        spawnY = event.player.posY;
    }

    private double fixRots(double start, double end) {
        return start + 0.1 * (end - start);
    }

    private void runGrinder() {
        EntityLivingBase entity = getTarget();
        if (entity == null) {
            clearControlStates();
            return;
        }
        targetEntity(entity);
    }

    private void targetEntity(EntityLivingBase target) {
        if (mc.thePlayer == null) return;
        rotations(target);
        setControlStates();
        attackTarget(target);
    }

    private double[] getYawPitch(EntityLivingBase target) {
        Vec3 vec3 = new Vec3(target.posX - mc.thePlayer.posX,
                (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()),
                target.posZ - mc.thePlayer.posZ);
        double distance = vec3.lengthVector();
        double pitch = -Math.asin(vec3.yCoord / distance) * (180 / Math.PI);
        double yaw = Math.atan2(vec3.zCoord, vec3.xCoord) * (180 / Math.PI) - 90;
        return new double[]{yaw, pitch};
    }

    private void rotations(EntityLivingBase target) {
        double[] values = getYawPitch(target);
        double yaw = values[0];
        double pitch = values[1];

        if (modeSetting.is("Legit")) {
            mc.thePlayer.rotationYaw = (float) fixRots(mc.thePlayer.rotationYaw, yaw);
            mc.thePlayer.rotationPitch = (float) fixRots(mc.thePlayer.rotationPitch, pitch);
        } else {
            mc.thePlayer.rotationYaw = (float) yaw;
            mc.thePlayer.rotationPitch = (float) pitch;
        }
    }

    private void setControlStates() {
        if (mc.thePlayer == null) return;

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), true);
    }

    private void clearControlStates() {
        if (mc.thePlayer == null) return;

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

    private EntityLivingBase getTarget() {
        if (mc.thePlayer == null || mc.theWorld == null) return null;

        List<EntityLivingBase> potentialTargets = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase && !entity.getName().equals(mc.thePlayer.getName()))
                .map(entity -> (EntityLivingBase) entity)
                .collect(Collectors.toList());

        if (sorting.is("health")) {
            return getLowestHealthTarget(potentialTargets);
        } else if (sorting.is("distance")) {
            return getClosestTarget(potentialTargets);
        } else {
            return null;
        }
    }

    private EntityLivingBase getLowestHealthTarget(List<EntityLivingBase> potentialTargets) {
        return potentialTargets.stream()
                .filter(entity -> entity.posY <= 76)
                .min(Comparator.comparing(EntityLivingBase::getHealth))
                .orElse(null);
    }

    private EntityLivingBase getClosestTarget(List<EntityLivingBase> potentialTargets) {
        return potentialTargets.stream()
                .filter(entity -> entity.posY <= 76)
                .min(Comparator.comparing(entity -> mc.thePlayer.getDistanceToEntity(entity)))
                .orElse(null);
    }

    private void attackTarget(EntityLivingBase target) {
        if (mc.thePlayer != null && mc.playerController != null) {
            double reach = attackRange.getValue();
            if (mc.thePlayer.getDistanceToEntity(target) <= reach && ticks % 2 == 0) {
                double[] values = getYawPitch(target);
                double yaw = values[0];
                double pitch = values[1];

                double yawDiff = Math.abs(mc.thePlayer.rotationYaw - yaw);
                double pitchDiff = Math.abs(mc.thePlayer.rotationPitch - pitch);

                if (modeSetting.is("Legit")) {
                    if (yawDiff <= fovTolerance.getValue() && pitchDiff <= fovTolerance.getValue()) {
                        mc.playerController.attackEntity(mc.thePlayer, target);
                        mc.thePlayer.swingItem();
                    }
                } else {
                    mc.playerController.attackEntity(mc.thePlayer, target);
                    mc.thePlayer.swingItem();
                }
            }
        }
    }
}