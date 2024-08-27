package org.brutality.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.CustomTimer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KillAura extends Module {
    private Minecraft mc = Minecraft.getMinecraft();
    private CustomTimer timer = new CustomTimer();

    private final NumberSetting angle = new NumberSetting("Angle", this, 360, 1, 360, 0);
    private final NumberSetting cps = new NumberSetting("CPS", this, 10, 1, 20, 0);
    private final NumberSetting reach = new NumberSetting("Reach", this, 4.0, 1.0, 6.0, 1);
    private final SimpleModeSetting filter = new SimpleModeSetting("Filter", this, "Distance", new String[]{"Health", "Distance"}); // Filter mode// Decimal precision for reach
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", this, 100, 0, 1000, 0); // Integer precision
    private final NumberSetting targetAmount = new NumberSetting("Target Amount", this, 3, 1, 10, 0); // Integer precision
    private final BooleanSetting targetPlayers = new BooleanSetting("Target Players", this, true);
    private final BooleanSetting targetMobs = new BooleanSetting("Target Mobs", this, false);
    private final BooleanSetting pitSpawnCheck = new BooleanSetting("Pit Spawn Check", this, true);
    private final ColorSetting targetBoxColor = new ColorSetting("Target Box Color", this, Color.RED);

    private long lastAttackTime = 0;
    private long lastSwitchTime = 0;
    private int currentTargetIndex = 0;

    public KillAura() {
        super("KillAura", "Automatically attacks nearby entities.", Category.COMBAT);
        addSettings(reach, targetPlayers, targetMobs, pitSpawnCheck, cps, targetAmount, targetBoxColor, angle, switchDelay, filter);
        setKey(Keyboard.KEY_H);
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Update the custom timer
        timer.update();

        if (pitSpawnCheck.isEnabled() && isInPitSpawn()) {
            return; // Don't attack if in the Hypixel Pit spawn area
        }

        List<Entity> targets = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity != mc.thePlayer)
                .filter(entity -> (targetPlayers.isEnabled() && entity instanceof EntityPlayer) ||
                        (targetMobs.isEnabled() && entity instanceof IMob))
                .filter(entity -> !entity.isDead)
                .filter(entity -> mc.thePlayer.getDistanceToEntity(entity) <= reach.getValue())
                .filter(this::isWithinAttackAngle)
                .sorted(getComparator())
                .limit((int) targetAmount.getValue())
                .collect(Collectors.toList());

        if (targets.isEmpty()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSwitchTime >= switchDelay.getValue()) {
            currentTargetIndex = (currentTargetIndex + 1) % targets.size();
            lastSwitchTime = currentTime;
        }

        if (currentTargetIndex >= targets.size()) return;

        Entity target = targets.get(currentTargetIndex);
        if (shouldAttack()) {
            attackEntity(target);
            renderTargetBox(target, targetBoxColor.getColor());
        }
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

    private boolean isInPitSpawn() {
        // Hypixel Pit spawn coordinates check (example coordinates, adjust as needed)
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        // Define the bounds of the spawn area
        double spawnMinX = -50; // Example values
        double spawnMaxX = 50;  // Example values
        double spawnMinY = 60;  // Example values
        double spawnMaxY = 100; // Example values
        double spawnMinZ = -50; // Example values
        double spawnMaxZ = 50;  // Example values

        return x >= spawnMinX && x <= spawnMaxX &&
                y >= spawnMinY && y <= spawnMaxY &&
                z >= spawnMinZ && z <= spawnMaxZ;
    }

    private boolean isWithinAttackAngle(Entity entity) {
        double angleDifference = getAngleDifference(mc.thePlayer.rotationYaw, getYawToEntity(entity));
        return angleDifference <= angle.getValue() / 2;
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

    private void renderTargetBox(Entity entity, Color color) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.getPartialTicks() - mc.getRenderManager().viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.getPartialTicks() - mc.getRenderManager().viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.getPartialTicks() - mc.getRenderManager().viewerPosZ;
        double width = entity.width;
        double height = entity.height;

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glLineWidth(2.0F);

        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.5F);

        GL11.glBegin(GL11.GL_LINES);
        // Draw box edges
        addBoxEdges(x, y, z, x + width, y + height, z + width);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void addBoxEdges(double x, double y, double z, double x1, double y1, double z1) {
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y, z);

        GL11.glVertex3d(x1, y, z);
        GL11.glVertex3d(x1, y1, z);

        GL11.glVertex3d(x1, y1, z);
        GL11.glVertex3d(x, y1, z);

        GL11.glVertex3d(x, y1, z);
        GL11.glVertex3d(x, y, z);

        GL11.glVertex3d(x, y, z1);
        GL11.glVertex3d(x1, y, z1);

        GL11.glVertex3d(x1, y, z1);
        GL11.glVertex3d(x1, y1, z1);

        GL11.glVertex3d(x1, y1, z1);
        GL11.glVertex3d(x, y1, z1);

        GL11.glVertex3d(x, y1, z1);
        GL11.glVertex3d(x, y, z1);
    }
}
