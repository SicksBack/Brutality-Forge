package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RotationUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();

    // Set the player's yaw (view direction)
    public static void setRenderYaw(float yaw) {
        mc.thePlayer.rotationYaw = yaw;
    }

    // Get the rotations needed to face a specific BlockPos
    public static float[] getRotations(BlockPos blockPos) {
        double x = blockPos.getX() + 0.5 - mc.thePlayer.posX;
        double y = blockPos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double z = blockPos.getZ() + 0.5 - mc.thePlayer.posZ;
        double distance = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, distance) * (180 / Math.PI)));
        return new float[]{yaw, pitch};
    }

    // Interpolate between two values
    public static float interpolateValue(float partialTicks, float prev, float current) {
        return prev + (current - prev) * partialTicks;
    }

    // Get the rotations needed to face an Entity
    public static float[] getRotations(Entity entity) {
        double x = entity.posX - mc.thePlayer.posX;
        double y = entity.posY + entity.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double z = entity.posZ - mc.thePlayer.posZ;
        double distance = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * (180 / Math.PI)) - 90.0F;
        float pitch = (float)(-(Math.atan2(y, distance) * (180 / Math.PI)));
        return new float[]{yaw, pitch};
    }

    // Raycast to detect blocks or entities
    public static MovingObjectPosition rayCast(double distance, float yaw, float pitch) {
        Vec3 start = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 direction = getDirectionVector(yaw, pitch);
        Vec3 end = start.addVector(direction.xCoord * distance, direction.yCoord * distance, direction.zCoord * distance);
        return mc.theWorld.rayTraceBlocks(start, end, false, false, false);
    }

    // Helper function to get the direction vector from yaw and pitch
    private static Vec3 getDirectionVector(float yaw, float pitch) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }
}
