package org.brutality.utils;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockUtils {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean check(BlockPos pos, Block block) {
        World world = mc.theWorld;
        return world.getBlockState(pos).getBlock() == block;
    }

    public static Block getBlock(BlockPos pos) {
        World world = mc.theWorld;
        return world.getBlockState(pos).getBlock();
    }
}
