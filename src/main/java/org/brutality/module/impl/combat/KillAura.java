package org.brutality.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.events.RenderEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.CustomTimer;
import org.brutality.utils.FriendManager;
import org.brutality.utils.KillAuraBoxUtils;
import org.brutality.module.impl.player.Friends;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {
    private final CustomTimer timer = new CustomTimer();

    private final NumberSetting angle = new NumberSetting("Angle", this, 360, 1, 360, 0);
    private final NumberSetting cps = new NumberSetting("CPS", this, 10, 1, 20, 0);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 100, 0, 1000, 0);
    private final NumberSetting reachDistance = new NumberSetting("Reach", this, 4, 1, 6, 1);
    private final SimpleModeSetting mode = new SimpleModeSetting("Mode", this, "Single", new String[]{"Single", "Switch"});
    private final SimpleModeSetting filter = new SimpleModeSetting("Filter", this, "Distance", new String[]{"Health", "Distance"});
    private final BooleanSetting targetPlayers = new BooleanSetting("Target Players", this, true);
    private final BooleanSetting targetMobs = new BooleanSetting("Target Mobs", this, false);
    private final ColorSetting targetBoxColor = new ColorSetting("Target Box Color", this, Color.RED);
    private final BooleanSetting showTarget = new BooleanSetting("Show Target", this, true);

    private long lastAttackTime = 0;
    private long lastSwitchTime = 0;
    private int currentTargetIndex = 0;
    private Entity target;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities.", Category.COMBAT);
        addSettings(targetPlayers, targetMobs, cps, targetBoxColor, angle, switchDelay, reachDistance, mode, filter, showTarget);
        setKey(Keyboard.KEY_R);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        timer.update();

        List<Entity> targets = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity != mc.thePlayer)
                .filter(entity -> (targetPlayers.isEnabled() && entity instanceof EntityPlayer) ||
                        (targetMobs.isEnabled() && entity instanceof IMob))
                .filter(entity -> !entity.isDead)
                .filter(this::isWithinReach)
                .filter(this::isWithinAttackAngle)
                .filter(entity -> !Friends.friendsEnabled || !isFriend((EntityPlayer) entity)) // Use Friends module state
                .sorted(getComparator())
                .collect(Collectors.toList());

        if (targets.isEmpty()) return;

        if (mode.getValue().equalsIgnoreCase("Switch")) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSwitchTime >= switchDelay.getValue()) {
                currentTargetIndex = (currentTargetIndex + 1) % targets.size();
                lastSwitchTime = currentTime;
            }
        } else {
            currentTargetIndex = 0; // Single mode: always target the first entity
        }

        target = targets.get(currentTargetIndex);

        if (shouldAttack()) {
            attackEntity(target);
        }
    }

    @SubscribeEvent
    public void onRender(RenderEvent event) {
        if (target != null && showTarget.isEnabled()) {
            KillAuraBoxUtils.drawTargetBox(target, targetBoxColor.getColor(), event.getPartialTicks());
        }
    }

    public Entity getTarget() {
        return target;
    }

    private Comparator<Entity> getComparator() {
        switch (filter.getValue()) {
            case "Health":
                return Comparator.comparingDouble(entity -> ((EntityPlayer) entity).getHealth());
            case "Distance":
            default:
                return Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity));
        }
    }

    private boolean shouldAttack() {
        long currentTime = System.currentTimeMillis();
        long delay = (long) (1000 / cps.getValue());
        if (currentTime - lastAttackTime >= delay) {
            lastAttackTime = currentTime;
            return true;
        }
        return false;
    }

    private void attackEntity(Entity target) {
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, target);
    }

    private boolean isWithinAttackAngle(Entity entity) {
        double angleDifference = getAngleDifference(mc.thePlayer.rotationYaw, getYawToEntity(entity));
        return angleDifference <= angle.getValue() / 2;
    }

    private boolean isWithinReach(Entity entity) {
        double distance = mc.thePlayer.getDistanceToEntity(entity);
        return distance <= reachDistance.getValue();
    }

    private boolean isFriend(EntityPlayer player) {
        return Friends.friendsEnabled && FriendManager.isFriend(player);
    }

    private double getAngleDifference(double angle1, double angle2) {
        double diff = angle1 - angle2;
        while (diff >= 180.0) diff -= 360.0;
        while (diff < -180.0) diff += 360.0;
        return Math.abs(diff);
    }

    private float getYawToEntity(Entity entity) {
        double deltaX = entity.posX - mc.thePlayer.posX;
        double deltaZ = entity.posZ - mc.thePlayer.posZ;
        return (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90.0F;
    }
}
