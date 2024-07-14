package org.brutality.module.impl.pit;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.block.BlockCake;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.brutality.events.listeners.EventRenderWorld;
import org.brutality.events.PreMotionEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Timer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CakeAura extends Module {

    private Vec3 nearestCake;
    private Vec3 cakeToEat = null;
    private List<Vec3> cakes = new ArrayList<>();
    private NumberSetting speed = new NumberSetting("Speed", this, 0.5, 0.1, 1.0, 0);
    private NumberSetting radius = new NumberSetting("Radius", this, 5.0, 1.0, 10.0, 0);
    private NumberSetting red = new NumberSetting("Red", this, 255.0, 0.0, 255.0, 0);
    private NumberSetting green = new NumberSetting("Green", this, 0.0, 0.0, 255.0, 0);
    private NumberSetting blue = new NumberSetting("Blue", this, 0.0, 0.0, 255.0, 0);
    private Timer timer = new Timer();

    public CakeAura() {
        super("CakeAura", "Automatically finds and eats cakes", Category.PIT);
        addSettings(speed, radius, red, green, blue);
    }


    public void onEnable() {
        super.onEnable();
    }


    public void onDisable() {
        super.onDisable();
        nearestCake = null;
        cakeToEat = null;
        cakes.clear();
    }


    public void onEvent(EventRenderWorld event) {
        onRenderWorld(event.getPartialTicks());
    }


    public void onEvent(PreMotionEvent event) {
        onPreMotion(event);
    }

    private void onRenderWorld(float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer localPlayer = mc.thePlayer;
        BlockPos playerPos = localPlayer.getPosition();

        cakes.clear();
        int rad = (int) radius.getValue();
        for (int x = -rad; x <= rad; x++) {
            for (int y = -rad; y <= rad; y++) {
                for (int z = -rad; z <= rad; z++) {
                    BlockPos blockPos = playerPos.add(x, y, z);
                    Block block = mc.theWorld.getBlockState(blockPos).getBlock();
                    if (block == Blocks.cake) {
                        cakes.add(new Vec3(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    }
                }
            }
        }

        nearestCake = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Vec3 cake : cakes) {
            double distance = localPlayer.getPositionVector().distanceTo(cake);
            if (distance < nearestDistance) {
                nearestCake = cake;
                nearestDistance = distance;
            }
        }

        if (cakeToEat == null || localPlayer.getPositionVector().distanceTo(cakeToEat) > 4.0
                || !(mc.theWorld.getBlockState(new BlockPos(cakeToEat.xCoord, cakeToEat.yCoord, cakeToEat.zCoord)).getBlock() instanceof BlockCake)) {
            cakeToEat = nearestCake;
        }

        if (cakeToEat != null) {
            int colour = new Color((int) red.getValue(), (int) green.getValue(), (int) blue.getValue()).getRGB();
            // Render the cake block with the specified color
            mc.renderGlobal.markBlockForUpdate(new BlockPos(cakeToEat.xCoord, cakeToEat.yCoord, cakeToEat.zCoord));
        }
    }

    private void onPreMotion(PreMotionEvent event) {
        if (cakeToEat == null) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        Vec3 playerPos = player.getPositionVector();

        double targetDeltaX = (cakeToEat.xCoord + 0.5) - playerPos.xCoord;
        double targetDeltaY = (cakeToEat.yCoord - playerPos.yCoord);
        double targetDeltaZ = (cakeToEat.zCoord + 0.5) - playerPos.zCoord;

        double targetDistanceXZ = Math.sqrt(targetDeltaX * targetDeltaX + targetDeltaZ * targetDeltaZ);
        double targetYaw = Math.toDegrees(Math.atan2(targetDeltaZ, targetDeltaX)) - 90;
        double targetPitch = -Math.toDegrees(Math.atan2(targetDeltaY, targetDistanceXZ));

        float currentYaw = player.rotationYaw;
        float currentPitch = player.rotationPitch;

        float newYaw = currentYaw + (float) speed.getValue() * (float) (targetYaw - currentYaw);
        float newPitch = currentPitch + (float) speed.getValue() * (float) (targetPitch - currentPitch);

        player.rotationYaw = newYaw % 360;
        player.rotationPitch = newPitch % 360;
        double yawDifference = Math.abs(targetYaw - newYaw) % 360;
        double pitchDifference = Math.abs(targetPitch - newPitch) % 360;
        if (yawDifference < 10 && pitchDifference < 10) {
            // Send a packet to simulate right-clicking the block
            BlockPos blockPos = new BlockPos(cakeToEat.xCoord, cakeToEat.yCoord, cakeToEat.zCoord);
            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(blockPos, 1, mc.thePlayer.inventory.getCurrentItem(), 0.5f, 0.5f, 0.5f));
        }
    }
}
