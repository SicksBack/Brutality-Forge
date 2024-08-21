package org.brutality.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
import org.brutality.settings.impl.NumberSetting; // Import statement for NumberSetting

import java.awt.Color;
import java.util.Random;

public class Utils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Random random = new Random();

    /**
     * Checks if the player or the world is null.
     *
     * @return true if either the player or world is null, false otherwise.
     */
    public static boolean nullCheck() {
        return mc.thePlayer == null || mc.theWorld == null;
    }

    /**
     * Checks if the player is holding a sword.
     *
     * @return true if the player is holding a sword, false otherwise.
     */
    public static boolean holdingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    /**
     * Checks if the player is holding any weapon. Currently, this method only checks for swords.
     *
     * @return true if the player is holding a weapon, false otherwise.
     */
    public static boolean holdingWeapon() {
        return holdingSword();
    }

    /**
     * Attacks the specified entity.
     *
     * @param target    The entity to attack.
     * @param swingArm  Whether to swing the arm or not.
     * @param silent    Whether to send a silent attack packet or not.
     */
    public static void attackEntity(EntityLivingBase target, boolean swingArm, boolean silent) {
        if (nullCheck() || target == null) {
            return;
        }
        mc.thePlayer.swingItem();
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        if (silent) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
        }
    }

    /**
     * Gets a random value between min and max.
     *
     * @param min  The minimum value.
     * @param max  The maximum value.
     * @param rand The Random object used to generate random numbers.
     * @return A random value between min and max.
     */
    public static double getRandomValue(NumberSetting min, NumberSetting max, Random rand) {
        return min.getValue() + (max.getValue() - min.getValue()) * rand.nextDouble();
    }

    /**
     * Ensures that the minimum value is not greater than the maximum value.
     *
     * @param min The minimum value setting.
     * @param max The maximum value setting.
     */
    public static void correctValue(NumberSetting min, NumberSetting max) {
        if (min.getValue() > max.getValue()) {
            min.setValue(max.getValue());
        }
    }

    /**
     * Gets the difference between two time points.
     *
     * @param start The start time.
     * @param end   The end time.
     * @return The difference between the start and end times.
     */
    public static long getDifference(long start, long end) {
        return end - start;
    }

    /**
     * Formats a message to include Minecraft color codes.
     *
     * @param message The message to format.
     * @return The formatted message with Minecraft color codes.
     */
    public static String formatColor(String message) {
        return message.replaceAll("&", "\u00a7");
    }

    // Utility methods for Speed module

    /**
     * Checks if the player is currently moving.
     *
     * @return true if the player is moving, false otherwise.
     */
    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0;
    }

    /**
     * Gets the player's horizontal speed.
     *
     * @return The horizontal speed of the player.
     */
    public static double getHorizontalSpeed() {
        return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    /**
     * Sets the player's speed.
     *
     * @param speed The speed to set.
     */
    public static void setSpeed(double speed) {
        if (nullCheck()) {
            return;
        }
        float yaw = mc.thePlayer.rotationYaw;
        double sin = Math.sin(Math.toRadians(yaw));
        double cos = Math.cos(Math.toRadians(yaw));
        mc.thePlayer.motionX = speed * cos;
        mc.thePlayer.motionZ = speed * sin;
    }

    /**
     * Checks if the player is falling (moving downwards).
     *
     * @return true if the player is falling, false otherwise.
     */
    public static boolean jumpDown() {
        return mc.thePlayer.motionY < 0;
    }

    /**
     * Gets the color for the given health value.
     *
     * @param health The health value.
     * @return The color corresponding to the health value.
     */
    public static int getColorForHealth(float health) {
        return Color.HSBtoRGB((float) (health * 0.4), 0.75f, 0.85f);
    }

    /**
     * Draws a rounded gradient rectangle with specified colors.
     *
     * @param x      The x-coordinate of the rectangle.
     * @param y      The y-coordinate of the rectangle.
     * @param x2     The width of the rectangle.
     * @param y2     The height of the rectangle.
     * @param radius The radius of the corners.
     * @param color1 The first color.
     * @param color2 The second color.
     * @param color3 The third color.
     * @param color4 The fourth color.
     */
    public static void drawRoundedGradientRect(float x, float y, float x2, float y2, float radius, int color1, int color2, int color3, int color4) {
        // Your implementation here
    }

    /**
     * Draws a rounded gradient outlined rectangle with specified colors.
     *
     * @param x              The x-coordinate of the rectangle.
     * @param y              The y-coordinate of the rectangle.
     * @param x2             The width of the rectangle.
     * @param y2             The height of the rectangle.
     * @param radius         The radius of the corners.
     * @param outlineColor   The outline color.
     * @param startColor     The gradient start color.
     * @param endColor       The gradient end color.
     */
    public static void drawRoundedGradientOutlinedRectangle(float x, float y, float x2, float y2, float radius, int outlineColor, int startColor, int endColor) {
        // Your implementation here
    }

    /**
     * Draws a rounded rectangle with a specified color.
     *
     * @param x      The x-coordinate of the rectangle.
     * @param y      The y-coordinate of the rectangle.
     * @param x2     The width of the rectangle.
     * @param y2     The height of the rectangle.
     * @param radius The radius of the corners.
     * @param color  The color of the rectangle.
     */
    public static void drawRoundedRectangle(float x, float y, float x2, float y2, float radius, int color) {
        // Your implementation here
    }
}
