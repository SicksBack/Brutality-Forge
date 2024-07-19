package org.brutality.module.impl.weasel;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.Timer;

public class AutoGrinder extends Module {

    private boolean aimingAtEnemy = false;
    private Timer swapTimer = new Timer();
    private Timer moveTimer = new Timer();

    private final BooleanSetting enableModule = new BooleanSetting("Enable", this, true);

    public AutoGrinder() {
        super("AutoGrinder", "Automatically grinds mobs or players", Category.WEASEL);
        this.addSettings(enableModule);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.contains("Sending you to world GENESIS lobby")) {
            aimingAtEnemy = true;
        }

        if (message.matches("DEATH! by \\[(\\d+)\\]")) {
            String[] parts = message.split(" ");
            int number = Integer.parseInt(parts[3].replaceAll("\\[|\\]", ""));
            if (number > 1) {
                aimingAtEnemy = false;
                mc.thePlayer.sendChatMessage("/play pit");
                aimingAtEnemy = true;
            }
        }
    }

    @SubscribeEvent
    public void onTickEvent(TickEvent.PlayerTickEvent event) {
        if (!enableModule.isEnabled()) {
            return;
        }

        // Move player to (0, 80, 0) if at spawn (0, 86, 0)
        if (mc.thePlayer.getPosition().equals(new BlockPos(0, 86, 0))) {
            moveToTarget(new BlockPos(0, 80, 0));
        }

        if (swapTimer.hasTimeElapsed(75, true)) {
            swapWeapon();
        }

        if (aimingAtEnemy) {
            Entity target = findTarget();
            if (target != null) {
                attackTarget(target);
            }
        }
    }

    private void moveToTarget(BlockPos targetPos) {
        if (moveTimer.hasTimeElapsed(50, true)) {
            double dx = targetPos.getX() - mc.thePlayer.posX;
            double dy = targetPos.getY() - mc.thePlayer.posY;
            double dz = targetPos.getZ() - mc.thePlayer.posZ;

            double distance = Math.sqrt(dx * dx + dz * dz);
            float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90;
            float pitch = (float) -(Math.atan2(dy, distance) * (180 / Math.PI));

            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;

            mc.thePlayer.setPosition(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5);
        }
    }

    private void swapWeapon() {
        ItemStack item0 = mc.thePlayer.inventory.mainInventory[0];
        ItemStack item2 = mc.thePlayer.inventory.mainInventory[2];

        if ((item0 != null && item0.getItem().equals(Item.getItemById(276))) &&  // Diamond Sword ID
                (item2 != null && item2.getDisplayName().equals("Combat Spade"))) {
            mc.thePlayer.inventory.currentItem = mc.thePlayer.inventory.currentItem == 0 ? 2 : 0;
        } else if (item0 != null && item0.getItem().equals(Item.getItemById(276))) {  // Diamond Sword ID
            mc.thePlayer.inventory.currentItem = 0;
        }
    }

    private Entity findTarget() {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity.getPosition().getY() < 50) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.getDisplayName().getFormattedText().startsWith("§7")) {
                    return player;
                }
            }
        }
        return null;
    }

    private void attackTarget(Entity target) {
        double dx = mc.thePlayer.posX - target.posX;
        double dy = mc.thePlayer.posY - target.posY;
        double dz = mc.thePlayer.posZ - target.posZ;

        double distance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90;
        float pitch = (float) -(Math.atan2(dy, distance) * (180 / Math.PI));

        mc.thePlayer.rotationYaw = yaw;
        mc.thePlayer.rotationPitch = pitch;

        if (mc.playerController.getBlockReachDistance() >= mc.thePlayer.getDistanceToEntity(target)) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, target);
        }
    }

    public void onDisable() {
        aimingAtEnemy = false;
    }
}
