package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side sprint debug — mirrors the Raven SD format exactly.
 * Only logs: JUMP, LANDED, START_SPRINT, STOP_SPRINT, and summary.
 * No velocity-change noise, no suppression spam.
 * Compare directly to the server-side SprintDebugCommand output.
 */
public class SprintDebug extends Module {

    private final BooleanSetting logSummary = new BooleanSetting("Log Summary", this, true);

    // ── Tick state ────────────────────────────────────────────────────────────

    private boolean lastSprinting  = false;
    private boolean lastOnGround   = true;
    private int     tick           = 0;

    // ── Jump / flight tracking ────────────────────────────────────────────────

    private int     jumpTick         = -1;
    private int     airDuration      = 0;
    private int     burstsThisFlight = 0;

    // ── Burst tracking ────────────────────────────────────────────────────────

    private int    sprintStartTick  = -1;
    private double sprintStartVel   = 0.0;
    private double sprintStartY     = 0.0;
    private int    lastBurstEndTick = -1;

    // ── Summary analytics ─────────────────────────────────────────────────────

    private final List<Integer> burstDurations     = new ArrayList<>();
    private final List<Double>  burstStartSpeeds   = new ArrayList<>();
    private final List<Double>  burstStopSpeeds    = new ArrayList<>();
    private final List<Double>  burstVelChanges    = new ArrayList<>();
    private final List<Integer> gapDurations       = new ArrayList<>();
    private final List<Integer> jumpToSprintDelays = new ArrayList<>();
    private final List<Double>  burstStartYVels    = new ArrayList<>();
    private final List<Integer> burstFlightPos     = new ArrayList<>();
    private final List<Integer> burstsPerFlight    = new ArrayList<>();

    private int totalJumps       = 0;
    private int burstsInAir      = 0;
    private int burstsOnGround   = 0;
    private int flightsWithBurst = 0;
    private int flightsNoBurst   = 0;

    // ── Constructor ───────────────────────────────────────────────────────────

    public SprintDebug() {
        super("SprintDebug", "h", Category.MOVEMENT);
        addSettings(logSummary);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
        chat(EnumChatFormatting.GREEN + "[SprintDebug] ON");
    }

    @Override
    public void onDisable() {
        if (logSummary.isEnabled() && !burstDurations.isEmpty()) printSummary();
        resetState();
        super.onDisable();
    }

    private void resetState() {
        tick             = 0;
        lastSprinting    = false;
        lastOnGround     = true;
        jumpTick         = -1;
        airDuration      = 0;
        burstsThisFlight = 0;
        sprintStartTick  = -1;
        sprintStartVel   = 0.0;
        sprintStartY     = 0.0;
        lastBurstEndTick = -1;
        burstDurations.clear();
        burstStartSpeeds.clear();
        burstStopSpeeds.clear();
        burstVelChanges.clear();
        gapDurations.clear();
        jumpToSprintDelays.clear();
        burstStartYVels.clear();
        burstFlightPos.clear();
        burstsPerFlight.clear();
        totalJumps = burstsInAir = burstsOnGround = 0;
        flightsWithBurst = flightsNoBurst = 0;
    }

    // ── Tick handler ──────────────────────────────────────────────────────────

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (!isToggled()) return;
        if (event.phase != TickEvent.Phase.START) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return;

        tick++;

        boolean sprinting = mc.thePlayer.isSprinting();
        boolean onGround  = mc.thePlayer.onGround;

        double vx  = mc.thePlayer.motionX;
        double vy  = mc.thePlayer.motionY;
        double vz  = mc.thePlayer.motionZ;
        double spd = Math.sqrt(vx * vx + vz * vz);
        double velDelta = spd - (lastOnGround == onGround ? getPrevSpeed() : spd); // computed inline below

        boolean justJumped = lastOnGround && !onGround;
        boolean justLanded = !lastOnGround && onGround;

        // ── JUMP ─────────────────────────────────────────────────────────────
        if (justJumped) {
            totalJumps++;
            jumpTick         = tick;
            airDuration      = 0;
            burstsThisFlight = 0;

            chat(EnumChatFormatting.LIGHT_PURPLE + "JUMP "
                    + EnumChatFormatting.YELLOW + "vel=" + fmt(spd)
                    + EnumChatFormatting.GRAY   + "(Δ" + fmtSigned(spd - prevSpeed) + ")"
                    + " yVel=" + fmt(vy)
                    + (sprinting
                    ? EnumChatFormatting.GREEN + " [SPR@JUMP]"
                    : EnumChatFormatting.RED   + " [NO-SPR@JUMP]"));
        }

        // ── LANDED ───────────────────────────────────────────────────────────
        if (justLanded) {
            int airTicks = jumpTick >= 0 ? (tick - jumpTick) : -1;

            if (burstsThisFlight > 0) {
                flightsWithBurst++;
                if (burstsThisFlight > 1) burstsPerFlight.add(burstsThisFlight);
            } else {
                flightsNoBurst++;
            }

            chat(EnumChatFormatting.AQUA + "LANDED "
                    + EnumChatFormatting.YELLOW + "vel=" + fmt(spd)
                    + (airTicks >= 0
                    ? EnumChatFormatting.GRAY + " airTime=" + airTicks + "t"
                    : "")
                    + EnumChatFormatting.GRAY + " flightBursts=" + burstsThisFlight);
        }

        if (!onGround) airDuration++;

        // ── Sprint transitions ────────────────────────────────────────────────
        if (sprinting != lastSprinting) {

            if (sprinting) {
                // START_SPRINT
                sprintStartTick = tick;
                sprintStartVel  = spd;
                sprintStartY    = vy;

                if (lastBurstEndTick >= 0)
                    gapDurations.add(tick - lastBurstEndTick);

                String jumpDelayStr = "";
                String flightPosStr = "";

                if (jumpTick >= 0 && !onGround) {
                    int delay    = tick - jumpTick;
                    int fPos     = airDuration;
                    jumpDelayStr = EnumChatFormatting.GRAY + " jumpDelay=" + delay + "t";
                    flightPosStr = EnumChatFormatting.GRAY + " flightPos=" + fPos + "t";
                    jumpToSprintDelays.add(delay);
                    burstFlightPos.add(fPos);
                    burstsInAir++;
                    burstsThisFlight++;
                } else if (onGround) {
                    burstsOnGround++;
                }

                burstStartYVels.add(vy);

                chat(EnumChatFormatting.GREEN + "START_SPRINT "
                        + EnumChatFormatting.YELLOW + "vel=" + fmt(spd)
                        + EnumChatFormatting.GRAY   + "(Δ" + fmtSigned(spd - prevSpeed) + ") "
                        + (onGround
                        ? EnumChatFormatting.AQUA         + "GND"
                        : EnumChatFormatting.LIGHT_PURPLE + "AIR")
                        + jumpDelayStr
                        + flightPosStr
                        + EnumChatFormatting.GRAY + " yVel=" + fmt(vy));

            } else {
                // STOP_SPRINT
                int burstLen     = sprintStartTick >= 0 ? (tick - sprintStartTick) : -1;
                lastBurstEndTick = tick;

                if (burstLen >= 0) {
                    burstDurations.add(burstLen);
                    burstStartSpeeds.add(sprintStartVel);
                    burstStopSpeeds.add(spd);
                    burstVelChanges.add(spd - sprintStartVel);
                }

                String flags = "";
                if      (burstLen == 0)  flags = EnumChatFormatting.RED + " ⚠ZERO-TICK";
                else if (burstLen >= 10) flags = EnumChatFormatting.RED + " ⚠LONG(" + burstLen + "t)";

                double boost = spd - sprintStartVel;

                chat(EnumChatFormatting.RED + "STOP_SPRINT "
                        + EnumChatFormatting.YELLOW + "vel=" + fmt(spd)
                        + EnumChatFormatting.GRAY   + "(Δ" + fmtSigned(spd - prevSpeed) + ") "
                        + (onGround
                        ? EnumChatFormatting.AQUA         + "GND"
                        : EnumChatFormatting.LIGHT_PURPLE + "AIR")
                        + (burstLen >= 0
                        ? EnumChatFormatting.GRAY + " burst=" + burstLen + "t"
                        : "")
                        + EnumChatFormatting.GRAY + " boost=" + fmtSigned(boost)
                        + " yVel=" + fmt(vy)
                        + flags);
            }
        }

        prevSpeed      = spd;
        lastSprinting  = sprinting;
        lastOnGround   = onGround;
    }

    // Need prevSpeed as a field (used inside onTick for velDelta)
    private double prevSpeed = 0.0;

    // dead stub — velDelta is computed from prevSpeed field directly above
    private double getPrevSpeed() { return prevSpeed; }

    // ── Summary ───────────────────────────────────────────────────────────────

    private void printSummary() {
        int count = burstDurations.size();
        chat(EnumChatFormatting.GOLD + "══ SprintDebug Summary ══");

        double avgBurst = average(burstDurations);
        int minBurst    = burstDurations.stream().mapToInt(i -> i).min().orElse(0);
        int maxBurst    = burstDurations.stream().mapToInt(i -> i).max().orElse(0);
        long burst1t    = burstDurations.stream().filter(d -> d == 1).count();
        long burst2t    = burstDurations.stream().filter(d -> d == 2).count();
        long burstLong  = burstDurations.stream().filter(d -> d >= 10).count();

        chat(EnumChatFormatting.GREEN + "Bursts: " + EnumChatFormatting.WHITE + count
                + EnumChatFormatting.GRAY
                + "  avg=" + String.format("%.1f", avgBurst) + "t"
                + "  min=" + minBurst + "t  max=" + maxBurst + "t"
                + (maxBurst >= 10 ? EnumChatFormatting.RED + " ⚠long" : ""));

        chat(EnumChatFormatting.GRAY
                + "  burst=1t: " + burst1t + " (" + pct(burst1t, count) + "%)"
                + "  burst=2t: " + burst2t + " (" + pct(burst2t, count) + "%)"
                + "  burst≥10t: " + burstLong + " (" + pct(burstLong, count) + "%)");

        double avgStart = averageD(burstStartSpeeds);
        double avgStop  = averageD(burstStopSpeeds);
        chat(EnumChatFormatting.YELLOW + "START vel avg=" + fmt(avgStart)
                + EnumChatFormatting.GRAY + "  range="
                + fmt(burstStartSpeeds.stream().mapToDouble(d -> d).min().orElse(0))
                + "–" + fmt(burstStartSpeeds.stream().mapToDouble(d -> d).max().orElse(0)));
        chat(EnumChatFormatting.RED + "STOP  vel avg=" + fmt(avgStop)
                + EnumChatFormatting.GRAY + "  range="
                + fmt(burstStopSpeeds.stream().mapToDouble(d -> d).min().orElse(0))
                + "–" + fmt(burstStopSpeeds.stream().mapToDouble(d -> d).max().orElse(0)));
        chat(EnumChatFormatting.AQUA + "Avg boost/burst: "
                + EnumChatFormatting.WHITE + fmtSigned(averageD(burstVelChanges)));

        if (!gapDurations.isEmpty()) {
            chat(EnumChatFormatting.GRAY + "Gap between bursts: avg="
                    + String.format("%.1f", average(gapDurations)) + "t"
                    + "  min=" + gapDurations.stream().mapToInt(i -> i).min().orElse(0) + "t"
                    + "  max=" + gapDurations.stream().mapToInt(i -> i).max().orElse(0) + "t");
        }

        if (!jumpToSprintDelays.isEmpty()) {
            long delay0 = jumpToSprintDelays.stream().filter(d -> d == 0).count();
            chat(EnumChatFormatting.LIGHT_PURPLE + "Jump→Sprint delay: avg="
                    + String.format("%.1f", average(jumpToSprintDelays)) + "t"
                    + "  min=" + jumpToSprintDelays.stream().mapToInt(i -> i).min().orElse(0) + "t"
                    + "  max=" + jumpToSprintDelays.stream().mapToInt(i -> i).max().orElse(0) + "t"
                    + EnumChatFormatting.GRAY + "  delay=0: " + delay0 + " (" + pct(delay0, jumpToSprintDelays.size()) + "%)");
        }

        if (!burstFlightPos.isEmpty()) {
            chat(EnumChatFormatting.GRAY + "Burst flight pos: avg="
                    + String.format("%.1f", average(burstFlightPos)) + "t"
                    + "  min=" + burstFlightPos.stream().mapToInt(i -> i).min().orElse(0) + "t"
                    + "  max=" + burstFlightPos.stream().mapToInt(i -> i).max().orElse(0) + "t");
        }

        if (!burstStartYVels.isEmpty()) {
            long onAscent  = burstStartYVels.stream().filter(y -> y > 0.05).count();
            long onDescent = burstStartYVels.stream().filter(y -> y < -0.05).count();
            chat(EnumChatFormatting.YELLOW + "yVel at burst start: avg=" + fmt(averageD(burstStartYVels))
                    + EnumChatFormatting.GRAY + "  ascent=" + onAscent + "  descent=" + onDescent);
        }

        int totalFlights = flightsWithBurst + flightsNoBurst;
        chat(EnumChatFormatting.GRAY
                + "Jumps=" + totalJumps
                + "  AirBursts=" + burstsInAir
                + "  GroundBursts=" + burstsOnGround);

        if (totalFlights > 0) {
            double rate = 100.0 * flightsWithBurst / totalFlights;
            chat(EnumChatFormatting.GRAY
                    + "Trigger rate: " + String.format("%.1f", rate) + "%"
                    + "  flights=" + totalFlights
                    + "  withBurst=" + flightsWithBurst
                    + "  skipped=" + flightsNoBurst
                    + (rate > 95
                    ? EnumChatFormatting.RED    + " ⚠ALWAYS-TRIGGERS"
                    : rate < 50
                    ? EnumChatFormatting.YELLOW + " ⚠VERY LOW"
                    : EnumChatFormatting.GREEN  + " ✓normal"));
        }

        if (!burstsPerFlight.isEmpty()) {
            chat(EnumChatFormatting.GRAY + "Multi-burst flights: " + burstsPerFlight.size()
                    + "  avg=" + String.format("%.1f", average(burstsPerFlight)));
        }

        if (count >= 5) {
            double stdDev  = stddev(burstDurations);
            String verdict = stdDev < 0.3
                    ? EnumChatFormatting.RED    + "⚠ VERY CONSISTENT"
                    : stdDev < 0.8
                    ? EnumChatFormatting.YELLOW + "Fairly consistent"
                    : EnumChatFormatting.GREEN  + "Varied (human-like)";
            chat("Burst stddev=" + String.format("%.2f", stdDev) + " → " + verdict);
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String pct(long part, long total) {
        if (total == 0) return "0";
        return String.format("%.0f", 100.0 * part / total);
    }

    private static String fmt(double v)       { return String.format("%.4f", v); }
    private static String fmtSigned(double v) { return (v >= 0 ? "+" : "") + String.format("%.4f", v); }

    private static double average(List<Integer> l)  { return l.stream().mapToInt(i -> i).average().orElse(0); }
    private static double averageD(List<Double>  l)  { return l.stream().mapToDouble(d -> d).average().orElse(0); }
    private static double stddev(List<Integer> l) {
        double avg = average(l);
        return Math.sqrt(l.stream().mapToDouble(i -> (i - avg) * (i - avg)).average().orElse(0));
    }

    private static void chat(String msg) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null)
            mc.thePlayer.addChatMessage(new ChatComponentText(msg));
    }
}