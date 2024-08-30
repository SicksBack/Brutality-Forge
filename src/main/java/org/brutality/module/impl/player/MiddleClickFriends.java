package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.FriendManager;
import org.brutality.utils.Wrapper;


public class MiddleClickFriends extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();

    private final NumberSetting blockRange = new NumberSetting("Block Range", this, 4, 1, 10, 1); // Range for middle click

    public MiddleClickFriends() {
        super("MiddleClickFriends", "Add or remove players from friends list with middle click.", Category.PLAYER);
        addSettings(blockRange);
    }

    @SubscribeEvent
    public void onMouseClick(MouseEvent event) {
        if (event.button == 2 && event.buttonstate) { // Check for middle click
            Entity target = getTargetEntity();

            if (target instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) target;
                String playerName = player.getName();

                if (FriendManager.isFriend(player)) {
                    FriendManager.removeFriend(player.getName());
                    Wrapper.addChatMessage(Wrapper.Colors.red + "[B] - Successfully Removed " + player.getName() + " From Friends.");
                } else {
                    FriendManager.addFriend(player.getName());
                    Wrapper.addChatMessage(Wrapper.Colors.green + "[B] - Successfully Added " + player.getName() + " To Friends.");
                }
            }
        }
    }

    private Entity getTargetEntity() {
        double range = blockRange.getValue();
        EntityPlayer closestPlayer = null;
        double closestDistance = range;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                double distance = mc.thePlayer.getDistanceToEntity(entity);

                if (distance <= range && isLookingAt(mc.thePlayer, entity)) {
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestPlayer = (EntityPlayer) entity;
                    }
                }
            }
        }
        return closestPlayer;
    }

    private boolean isLookingAt(EntityPlayer player, Entity target) {
        double deltaX = target.posX - player.posX;
        double deltaY = target.posY - player.posY;
        double deltaZ = target.posZ - player.posZ;

        float yaw = player.rotationYaw;
        float pitch = player.rotationPitch;

        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);

        double lookVecX = -Math.sin(yawRad) * Math.cos(pitchRad);
        double lookVecY = -Math.sin(pitchRad);
        double lookVecZ = Math.cos(yawRad) * Math.cos(pitchRad);

        double targetVecX = deltaX;
        double targetVecY = deltaY;
        double targetVecZ = deltaZ;

        double length = Math.sqrt(targetVecX * targetVecX + targetVecY * targetVecY + targetVecZ * targetVecZ);
        targetVecX /= length;
        targetVecY /= length;
        targetVecZ /= length;

        double dotProduct = lookVecX * targetVecX + lookVecY * targetVecY + lookVecZ * targetVecZ;
        return dotProduct > 0.5; // Adjust this threshold as needed
    }
}
