package org.brutality.module.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.TimeHelper;

public class LongJump extends Module {

    private final NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", this, 1.0, 0.0, 10.0, 0);
    private final NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", this, 0.5, 0.0, 10.0, 0);
    private final NumberSetting boostTicks = new NumberSetting("Boost Ticks", this, 10, 1, 100, 0);
    private final BooleanSetting invertYaw = new BooleanSetting("Invert Yaw", this, false);

    private final TimeHelper banCooldown = new TimeHelper();
    private boolean placed = false;
    private boolean setSpeed = false;
    private int ticks = -1;
    private int lastSlot = -1;
    private int waitTicks = 0;

    // Static variable to track if the module is stopped
    private static boolean stopped = false;

    // Reference to the Fire Charge item
    private static final Item FIRE_CHARGE = Item.getItemFromBlock(Block.getBlockById(74)); // Item ID 74 for Fire Charge

    public LongJump() {
        super("LongJump", "Performs a long jump using a fire charge", Category.MOVEMENT);
        this.addSettings(horizontalSpeed, verticalSpeed, boostTicks, invertYaw);
    }

    @Override
    public void onEnable() {
        super.onEnable(); // Ensure to call superclass method to register with event bus
        stopped = false; // Reset stopped flag when enabling
        if (getFireCharge() == -1) {
            sendMessage("No fire charge found.");
            this.toggle(); // Toggles off the module if no fire charge is found
            return;
        }
        if (horizontalSpeed.getValue() == 0.0 && verticalSpeed.getValue() == 0.0) {
            sendMessage("Long jump values are set to 0.");
            this.toggle(); // Toggles off the module if settings are invalid
            return;
        }
        // Additional setup logic when enabled
    }

    @Override
    public void onDisable() {
        super.onDisable(); // Ensure to call superclass method to unregister from event bus
        stopped = true; // Set stopped flag when disabling
        if (lastSlot != -1) {
            Minecraft.getMinecraft().thePlayer.inventory.currentItem = lastSlot;
        }
        lastSlot = -1;
        ticks = -1;
        placed = false;
        setSpeed = false;
        waitTicks = 0;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPreMotion(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) {
            return;
        }

        if (horizontalSpeed.getValue() != 0.0 || verticalSpeed.getValue() != 0.0) {
            if (!placed) {
                if (mc.thePlayer.onGround) {
                    ++waitTicks;
                }
                if (getFireCharge() != -1 && getFireCharge() != mc.thePlayer.inventory.currentItem) {
                    lastSlot = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = getFireCharge();
                }
            }

            if (!placed && mc.thePlayer.onGround && waitTicks <= 3) {
                if (invertYaw.isEnabled()) {
                    mc.thePlayer.rotationYaw -= 180.0f;
                    mc.thePlayer.rotationPitch = 60.0f;
                } else {
                    mc.thePlayer.rotationPitch = 90.0f;
                }
            }

            if (waitTicks == 2 && !placed) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                placed = true;
            }

            if (ticks >= 0) {
                if (ticks >= boostTicks.getValue()) {
                    this.toggle(); // Turns off the module when the boost ticks are exceeded
                    return;
                }
                setSpeed();
                ++ticks;
            } else if (setSpeed) {
                if (ticks > boostTicks.getValue()) {
                    setSpeed = false;
                    ticks = 0;
                    return;
                }
                ++ticks;
                setSpeed();
            }
        }
    }

    private void setSpeed() {
        if (verticalSpeed.getValue() != 0.0) {
            mc.thePlayer.motionY = verticalSpeed.getValue() / 2.0 - Math.random() / 20.0;
        }
        if (horizontalSpeed.getValue() != 0.0) {
            mc.thePlayer.motionX *= horizontalSpeed.getValue();
            mc.thePlayer.motionZ *= horizontalSpeed.getValue();
        }
    }

    private int getFireCharge() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == FIRE_CHARGE) { // Fire Charge item
                return i;
            }
        }
        return -1;
    }

    private boolean holdingFireCharge() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == FIRE_CHARGE; // Fire Charge item
    }

    private void sendMessage(String message) {
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    // Static method to check if the module is stopped
    public static boolean isStopped() {
        return stopped;
    }

    // Static method to set the stopped flag
    public static void setStopped(boolean stopped) {
        LongJump.stopped = stopped;
    }
}
