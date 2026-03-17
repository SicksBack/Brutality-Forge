package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

public class SprintDebug extends Module {

    private final BooleanSetting logAllTicks = new BooleanSetting("Log All Ticks", this, false);
    private final BooleanSetting logVelocity = new BooleanSetting("Log Velocity",  this, true);
    private final BooleanSetting logGround   = new BooleanSetting("Log Ground",    this, true);

    private boolean lastSprinting  = false;
    private boolean lastOnGround   = true;
    private double  lastHorizSpeed = 0;
    private int     tick           = 0;

    public SprintDebug() {
        super("SprintDebug", "Logs sprint state and velocity per tick.", Category.MOVEMENT);
        addSettings(logAllTicks, logVelocity, logGround);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastSprinting  = false;
        lastOnGround   = true;
        lastHorizSpeed = 0;
        tick           = 0;
        Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText("§aSprintDebug started — fight someone"));
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!isToggled()) return;
        if (event.phase != TickEvent.Phase.START) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        tick++;

        boolean sprinting   = mc.thePlayer.isSprinting();
        boolean onGround    = mc.thePlayer.onGround;
        boolean sprintKey   = mc.gameSettings.keyBindSprint.isKeyDown();

        double vx         = mc.thePlayer.motionX;
        double vz         = mc.thePlayer.motionZ;
        double horizSpeed = Math.sqrt(vx * vx + vz * vz);
        double velDelta   = Math.abs(horizSpeed - lastHorizSpeed);

        boolean sprintChanged = sprinting != lastSprinting;
        boolean groundChanged = onGround  != lastOnGround;
        boolean velChanged    = velDelta  > 0.005;

        boolean shouldLog = logAllTicks.isEnabled()
                || sprintChanged
                || (logGround.isEnabled()   && groundChanged)
                || (logVelocity.isEnabled() && velChanged);

        if (shouldLog) {
            StringBuilder sb = new StringBuilder();

            sb.append(EnumChatFormatting.GRAY).append("[").append(tick).append("] ");

            if (sprintChanged) {
                sb.append(sprinting
                        ? EnumChatFormatting.GREEN + "" + EnumChatFormatting.BOLD + "START_SPRINT "
                        : EnumChatFormatting.RED   + "" + EnumChatFormatting.BOLD + "STOP_SPRINT ");
            } else {
                sb.append(sprinting
                        ? EnumChatFormatting.GREEN + "SPR "
                        : EnumChatFormatting.RED   + "NO-SPR ");
            }

            sb.append(sprintKey
                    ? EnumChatFormatting.GREEN + "KEY↑ "
                    : EnumChatFormatting.RED   + "KEY↓ ");

            if (logGround.isEnabled()) {
                if (groundChanged) {
                    sb.append(onGround
                            ? EnumChatFormatting.AQUA         + "" + EnumChatFormatting.BOLD + "LANDED "
                            : EnumChatFormatting.LIGHT_PURPLE + "" + EnumChatFormatting.BOLD + "AIRBORNE ");
                } else {
                    sb.append(onGround
                            ? EnumChatFormatting.AQUA         + "GND "
                            : EnumChatFormatting.LIGHT_PURPLE + "AIR ");
                }
            }

            if (logVelocity.isEnabled()) {
                sb.append(EnumChatFormatting.YELLOW)
                  .append("spd=").append(String.format("%.4f", horizSpeed));
                if (velChanged) {
                    sb.append(EnumChatFormatting.GRAY)
                      .append("(d=").append(String.format("%.4f", velDelta)).append(")");
                }
            }

            mc.thePlayer.addChatMessage(new ChatComponentText(sb.toString()));
        }

        lastSprinting  = sprinting;
        lastOnGround   = onGround;
        lastHorizSpeed = horizSpeed;
    }
}