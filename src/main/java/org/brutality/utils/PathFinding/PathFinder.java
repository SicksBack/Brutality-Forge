package org.brutality.utils.pathfinding;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class PathFinder {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void navigateTo(BlockPos targetPos) {
        // Basic pathfinding logic
        Vec3 targetVec = new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ());
        mc.thePlayer.moveFlying((float) (targetVec.xCoord - mc.thePlayer.posX),
                (float) (targetVec.zCoord - mc.thePlayer.posZ),
                1.0F);
    }
}
