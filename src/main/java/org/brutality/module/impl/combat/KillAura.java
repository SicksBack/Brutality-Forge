package org.brutality.module.impl.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.brutality.events.EventTarget;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.Mode;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.RotationUtils;
import org.brutality.utils.Timer;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import static java.lang.Math.sqrt;

public class KillAura extends Module {
    private EntityLivingBase target;
    private final NumberSetting aps = new NumberSetting("APS", this, 12, 1, 20, 1);
    private final ModeSetting autoBlockMode = new ModeSetting("AutoBlock", this, new Mode<?>[] {
            new Mode<Module>("None", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Vanilla", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Interact", this) {
                @Override
                public void setup() {}
            }
    });
    private final NumberSetting fov = new NumberSetting("FOV", this, 360, 30, 360, 1);
    private final NumberSetting swingRange = new NumberSetting("Swing Range", this, 4, 1, 6, 1);
    private final NumberSetting attackRange = new NumberSetting("Attack Range", this, 4, 1, 6, 1);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 500, 0, 1000, 1);
    private final ModeSetting rotationMode = new ModeSetting("Rotation Mode", this, new Mode<?>[] {
            new Mode<Module>("VapeV4", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Silent", this) {
                @Override
                public void setup() {}
            }
    });
    private final ModeSetting sortingMode = new ModeSetting("Sorting", this, new Mode<?>[] {
            new Mode<Module>("Distance", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Health", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Beast", this) {
                @Override
                public void setup() {}
            }
    });
    private final ModeSetting mode = new ModeSetting("Mode", this, new Mode<?>[] {
            new Mode<Module>("Single", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Switch", this) {
                @Override
                public void setup() {}
            },
            new Mode<Module>("Multiple", this) {
                @Override
                public void setup() {}
            }
    });
    private final BooleanSetting targetInvisibles = new BooleanSetting("Target Invisibles", this, false);
    private final BooleanSetting targetMobs = new BooleanSetting("Target Mobs", this, true);
    private final BooleanSetting targetPlayers = new BooleanSetting("Target Players", this, true);

    private final Timer attackTimer = new Timer();
    private final Timer switchTimer = new Timer();
    private final Random random = new Random();
    private boolean blocking = false;
    private int currentTargetIndex = 0;

    public KillAura() {
        super("KillAura", "KillAura", Category.COMBAT);
        addSettings(aps, autoBlockMode, fov, swingRange, attackRange, switchDelay, rotationMode, sortingMode, mode, targetInvisibles, targetMobs, targetPlayers);
    }

    @Override
    public void onEnable() {
        attackTimer.reset();
        switchTimer.reset();
        currentTargetIndex = 0;
    }

    @Override
    public void onDisable() {
        target = null;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }

        // Ensure constraints between swingRange and attackRange
        if (swingRange.getValue() < attackRange.getValue()) {
            attackRange.setValue(swingRange.getValue());
        } else if (attackRange.getValue() > swingRange.getValue()) {
            swingRange.setValue(attackRange.getValue());
        }

        List<EntityLivingBase> targets = getTargetsInRange();

        if (mode.is("Single")) {
            target = getClosestTarget(targets);
        } else if (mode.is("Switch")) {
            if (switchTimer.hasTimeElapsed((long) switchDelay.getValue(), true)) {
                currentTargetIndex++;
                if (currentTargetIndex >= targets.size()) {
                    currentTargetIndex = 0;
                }
            }
            if (!targets.isEmpty()) {
                target = targets.get(currentTargetIndex % targets.size());
            }
        } else if (mode.is("Multiple")) {
            target = getClosestTarget(targets);
        }

        if (target == null) {
            handleUnblock();
            return;
        }

        if (rotationMode.is("Silent")) {
            float[] rotations = getRotations(target.posX, target.posY, target.posZ);
            RotationUtils.setRotations(rotations[0], rotations[1]);
        }

        if (attackTimer.hasTimeElapsed((long) (1000 / aps.getValue()), true)) {
            swing(target);
            attack(target);
            handleAutoBlock(target);
        }
    }

    private void swing(EntityLivingBase entity) {
        if (mc.thePlayer.getDistanceToEntity(entity) <= swingRange.getValue()) {
            mc.thePlayer.swingItem();
        }
    }

    private void attack(EntityLivingBase entity) {
        if (mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue()) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity);
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
    }

    private List<EntityLivingBase> getTargetsInRange() {
        List<EntityLivingBase> targets = new ArrayList<>();
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && canAttack((EntityLivingBase) entity)) {
                targets.add((EntityLivingBase) entity);
            }
        }

        targets.sort(getComparator());
        return targets;
    }

    private EntityLivingBase getClosestTarget(List<EntityLivingBase> targets) {
        if (targets.isEmpty()) {
            return null;
        }
        return targets.get(0);
    }

    private Comparator<EntityLivingBase> getComparator() {
        switch (sortingMode.getMode()) {
            case "Health":
                return Comparator.comparingDouble(EntityLivingBase::getHealth);
            case "Distance":
                return Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity);
            case "Beast":
                return Comparator.comparing((EntityLivingBase entity) -> hasBeastTag(entity)).reversed();
            default:
                return Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity);
        }
    }

    private boolean hasBeastTag(EntityLivingBase entity) {
        return entity.getDisplayName().getFormattedText().contains("§a§lBEAST");
    }

    private boolean canAttack(EntityLivingBase entity) {
        if (entity == mc.thePlayer || !entity.isEntityAlive() || mc.thePlayer.getDistanceToEntity(entity) > swingRange.getValue()) {
            return false;
        }

        if (!targetInvisibles.isEnabled() && entity.isInvisible()) {
            return false;
        }

        if (entity instanceof EntityPlayer && targetPlayers.isEnabled()) {
            return true;
        }

        if (entity instanceof IMob && targetMobs.isEnabled()) {
            return true;
        }

        if (!isWithinFov(entity)) {
            return false;
        }

        return true;
    }

    private boolean isWithinFov(EntityLivingBase entity) {
        double angle = getAngleBetweenEntities(mc.thePlayer, entity);
        return angle <= fov.getValue() / 2;
    }

    private double getAngleBetweenEntities(Entity from, Entity to) {
        double diffX = to.posX - from.posX;
        double diffZ = to.posZ;
        float yaw = (float) (Math.atan2(diffZ, diffX) * (180 / Math.PI)) - 90F;
        return Math.abs(yaw - from.rotationYaw) % 360;
    }

    private float[] getRotations(double x, double y, double z) {
        double diffX = x + .5D - mc.thePlayer.posX;
        double diffY = (y + .5D) / 2D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = z + .5D - mc.thePlayer.posZ;

        double dist = sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI) - 90F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180D / Math.PI);

        return new float[] { yaw, pitch };
    }

    private void blockVanilla() {
        if (!this.blocking && this.target != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            this.blocking = true;
        }
    }

    private void unblockVanilla() {
        if (this.blocking) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    private void blockInteract(EntityLivingBase target) {
        if (this.target != null) {
            mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.INTERACT));
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            this.blocking = true;
        }
    }

    private void unblockInteract() {
        if (this.blocking) {
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            this.blocking = false;
        }
    }

    private void handleAutoBlock(EntityLivingBase target) {
        switch (autoBlockMode.getMode()) {
            case "Vanilla":
                blockVanilla();
                break;
            case "Interact":
                blockInteract(target);
                break;
            case "None":
                handleUnblock();
                break;
        }
    }

    private void handleUnblock() {
        switch (autoBlockMode.getMode()) {
            case "Vanilla":
                unblockVanilla();
                break;
            case "Interact":
                unblockInteract();
                break;
        }
    }
}
