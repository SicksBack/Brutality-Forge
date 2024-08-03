package org.brutality.module.impl.pit;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;


public class Grinder extends Module {
    private final NumberSetting targetsMinY;
    private final NumberSetting distance;
    private int ticks = 0;

    public Grinder() {
        super("Auto Grinder", "Automatically grind kills", Category.PIT);
        targetsMinY = new NumberSetting("Targets Min PosY", this, 76.0, 0.0, 256.0, 1);
        distance = new NumberSetting("Distance", this, 3.3, 3.0, 6.0, 1);
        addSettings(distance, targetsMinY);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent e) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            ticks++;
            EntityPlayer closestPlayer = getClosestPlayer(mc.thePlayer);
            if (closestPlayer != null) {
                double[] values = getYawPitch(closestPlayer);
                float targetYaw = (float) values[0];
                float targetPitch = (float) values[1];

                mc.thePlayer.rotationYaw = smoothAim(mc.thePlayer.rotationYaw, targetYaw);
                mc.thePlayer.rotationPitch = smoothAim(mc.thePlayer.rotationPitch, targetPitch);

                setControlStates(closestPlayer);
                targetEntity(closestPlayer);
            } else {
                clearControlStates();
            }
        }
    }

    private EntityPlayer getClosestPlayer(EntityPlayer entity) {
        double closestDistance = Double.MAX_VALUE;
        EntityPlayer closestPlayer = null;

        for (EntityPlayer player : entity.worldObj.playerEntities) {
            if (!player.equals(entity)) {
                double distance = player.getDistanceSqToEntity(entity);
                if (distance < closestDistance && player.posY <= targetsMinY.getValue()) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }
        }

        return closestPlayer;
    }

    private void targetEntity(EntityPlayer target) {
        float playerYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

        double[] targetYawPitch = getYawPitch(target);
        float targetYaw = (float) targetYawPitch[0];

        float yawTolerance = 17.50f;
        float yawDifference = MathHelper.wrapAngleTo180_float(playerYaw - targetYaw);

        boolean canAttack = Math.abs(yawDifference) < yawTolerance;
        if (canAttack && ticks % 3 == 0 && mc.thePlayer.getDistanceToEntity(target) <= distance.getValue()) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        }
    }

    private double getDistanceXZ(EntityPlayer player1, EntityPlayer player2) {
        double dx = player1.posX - player2.posX;
        double dz = player1.posZ - player2.posZ;
        return MathHelper.sqrt_double(dx * dx + dz * dz);
    }

    private void setControlStates(EntityPlayer target) {
        double distance = getDistanceXZ(mc.thePlayer, target);

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), distance > 0.5);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), mc.gameSettings.keyBindForward.isKeyDown());
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), !enemyLooking(target) || distance > 5);
    }

    private boolean enemyLooking(EntityPlayer enemy) {
        Vec3 playerVec = new Vec3(mc.thePlayer.posX - enemy.posX, mc.thePlayer.posY - (enemy.posY + enemy.getEyeHeight()), mc.thePlayer.posZ - enemy.posZ).normalize();

        float pitch = enemy.rotationPitch;
        float yaw = enemy.rotationYaw;
        Vec3 enemyVec = getRotationVec(pitch, yaw).normalize();
        double dot = playerVec.dotProduct(enemyVec);

        return Math.acos(dot) * (180 / Math.PI) < 45;
    }

    private Vec3 getRotationVec(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    private void clearControlStates() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
    }

    private double[] getYawPitch(EntityPlayer target) {
        double dx = target.posX - mc.thePlayer.posX;
        double dy = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double dz = target.posZ - mc.thePlayer.posZ;

        double distanceXZ = MathHelper.sqrt_double(dx * dx + dz * dz);
        double yawRadians = Math.atan2(dz, dx);
        double pitchRadians = Math.atan2(dy, distanceXZ);

        double yawDegrees = Math.toDegrees(yawRadians) - 90.0;
        double pitchDegrees = -Math.toDegrees(pitchRadians);

        yawDegrees = normalizeYaw(yawDegrees);

        return new double[]{yawDegrees, pitchDegrees};
    }

    private double normalizeYaw(double yaw) {
        yaw = yaw % 360.0;
        if (yaw > 180.0) {
            yaw -= 360.0;
        }
        if (yaw < -180.0) {
            yaw += 360.0;
        }
        return yaw;
    }

    private float smoothAim(float current, float target) {
        float delta = MathHelper.wrapAngleTo180_float(target - current);
        return current + delta / (float) Math.max(1.0, (10.0 / (float) 4));
    }
}
