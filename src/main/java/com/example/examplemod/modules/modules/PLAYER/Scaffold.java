/*      */ package Acrimony.module.impl.player;
/*      */ import Acrimony.Acrimony;
/*      */ import Acrimony.event.Event;
/*      */ import Acrimony.event.Listener;
/*      */ import Acrimony.event.impl.EntityActionEvent;
/*      */ import Acrimony.event.impl.JumpEvent;
/*      */ import Acrimony.event.impl.MotionEvent;
/*      */ import Acrimony.event.impl.MoveEvent;
/*      */ import Acrimony.event.impl.PacketSendEvent;
/*      */ import Acrimony.event.impl.Render3DEvent;
/*      */ import Acrimony.event.impl.StrafeEvent;
/*      */ import Acrimony.module.Category;
/*      */ import Acrimony.setting.AbstractSetting;
/*      */ import Acrimony.setting.impl.BooleanSetting;
/*      */ import Acrimony.setting.impl.CustomDoubleSetting;
/*      */ import Acrimony.setting.impl.DoubleSetting;
/*      */ import Acrimony.setting.impl.IntegerSetting;
/*      */ import Acrimony.setting.impl.ModeSetting;
/*      */ import Acrimony.util.misc.KeyboardUtil;
/*      */ import Acrimony.util.misc.LogUtil;
/*      */ import Acrimony.util.misc.TimerUtil;
/*      */ import Acrimony.util.network.PacketUtil;
/*      */ import Acrimony.util.player.FixedRotations;
/*      */ import Acrimony.util.player.InventoryUtil;
/*      */ import Acrimony.util.player.MovementUtil;
/*      */ import Acrimony.util.player.RotationsUtil;
/*      */ import Acrimony.util.render.RenderUtil;
/*      */ import Acrimony.util.world.BlockInfo;
/*      */ import Acrimony.util.world.WorldUtil;
/*      */ import java.awt.Color;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Iterator;
/*      */ import java.util.Random;
/*      */ import java.util.concurrent.ThreadLocalRandom;
/*      */ import net.minecraft.item.ItemStack;
/*      */ import net.minecraft.network.Packet;
/*      */ import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
/*      */ import net.minecraft.potion.Potion;
/*      */ import net.minecraft.util.BlockPos;
/*      */ import net.minecraft.util.EnumFacing;
/*      */ import net.minecraft.util.MathHelper;
/*      */ import net.minecraft.util.MovingObjectPosition;
/*      */ import net.minecraft.util.Vec3;
/*      */ 
/*      */ public class Scaffold extends Module {
/*   46 */   private final ArrayList<PlacedBlock> blockPlaceList = new ArrayList<>();
/*   47 */   private final ModeSetting mode = new ModeSetting("Mode", "Basic", new String[] { "Basic", "Verus", "Hypixel", "Hypixel jump", "Basic custom", "Godbridge", "Sneak", "Andromeda" });
/*      */   
/*   49 */   private final ModeSetting basicRotations = new ModeSetting("basic-rotations", "Rotations", () -> Boolean.valueOf(this.mode.is("Basic")), "Block center", new String[] { "Movement based", "Block center" });
/*   50 */   private final ModeSetting jumpSprintMode = new ModeSetting("Bypass Mode", () -> Boolean.valueOf(this.mode.is("Hypixel jump")), "Rise/Opal", new String[] { "Rise/Opal", "Novoline" });
/*      */   
/*   52 */   private final ModeSetting jumpMode = new ModeSetting("Jump mode", () -> Boolean.valueOf(this.mode.is("Basic")), "None", new String[] { "None", "Normal", "Place when falling" });
/*      */   
/*   54 */   private final ModeSetting noSprint = new ModeSetting("No sprint mode", () -> Boolean.valueOf(this.mode.is("Basic")), "None", new String[] { "None", "Enabled", "Spoof" });
/*      */ 
/*      */   
/*   57 */   private final BooleanSetting showRotationsSettings = new BooleanSetting("Show rotations settings", () -> Boolean.valueOf(this.mode.is("Basic custom")), false);
/*   58 */   private final BooleanSetting showMovementSettings = new BooleanSetting("Show movement settings", () -> Boolean.valueOf(this.mode.is("Basic custom")), false);
/*   59 */   private final BooleanSetting showPlacementSettings = new BooleanSetting("Show placement settings", () -> Boolean.valueOf(this.mode.is("Basic custom")), false);
/*      */   
/*   61 */   private final ModeSetting bCustomRotationsTiming = new ModeSetting("bCustomRotationsTiming", "Rotations timing", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showRotationsSettings.isEnabled())), "Always", new String[] { "Never", "Always", "Over air", "When placing", "When not jumping" });
/*   62 */   private final ModeSetting bCustomRotationsMode = new ModeSetting("bCustomRotations", "Rotations", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && this.showRotationsSettings.isEnabled())), "Facing", new String[] { "Facing", "Block center", "Movement based", "Godbridge", "Raytrace pitch", "Static" });
/*      */   
/*   64 */   private final IntegerSetting bCustomYawOffset = new IntegerSetting("bCustomYawOffset", "Yaw offset", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && (this.bCustomRotationsMode.is("Movement based") || this.bCustomRotationsMode.is("Raytrace pitch")) && this.showRotationsSettings.isEnabled())), 180, 0, 180, 5);
/*   65 */   private final CustomDoubleSetting bCustomPitchValue = new CustomDoubleSetting("bCustomPitch", "Pitch", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && (this.bCustomRotationsMode.is("Movement based") || this.bCustomRotationsMode.is("Facing")) && this.showRotationsSettings.isEnabled())), 82.0D);
/*      */   
/*   67 */   private final BooleanSetting instantRotations = new BooleanSetting("Instant rotations", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && this.showRotationsSettings.isEnabled())), true);
/*      */   
/*   69 */   private final ModeSetting yawSpeedMode = new ModeSetting("Yaw speed mode", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), "Randomised", new String[] { "Randomised", "Acceleration" });
/*      */   
/*   71 */   private final DoubleSetting minYawSpeed = new DoubleSetting("Min yaw speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 30.0D, 5.0D, 180.0D, 2.5D);
/*   72 */   private final DoubleSetting maxYawSpeed = new DoubleSetting("Max yaw speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 35.0D, 5.0D, 180.0D, 2.5D);
/*      */   
/*   74 */   private final DoubleSetting minYawAccel = new DoubleSetting("Min yaw accel", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.yawSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), 4.0D, 0.0D, 25.0D, 0.25D);
/*   75 */   private final DoubleSetting maxYawAccel = new DoubleSetting("Max yaw accel", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.yawSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), 4.0D, 0.0D, 25.0D, 0.25D);
/*      */   
/*   77 */   private final BooleanSetting reduceYawSpeedWhenAlmostDone = new BooleanSetting("Reduce yaw speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.yawSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), false);
/*      */   
/*   79 */   private final DoubleSetting minYawSpeedWhenAlmostDone = new DoubleSetting("Min yaw speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.yawSpeedMode.is("Acceleration") && this.reduceYawSpeedWhenAlmostDone.isEnabled() && this.showRotationsSettings.isEnabled())), 5.0D, 0.0D, 50.0D, 0.5D);
/*   80 */   private final DoubleSetting maxYawSpeedWhenAlmostDone = new DoubleSetting("Max yaw speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.yawSpeedMode.is("Acceleration") && this.reduceYawSpeedWhenAlmostDone.isEnabled() && this.showRotationsSettings.isEnabled())), 5.0D, 0.0D, 50.0D, 0.5D);
/*      */   
/*   82 */   private final ModeSetting pitchSpeedMode = new ModeSetting("Pitch speed mode", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), "Randomised", new String[] { "Randomised", "Acceleration" });
/*      */   
/*   84 */   private final DoubleSetting minPitchSpeed = new DoubleSetting("Min pitch speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 10.0D, 2.0D, 180.0D, 2.0D);
/*   85 */   private final DoubleSetting maxPitchSpeed = new DoubleSetting("Max pitch speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 10.0D, 2.0D, 180.0D, 2.0D);
/*      */   
/*   87 */   private final DoubleSetting minPitchAccel = new DoubleSetting("Min pitch accel", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.pitchSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), 4.0D, 0.0D, 25.0D, 0.25D);
/*   88 */   private final DoubleSetting maxPitchAccel = new DoubleSetting("Max pitch accel", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.pitchSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), 4.0D, 0.0D, 25.0D, 0.25D);
/*      */   
/*   90 */   private final BooleanSetting reducePitchSpeedWhenAlmostDone = new BooleanSetting("Reduce pitch speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.pitchSpeedMode.is("Acceleration") && this.showRotationsSettings.isEnabled())), false);
/*      */   
/*   92 */   private final DoubleSetting minPitchSpeedWhenAlmostDone = new DoubleSetting("Min pitch speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.pitchSpeedMode.is("Acceleration") && this.reducePitchSpeedWhenAlmostDone.isEnabled() && this.showRotationsSettings.isEnabled())), 5.0D, 0.0D, 50.0D, 0.5D);
/*   93 */   private final DoubleSetting maxPitchSpeedWhenAlmostDone = new DoubleSetting("Max pitch speed when almost done", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.pitchSpeedMode.is("Acceleration") && this.reducePitchSpeedWhenAlmostDone.isEnabled() && this.showRotationsSettings.isEnabled())), 5.0D, 0.0D, 50.0D, 0.5D);
/*      */   
/*   95 */   private final DoubleSetting minYawChange = new DoubleSetting("Min yaw change", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 2.0D, 0.0D, 5.0D, 0.1D);
/*   96 */   private final DoubleSetting minPitchChange = new DoubleSetting("Min pitch change", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 0.8D, 0.0D, 5.0D, 0.1D);
/*   97 */   private final DoubleSetting rotsRandomisation = new DoubleSetting("Rots randomisation", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), 1.6D, 0.0D, 25.0D, 0.25D);
/*      */   
/*   99 */   private final BooleanSetting bCustomResetRotsIfNotRotating = new BooleanSetting("bCustomResetRotsIfNotRotating", "Reset rots if not rotating", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.bCustomRotationsTiming.is("Never") && !this.bCustomRotationsTiming.is("Always") && !this.instantRotations.isEnabled() && this.showRotationsSettings.isEnabled())), false);
/*      */ 
/*      */   
/*  102 */   private final ModeSetting customNoSprintTiming = new ModeSetting("customNoSprintTiming", "No sprint timing", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), "Never", new String[] { "Never", "Always", "Over air", "When placing", "Onground", "Offground" });
/*      */   
/*  104 */   private final ModeSetting customNoSprintMode = new ModeSetting("customNoSprintMode", "No sprint mode", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.customNoSprintTiming.is("Never") && this.showMovementSettings.isEnabled())), "Enabled", new String[] { "Enabled", "Spoof" });
/*      */   
/*  106 */   private final BooleanSetting customAllowJumpBoost = new BooleanSetting("customAllowJumpBoost", "Allow jump boost", () -> Boolean.valueOf((this.mode.is("Basic custom") && (!this.customNoSprintTiming.is("Always") || !this.noSprint.is("Enabled")) && this.showMovementSettings.isEnabled())), true);
/*      */   
/*  108 */   private final ModeSetting customSneakTiming = new ModeSetting("customSneakTiming", "Sneak timing", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), "Never", new String[] { "Never", "Always", "Over air", "Over air and place fail", "When placing", "Every x blocks", "Alternate" });
/*  109 */   private final IntegerSetting customSneakFrequency = new IntegerSetting("customSneakFrequency", "Sneak frequency", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.customSneakTiming.is("Every x blocks") && this.showMovementSettings.isEnabled())), 2, 1, 10, 1);
/*  110 */   private final BooleanSetting customSneakOffground = new BooleanSetting("customSneakOffground", "Sneak offground", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.customSneakTiming.is("Never") && this.showMovementSettings.isEnabled())), false);
/*      */   
/*  112 */   private final ModeSetting customSneakMode = new ModeSetting("customSneakMode", "Sneak mode", () -> Boolean.valueOf((this.mode.is("Basic custom") && !this.customSneakTiming.is("Never") && this.showMovementSettings.isEnabled())), "Enabled", new String[] { "Enabled", "Spoof" });
/*      */   
/*  114 */   private final ModeSetting customGroundspoof = new ModeSetting("customGroundspoof", "Groundspoof", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), "Disabled", new String[] { "Disabled", "Offground", "Alternate" });
/*      */   
/*  116 */   private final ModeSetting customJumpMode = new ModeSetting("customJumpMode", "Jump mode", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), "None", new String[] { "None", "Normal", "Place when falling", "Godbridge" });
/*      */   
/*  118 */   private final ModeSetting movementType = new ModeSetting("Movement type", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), "Normal", new String[] { "Normal", "Strafe", "Fixed" });
/*      */   
/*  120 */   private final DoubleSetting customMotionMultOnGround = new DoubleSetting("customMotionMultOnGround", "Motion mult onground", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Normal") && this.showMovementSettings.isEnabled())), 1.0D, 0.5D, 1.4D, 0.01D);
/*  121 */   private final DoubleSetting customMotionMultOffGround = new DoubleSetting("customMotionMultOffGround", "Motion mult offground", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Normal") && this.showMovementSettings.isEnabled())), 1.0D, 0.5D, 1.4D, 0.01D);
/*  122 */   private final BooleanSetting customMultAffectsNextMotion = new BooleanSetting("customMultAffectsNextMotion", "Mult affects next motion", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Normal") && this.showMovementSettings.isEnabled())), false);
/*  123 */   private final DoubleSetting customJumpBoostAmount = new DoubleSetting("customJumpBoostAmount", "Jump boost amount", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Normal") && (!this.customNoSprintTiming.is("Always") || !this.noSprint.is("Enabled")) && this.customAllowJumpBoost.isEnabled() && this.showMovementSettings.isEnabled())), 0.2D, 0.0D, 0.4D, 0.005D);
/*      */   
/*  125 */   private final DoubleSetting customOnGroundSpeed = new DoubleSetting("customOnGroundSpeed", "Onground speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Strafe") && this.showMovementSettings.isEnabled())), 0.28D, 0.1D, 0.5D, 0.005D);
/*      */   
/*  127 */   private final BooleanSetting customOffGroundStrafe = new BooleanSetting("customOffGroundStrafe", "Offground strafe", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Strafe") && this.showMovementSettings.isEnabled())), true);
/*  128 */   private final DoubleSetting customOffGroundSpeed = new DoubleSetting("customOffGroundSpeed", "Offground speed", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Strafe") && this.customOffGroundStrafe.isEnabled() && this.showMovementSettings.isEnabled())), 0.28D, 0.1D, 0.5D, 0.005D);
/*      */   
/*  130 */   private final DoubleSetting customOnGroundPotionExtra = new DoubleSetting("customOnGroundPotionExtra", "Onground potion extra", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Strafe") && this.showMovementSettings.isEnabled())), 0.02D, 0.0D, 0.2D, 0.005D);
/*  131 */   private final DoubleSetting customOffGroundPotionExtra = new DoubleSetting("customOffGroundPotionExtra", "Offground potion extra", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.movementType.is("Strafe") && this.customOffGroundStrafe.isEnabled() && this.showMovementSettings.isEnabled())), 0.02D, 0.0D, 0.2D, 0.005D);
/*      */   
/*  133 */   private final BooleanSetting customIgnoreSpeedPot = new BooleanSetting("customIgnoreSpeedPot", "Ignore speed pot", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), false);
/*  134 */   private final IntegerSetting customNoMoveTicksOnStart = new IntegerSetting("customNoMoveTicksOnStart", "No move ticks on start", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showMovementSettings.isEnabled())), 0, 0, 5, 1);
/*      */ 
/*      */   
/*  137 */   private final BooleanSetting bCustomOnlyPlaceIfRaytraceSuccess = new BooleanSetting("bCustomOnlyPlaceIfRaytraceSuccess", "Only place if raytrace success", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), true);
/*      */   
/*  139 */   private final IntegerSetting range = new IntegerSetting("Range", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), 2, 1, 4, 1);
/*      */   
/*  141 */   private final DoubleSetting distFromBlock = new DoubleSetting("Dist from block", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), 0.0D, 0.0D, 0.24D, 0.01D);
/*  142 */   private final DoubleSetting offGroundDistFromBlock = new DoubleSetting("Offground dist from block", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), 0.0D, 0.0D, 0.24D, 0.01D);
/*      */   
/*  144 */   private final IntegerSetting minPlaceDelay = new IntegerSetting("Min place delay", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), 1, 1, 10, 1);
/*  145 */   private final IntegerSetting maxPlaceDelay = new IntegerSetting("Max place delay", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), 1, 1, 10, 1);
/*      */   
/*  147 */   private final BooleanSetting applyPlaceDelayOffground = new BooleanSetting("Apply place delay offground", () -> Boolean.valueOf((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled())), false);
/*      */   
/*  149 */   private final ModeSetting extraRightClicks = new ModeSetting("Extra right clicks", () -> Boolean.valueOf(((this.mode.is("Basic custom") && this.showPlacementSettings.isEnabled()) || this.mode.is("Sneak") || this.mode.is("Godbridge"))), "Normal", new String[] { "Disabled", "Normal", "Dragclick", "Always" });
/*      */ 
/*      */ 
/*      */   
/*  153 */   private final BooleanSetting modulo45Rots = new BooleanSetting("Modulo 45 rots", () -> Boolean.valueOf((this.mode.is("Raytrace custom") && this.showRotationsSettings.isEnabled())), true);
/*      */ 
/*      */   
/*  156 */   private final IntegerSetting expand = new IntegerSetting("Expand", () -> Boolean.valueOf(this.mode.is("Basic")), 0, 0, 4, 1);
/*      */   
/*  158 */   private final BooleanSetting jump = new BooleanSetting("Jump", () -> Boolean.valueOf(this.mode.is("Verus")), false);
/*      */ 
/*      */   
/*  161 */   private final ModeSetting hypixelSprint = new ModeSetting("Hypixel sprint", () -> Boolean.valueOf(this.mode.is("Hypixel")), "None", new String[] { "None", "Semi" });
/*  162 */   private final BooleanSetting jumpToAvoidSetback = new BooleanSetting("Jump to avoid setback", () -> Boolean.valueOf((this.mode.is("Hypixel") && this.hypixelSprint.is("Semi"))), true);
/*  163 */   private final ModeSetting hypixelTower = new ModeSetting("Hypixel tower", () -> Boolean.valueOf(this.mode.is("Hypixel")), "Faster vertically", new String[] { "None", "Faster vertically", "Faster horizontally", "Legit" });
/*      */   
/*  165 */   private final DoubleSetting towerSpeed = new DoubleSetting("Speed", () -> Boolean.valueOf((this.mode.is("Hypixel") && this.hypixelTower.is("Faster vertically"))), 0.28D, 0.2D, 0.28D, 0.005D);
/*  166 */   private final DoubleSetting towerSpeedWhenDiagonal = new DoubleSetting("Diagonal Speed", () -> Boolean.valueOf((this.mode.is("Hypixel") && this.hypixelTower.is("Faster vertically"))), 0.22D, 0.2D, 0.28D, 0.005D);
/*      */ 
/*      */   
/*  169 */   private final BooleanSetting stillPlaceOnRaytraceFail = new BooleanSetting("Still place on raytrace fail", () -> Boolean.valueOf(this.mode.is("Godbridge")), true);
/*  170 */   private final BooleanSetting debugOnRaytraceFail = new BooleanSetting("Debug on raytrace fail", () -> Boolean.valueOf(this.mode.is("Godbridge")), true);
/*      */ 
/*      */   
/*  173 */   private final BooleanSetting rotationsEnabled = new BooleanSetting("rotations-enabled", "Rotations", () -> Boolean.valueOf(this.mode.is("Andromeda")), true);
/*  174 */   private final BooleanSetting moveFix = new BooleanSetting("Move fix", () -> Boolean.valueOf((this.mode.is("Andromeda") && this.rotationsEnabled.isEnabled())), true);
/*  175 */   private final BooleanSetting noPlaceOnJumpTick = new BooleanSetting("No place on jump tick", () -> Boolean.valueOf((this.mode.is("Andromeda") && this.rotationsEnabled.isEnabled() && this.moveFix.isEnabled())), false);
/*      */   
/*  177 */   public final BooleanSetting blockPlaceESP = new BooleanSetting("Block place ESP", false);
/*  178 */   private final ModeSetting blockPicker = new ModeSetting("Block picker", "Switch", new String[] { "None", "Switch", "Spoof" });
/*      */   
/*  180 */   private final BooleanSetting swingAnimation = new BooleanSetting("Swing animation", false);
/*      */   
/*  182 */   private final ModeSetting tower = new ModeSetting("Tower", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump"))), "Legit", new String[] { "None", "Vanilla", "NCP", "NCP2", "Hypixel", "Hypixel2", "Legit", "Custom" });
/*      */   
/*  184 */   private final BooleanSetting showTeleportSettings = new BooleanSetting("Show teleport settings", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom"))), false);
/*  185 */   private final BooleanSetting showMotionYSettings = new BooleanSetting("Show motionY settings", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom"))), false);
/*      */   
/*  187 */   private final CustomDoubleSetting jumpMotionY = new CustomDoubleSetting("Jump motion Y", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), 0.42D);
/*      */   
/*  189 */   private final ModeSetting teleportTick1 = new ModeSetting("Teleport tick 1", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  190 */   private final ModeSetting yMotionTick1 = new ModeSetting("Y motion tick 1", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  191 */   private final CustomDoubleSetting yMotionValue1 = new CustomDoubleSetting("Y motion value tick 1", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick1.is("Set motionY") || this.yMotionTick1.is("Add motionY")))), 0.0D);
/*      */   
/*  193 */   private final ModeSetting teleportTick2 = new ModeSetting("Teleport tick 2", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  194 */   private final ModeSetting yMotionTick2 = new ModeSetting("Y motion tick 2", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  195 */   private final CustomDoubleSetting yMotionValue2 = new CustomDoubleSetting("Y motion value tick 2", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick2.is("Set motionY") || this.yMotionTick2.is("Add motionY")))), 0.0D);
/*      */   
/*  197 */   private final ModeSetting teleportTick3 = new ModeSetting("Teleport tick 3", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  198 */   private final ModeSetting yMotionTick3 = new ModeSetting("Y motion tick 3", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  199 */   private final CustomDoubleSetting yMotionValue3 = new CustomDoubleSetting("Y motion value tick 3", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick3.is("Set motionY") || this.yMotionTick3.is("Add motionY")))), 0.0D);
/*      */   
/*  201 */   private final ModeSetting teleportTick4 = new ModeSetting("Teleport tick 4", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  202 */   private final ModeSetting yMotionTick4 = new ModeSetting("Y motion tick 4", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  203 */   private final CustomDoubleSetting yMotionValue4 = new CustomDoubleSetting("Y motion value tick 4", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick4.is("Set motionY") || this.yMotionTick4.is("Add motionY")))), 0.0D);
/*      */   
/*  205 */   private final ModeSetting teleportTick5 = new ModeSetting("Teleport tick 5", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  206 */   private final ModeSetting yMotionTick5 = new ModeSetting("Y motion tick 5", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  207 */   private final CustomDoubleSetting yMotionValue5 = new CustomDoubleSetting("Y motion value tick 5", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick5.is("Set motionY") || this.yMotionTick5.is("Add motionY")))), 0.0D);
/*      */   
/*  209 */   private final ModeSetting teleportTick6 = new ModeSetting("Teleport tick 6", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  210 */   private final ModeSetting yMotionTick6 = new ModeSetting("Y motion tick 6", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  211 */   private final CustomDoubleSetting yMotionValue6 = new CustomDoubleSetting("Y motion value tick 6", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel2") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick6.is("Set motionY") || this.yMotionTick6.is("Add motionY")))), 0.0D);
/*      */   
/*  213 */   private final ModeSetting teleportTick7 = new ModeSetting("Teleport tick 7", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  214 */   private final ModeSetting yMotionTick7 = new ModeSetting("Y motion tick 7", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && !this.mode.is("Hypixel2") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  215 */   private final CustomDoubleSetting yMotionValue7 = new CustomDoubleSetting("Y motion value tick 7", () -> Boolean.valueOf((!this.mode.is("Hypixel2") && !this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick7.is("Set motionY") || this.yMotionTick7.is("Add motionY")))), 0.0D);
/*      */   
/*  217 */   private final ModeSetting teleportTick8 = new ModeSetting("Teleport tick 8", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showTeleportSettings.isEnabled())), "None", new String[] { "Set pos to rounded Y", "Teleport to block over", "None" });
/*  218 */   private final ModeSetting yMotionTick8 = new ModeSetting("Y motion tick 8", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), "None", new String[] { "Set motionY", "Add motionY", "Jump again", "None" });
/*  219 */   private final CustomDoubleSetting yMotionValue8 = new CustomDoubleSetting("Y motion value tick 8", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled() && (this.yMotionTick8.is("Set motionY") || this.yMotionTick8.is("Add motionY")))), 0.0D);
/*      */   
/*  221 */   private final BooleanSetting yChangeAffectsNextMotion = new BooleanSetting("Y change affects next motion", () -> Boolean.valueOf((!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump") && this.tower.is("Custom") && this.showMotionYSettings.isEnabled())), true);
/*      */   
/*  223 */   private final ModeSetting towerTiming = new ModeSetting("Tower timing", () -> Boolean.valueOf(this.tower.is("Custom")), "Always", new String[] { "Always", "Only when moving", "Only when not moving" });
/*  224 */   private final DoubleSetting minMotionForMovement = new DoubleSetting("Min motion to define moving", () -> Boolean.valueOf((this.tower.is("Custom") && (this.towerTiming.is("Only when moving") || this.towerTiming.is("Only when not moving")))), 0.1D, 0.0D, 0.2D, 0.01D);
/*      */   
/*      */   private double placeY;
/*      */   
/*      */   private BlockInfo info;
/*      */   
/*      */   private Vec3 rotationVec3;
/*      */   
/*      */   private FixedRotations rotations;
/*      */   
/*      */   private float lastTickYaw;
/*      */   
/*      */   private float lastTickPitch;
/*      */   
/*      */   private MovingObjectPosition requestedCursor;
/*      */   
/*      */   private boolean raytraceSuccess;
/*      */   
/*      */   private boolean startedRotating;
/*      */   
/*      */   private float facingYaw;
/*      */   
/*      */   private float facingPitch;
/*      */   
/*      */   private boolean overAir;
/*      */   
/*      */   private boolean placing;
/*      */   
/*      */   private boolean hasPlacedYet;
/*      */   
/*      */   private int sneakCounter;
/*      */   
/*      */   private int sneakPlacementCounter;
/*      */   private int offGroundTicks;
/*      */   private double yawSpeed;
/*      */   private boolean shouldResetYawAccel;
/*      */   private double pitchSpeed;
/*      */   private boolean shouldResetPitchAccel;
/*      */   private boolean yawDone;
/*      */   private boolean pitchDone;
/*      */   private int placeDelay;
/*      */   private int nextPlaceDelay;
/*      */   private int rightClickCounter;
/*      */   private float requestedRotationYaw;
/*      */   private float requestedRotationPitch;
/*      */   private int noMoveOnStartCounter;
/*      */   private int groundSpoofCounter;
/*      */   private float startingYaw;
/*      */   private boolean towering;
/*      */   private int towerTicks;
/*      */   private boolean startedSprint;
/*      */   private int sprintTicks;
/*      */   private boolean wasHovering;
/*      */   private int ticksHovering;
/*      */   private boolean wasTowering;
/*      */   private int toweringTicks;
/*      */   private boolean pendingMovementStop;
/*      */   private boolean jumpTick;
/*      */   private boolean changedKeybinds;
/*      */   private int oldSlot;
/*      */   private boolean changedSlot;
/*      */   private boolean started;
/*      */   private int counter;
/*      */   private int ticks;
/*      */   private boolean diagonally;
/*      */   private boolean lastDiagonal;
/*      */   private int blocksPlaced;
/*      */   private final Random random;
/*      */   private int randomNumber;
/*      */   private int randomNumber2;
/*      */   private double spoofedX;
/*      */   private double spoofedY;
/*      */   private double spoofedZ;
/*      */   private float renderedYaw;
/*      */   private float renderedPitch;
/*      */   
/*      */   public float getRenderedYaw() {
/*  301 */     return this.renderedYaw; } public float getRenderedPitch() { return this.renderedPitch; }
/*      */ 
/*      */   
/*      */   public Scaffold() {
/*  305 */     super("Scaffold", Category.PLAYER);
/*      */     
/*  307 */     this.random = new Random();
/*      */     
/*  309 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.mode, (AbstractSetting)this.jumpSprintMode, (AbstractSetting)this.basicRotations, (AbstractSetting)this.noSprint, (AbstractSetting)this.jumpMode, (AbstractSetting)this.showRotationsSettings, (AbstractSetting)this.bCustomRotationsTiming, (AbstractSetting)this.bCustomRotationsMode, (AbstractSetting)this.bCustomYawOffset, (AbstractSetting)this.bCustomPitchValue, (AbstractSetting)this.instantRotations, (AbstractSetting)this.yawSpeedMode, (AbstractSetting)this.minYawSpeed, (AbstractSetting)this.maxYawSpeed, (AbstractSetting)this.minYawAccel, (AbstractSetting)this.maxYawAccel, (AbstractSetting)this.reduceYawSpeedWhenAlmostDone, (AbstractSetting)this.minYawSpeedWhenAlmostDone, (AbstractSetting)this.maxYawSpeedWhenAlmostDone, (AbstractSetting)this.pitchSpeedMode, (AbstractSetting)this.minPitchSpeed, (AbstractSetting)this.maxPitchSpeed, (AbstractSetting)this.minPitchAccel, (AbstractSetting)this.maxPitchAccel, (AbstractSetting)this.reducePitchSpeedWhenAlmostDone, (AbstractSetting)this.minPitchSpeedWhenAlmostDone, (AbstractSetting)this.maxPitchSpeedWhenAlmostDone, (AbstractSetting)this.minYawChange, (AbstractSetting)this.minPitchChange, (AbstractSetting)this.rotsRandomisation, (AbstractSetting)this.bCustomResetRotsIfNotRotating, (AbstractSetting)this.modulo45Rots, (AbstractSetting)this.showMovementSettings, (AbstractSetting)this.customNoSprintTiming, (AbstractSetting)this.customNoSprintMode, (AbstractSetting)this.customAllowJumpBoost, (AbstractSetting)this.customSneakTiming, (AbstractSetting)this.customSneakFrequency, (AbstractSetting)this.customSneakOffground, (AbstractSetting)this.customSneakMode, (AbstractSetting)this.customGroundspoof, (AbstractSetting)this.customJumpMode, (AbstractSetting)this.movementType, (AbstractSetting)this.customMotionMultOnGround, (AbstractSetting)this.customMotionMultOffGround, (AbstractSetting)this.customMultAffectsNextMotion, (AbstractSetting)this.customJumpBoostAmount, (AbstractSetting)this.customOnGroundSpeed, (AbstractSetting)this.customOffGroundStrafe, (AbstractSetting)this.customOffGroundSpeed, (AbstractSetting)this.customOnGroundPotionExtra, (AbstractSetting)this.customOffGroundPotionExtra, (AbstractSetting)this.customIgnoreSpeedPot, (AbstractSetting)this.customNoMoveTicksOnStart, (AbstractSetting)this.showPlacementSettings, (AbstractSetting)this.bCustomOnlyPlaceIfRaytraceSuccess, (AbstractSetting)this.range, (AbstractSetting)this.distFromBlock, (AbstractSetting)this.offGroundDistFromBlock, (AbstractSetting)this.minPlaceDelay, (AbstractSetting)this.maxPlaceDelay, (AbstractSetting)this.applyPlaceDelayOffground, (AbstractSetting)this.extraRightClicks, (AbstractSetting)this.expand, (AbstractSetting)this.jump, (AbstractSetting)this.hypixelSprint, (AbstractSetting)this.jumpToAvoidSetback, (AbstractSetting)this.hypixelTower, (AbstractSetting)this.towerSpeed, (AbstractSetting)this.towerSpeedWhenDiagonal, (AbstractSetting)this.stillPlaceOnRaytraceFail, (AbstractSetting)this.debugOnRaytraceFail, (AbstractSetting)this.rotationsEnabled, (AbstractSetting)this.moveFix, (AbstractSetting)this.noPlaceOnJumpTick, (AbstractSetting)this.blockPlaceESP, (AbstractSetting)this.blockPicker, (AbstractSetting)this.swingAnimation, (AbstractSetting)this.tower, (AbstractSetting)this.showTeleportSettings, (AbstractSetting)this.showMotionYSettings, (AbstractSetting)this.jumpMotionY, (AbstractSetting)this.teleportTick1, (AbstractSetting)this.yMotionTick1, (AbstractSetting)this.yMotionValue1, (AbstractSetting)this.teleportTick2, (AbstractSetting)this.yMotionTick2, (AbstractSetting)this.yMotionValue2, (AbstractSetting)this.teleportTick3, (AbstractSetting)this.yMotionTick3, (AbstractSetting)this.yMotionValue3, (AbstractSetting)this.teleportTick4, (AbstractSetting)this.yMotionTick4, (AbstractSetting)this.yMotionValue4, (AbstractSetting)this.teleportTick5, (AbstractSetting)this.yMotionTick5, (AbstractSetting)this.yMotionValue5, (AbstractSetting)this.teleportTick6, (AbstractSetting)this.yMotionTick6, (AbstractSetting)this.yMotionValue6, (AbstractSetting)this.teleportTick7, (AbstractSetting)this.yMotionTick7, (AbstractSetting)this.yMotionValue7, (AbstractSetting)this.teleportTick8, (AbstractSetting)this.yMotionTick8, (AbstractSetting)this.yMotionValue8, (AbstractSetting)this.yChangeAffectsNextMotion, (AbstractSetting)this.towerTiming, (AbstractSetting)this.minMotionForMovement });
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  317 */     this.showRotationsSettings.setShownInColor(true);
/*  318 */     this.showMovementSettings.setShownInColor(true);
/*  319 */     this.showPlacementSettings.setShownInColor(true);
/*      */   }
/*      */ 
/*      */   
/*      */   public void onEnable() {
/*  324 */     this.placeY = mc.thePlayer.posY;
/*      */     
/*  326 */     this.info = null;
/*  327 */     this.rotationVec3 = null;
/*      */     
/*  329 */     this.startedRotating = false;
/*      */     
/*  331 */     this.facingYaw = mc.thePlayer.rotationYaw - 180.0F;
/*  332 */     this.facingPitch = (float)this.bCustomPitchValue.getValue();
/*      */     
/*  334 */     this.startingYaw = mc.thePlayer.rotationYaw;
/*      */     
/*  336 */     this.rotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
/*      */     
/*  338 */     this.overAir = false;
/*  339 */     this.placing = false;
/*  340 */     this.hasPlacedYet = false;
/*      */     
/*  342 */     this.sneakCounter = 0;
/*  343 */     this.sneakPlacementCounter = 0;
/*      */     
/*  345 */     this.yawSpeed = this.minYawSpeed.getValue();
/*  346 */     this.pitchSpeed = this.minPitchSpeed.getValue();
/*      */     
/*  348 */     this.yawDone = false;
/*  349 */     this.pitchDone = false;
/*      */     
/*  351 */     this.shouldResetYawAccel = true;
/*  352 */     this.shouldResetPitchAccel = true;
/*      */     
/*  354 */     this.placeDelay = 0;
/*  355 */     this.nextPlaceDelay = getNextPlaceDelay();
/*      */     
/*  357 */     this.requestedCursor = null;
/*      */     
/*  359 */     this.rightClickCounter = 0;
/*  360 */     this.noMoveOnStartCounter = 0;
/*  361 */     this.groundSpoofCounter = 0;
/*      */     
/*  363 */     this.towering = false;
/*  364 */     this.towerTicks = 0;
/*      */     
/*  366 */     this.startedSprint = false;
/*  367 */     this.sprintTicks = 0;
/*      */     
/*  369 */     this.wasHovering = false;
/*  370 */     this.ticksHovering = 0;
/*      */     
/*  372 */     this.wasTowering = false;
/*  373 */     this.toweringTicks = 0;
/*      */     
/*  375 */     this.pendingMovementStop = false;
/*      */     
/*  377 */     this.jumpTick = false;
/*      */     
/*  379 */     this.changedKeybinds = false;
/*      */     
/*  381 */     this.started = false;
/*      */     
/*  383 */     this.counter = 0;
/*  384 */     this.ticks = 0;
/*      */     
/*  386 */     this.diagonally = false;
/*      */     
/*  388 */     this.blocksPlaced = 0;
/*      */     
/*  390 */     this.oldSlot = mc.thePlayer.inventory.currentItem;
/*  391 */     this.changedSlot = false;
/*      */     
/*  393 */     if (this.blockPicker.is("Spoof")) {
/*  394 */       Acrimony.instance.getSlotSpoofHandler().startSpoofing(mc.thePlayer.inventory.currentItem);
/*      */     }
/*      */     
/*  397 */     float yaw1 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + 720.0F;
/*      */     
/*  399 */     switch (this.mode.getMode()) {
/*      */       case "Hypixel":
/*  401 */         if (this.hypixelSprint.is("Semi") && (
/*  402 */           !this.jumpToAvoidSetback.isEnabled() || !mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed || Acrimony.instance.getAcrimonyClientUtil().getGroundTicks() < 10)) {
/*  403 */           this.startedSprint = true;
/*      */         }
/*      */         break;
/*      */       
/*      */       case "Sneak":
/*  408 */         if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/*  409 */           this.diagonally = false;
/*  410 */           this.requestedRotationYaw = yaw1 + 45.0F - (yaw1 + 45.0F) % 90.0F - 135.0F;
/*  411 */           this.requestedRotationPitch = 79.3F;
/*      */           
/*  413 */           this.blocksPlaced = 1;
/*      */         } else {
/*  415 */           this.diagonally = true;
/*      */           
/*  417 */           this.requestedRotationYaw = yaw1 - yaw1 % 90.0F + 45.0F - 180.0F;
/*  418 */           this.requestedRotationPitch = 79.7F;
/*      */         } 
/*      */         
/*  421 */         this.randomNumber = this.random.nextInt(2);
/*  422 */         this.randomNumber2 = this.random.nextInt(3);
/*      */         
/*  424 */         this.sneakCounter = 1000;
/*      */         break;
/*      */       case "Godbridge":
/*  427 */         if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/*  428 */           this.diagonally = false;
/*  429 */           this.requestedRotationYaw = yaw1 + 45.0F - (yaw1 + 45.0F) % 90.0F - 135.0F;
/*  430 */           this.requestedRotationPitch = 76.0F;
/*      */           
/*  432 */           this.blocksPlaced = 1;
/*      */         } else {
/*  434 */           this.diagonally = true;
/*      */           
/*  436 */           this.requestedRotationYaw = yaw1 - yaw1 % 90.0F + 45.0F - 180.0F;
/*  437 */           this.requestedRotationPitch = 77.0F;
/*      */         } 
/*      */         
/*  440 */         this.randomNumber = this.random.nextInt(2);
/*  441 */         this.randomNumber2 = this.random.nextInt(3);
/*      */         break;
/*      */       case "Basic custom":
/*  444 */         if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/*  445 */           this.blocksPlaced = 1;
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  450 */     this.renderedYaw = MovementUtil.getPlayerDirection() - 180.0F;
/*  451 */     this.renderedPitch = 82.0F;
/*      */   }
/*      */ 
/*      */   
/*      */   public void onDisable() {
/*  456 */     mc.gameSettings.keyBindSneak.pressed = KeyboardUtil.isPressed(mc.gameSettings.keyBindSneak);
/*  457 */     mc.gameSettings.keyBindJump.pressed = KeyboardUtil.isPressed(mc.gameSettings.keyBindJump);
/*      */     
/*  459 */     mc.gameSettings.keyBindUseItem.pressed = false;
/*      */     
/*  461 */     if (this.changedKeybinds) {
/*  462 */       KeyboardUtil.resetKeybindings(new KeyBinding[] { mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight });
/*      */       
/*  464 */       this.changedKeybinds = false;
/*      */     } 
/*      */     
/*  467 */     switchToOriginalSlot();
/*      */   }
/*      */   @Listener
/*      */   public void onEvent(Event event) {
/*  471 */     if (this.jumpSprintMode.is("Rise/Opal"));
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onTick(TickEvent event) {
/*  476 */     if (mc.thePlayer.ticksExisted < 10) {
/*  477 */       setEnabled(false);
/*      */       
/*      */       return;
/*      */     } 
/*  481 */     this.offGroundTicks++;
/*  482 */     if (mc.thePlayer.onGround) {
/*  483 */       this.offGroundTicks = 0;
/*      */     }
/*      */     
/*  486 */     switch (this.mode.getMode()) {
/*      */       case "Basic":
/*  488 */         basicScaffold();
/*      */         break;
/*      */       case "Verus":
/*  491 */         verusScaffold();
/*      */         break;
/*      */       case "Basic custom":
/*  494 */         basicCustomScaffold();
/*      */         break;
/*      */       case "Hypixel":
/*  497 */         hypixelScaffold();
/*      */         break;
/*      */       case "Hypixel jump":
/*  500 */         hypixelJumpScaffold();
/*      */         break;
/*      */       case "Hypixel2":
/*  503 */         hypixel2();
/*      */         break;
/*      */       case "Andromeda":
/*  506 */         andromedaScaffold();
/*      */         break;
/*      */       case "Godbridge":
/*  509 */         godbridgeScaffold();
/*      */         break;
/*      */       case "Sneak":
/*  512 */         sneakScaffold();
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/*  517 */     pickBlock();
/*      */   }
/*      */   
/*      */   private void pickBlock() {
/*  521 */     switch (this.blockPicker.getMode()) {
/*      */       case "Switch":
/*      */       case "Spoof":
/*  524 */         mc.thePlayer.inventory.currentItem = getBlockSlot();
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void switchToOriginalSlot() {
/*  530 */     switch (this.blockPicker.getMode()) {
/*      */       case "Spoof":
/*  532 */         Acrimony.instance.getSlotSpoofHandler().stopSpoofing();
/*      */       case "Switch":
/*  534 */         mc.thePlayer.inventory.currentItem = this.oldSlot;
/*  535 */         mc.playerController.syncCurrentPlayItem();
/*  536 */         this.changedSlot = false;
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onUpdate(UpdateEvent event) {
/*  543 */     switch (this.mode.getMode()) {
/*      */       case "Andromeda":
/*  545 */         if (!this.jumpTick) {
/*  546 */           mc.thePlayer.setSprinting(false);
/*      */         }
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void basicScaffold() {
/*  553 */     if (this.noSprint.is("Enabled")) {
/*  554 */       mc.thePlayer.setSprinting(false);
/*  555 */       mc.gameSettings.keyBindSprint.pressed = false;
/*      */     } 
/*      */     
/*  558 */     switch (this.jumpMode.getMode()) {
/*      */       case "None":
/*  560 */         this.placeY = mc.thePlayer.posY;
/*      */         break;
/*      */       case "Normal":
/*      */       case "Place when falling":
/*  564 */         if (mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed) {
/*  565 */           this.placeY = mc.thePlayer.posY;
/*      */         }
/*      */         
/*  568 */         if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed && MovementUtil.isMoving()) {
/*  569 */           mc.thePlayer.jump();
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/*  574 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     
/*  576 */     if (this.expand.getValue() > 0 && MovementUtil.isMoving() && !mc.gameSettings.keyBindJump.pressed) {
/*  577 */       double x = mc.thePlayer.posX;
/*  578 */       double z = mc.thePlayer.posZ;
/*      */       
/*  580 */       for (int i = 0; i <= this.expand.getValue(); i++) {
/*  581 */         if (i > 0) {
/*  582 */           float direction = (float)Math.toRadians(MovementUtil.getPlayerDirection());
/*      */           
/*  584 */           x -= Math.sin(direction);
/*  585 */           z += Math.cos(direction);
/*      */         } 
/*      */         
/*  588 */         pos = new BlockPos(x, this.placeY - 1.0D, z);
/*      */         
/*  590 */         if (WorldUtil.isAirOrLiquid(pos)) {
/*      */           break;
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  596 */     this.info = WorldUtil.getBlockInfo(pos, 4);
/*  597 */     this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */     
/*  599 */     this.rotationVec3 = null;
/*      */     
/*  601 */     boolean allowPlacing = this.jumpMode.is("Place when falling") ? ((mc.thePlayer.onGround || mc.thePlayer.motionY < 0.0D)) : true;
/*      */     
/*  603 */     Vec3 placeVec3 = null;
/*      */     
/*  605 */     if (this.overAir && this.info != null && allowPlacing) {
/*  606 */       mc.rightClickDelayTimer = 0;
/*  607 */       mc.gameSettings.keyBindUseItem.pressed = true;
/*      */       
/*  609 */       Vec3 vec3 = placeVec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */       
/*  611 */       placeBlock(vec3);
/*      */       
/*  613 */       this.placing = true;
/*      */     } else {
/*  615 */       this.placing = false;
/*      */     } 
/*      */     
/*  618 */     switch (this.basicRotations.getMode()) {
/*      */       case "Movement based":
/*  620 */         if (!this.startedRotating) {
/*  621 */           this.rotations.updateRotations(MovementUtil.getPlayerDirection() - 180.0F, 82.0F);
/*  622 */           this.startedRotating = true;
/*  623 */         } else if (MovementUtil.isMoving()) {
/*  624 */           this.rotations.updateRotations(MovementUtil.getPlayerDirection() - 180.0F, this.rotations.getPitch());
/*      */         } 
/*      */         
/*  627 */         if (this.placing && placeVec3 != null) {
/*  628 */           Vec3 hitVec = placeVec3;
/*      */           
/*  630 */           float[] rots = RotationsUtil.getRotationsToPosition(hitVec.xCoord, hitVec.yCoord, hitVec.zCoord);
/*      */           
/*  632 */           this.rotations.updateRotations(this.rotations.getYaw(), rots[1]);
/*      */         } 
/*      */         break;
/*      */       case "Block center":
/*  636 */         if (this.placing && placeVec3 != null) {
/*  637 */           Vec3 hitVec = placeVec3;
/*      */           
/*  639 */           float[] rots = RotationsUtil.getRotationsToPosition(hitVec.xCoord, hitVec.yCoord, hitVec.zCoord);
/*      */           
/*  641 */           this.rotations.updateRotations(rots[0], rots[1]);
/*      */         } 
/*      */         break;
/*      */     } 
/*      */     
/*  646 */     mc.gameSettings.keyBindUseItem.pressed = false;
/*  647 */     mc.objectMouseOver = null;
/*      */   }
/*      */   
/*      */   private void verusScaffold() {
/*  651 */     if (!this.jump.isEnabled() || mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed) {
/*  652 */       this.placeY = mc.thePlayer.posY;
/*      */     }
/*      */     
/*  655 */     if (this.jump.isEnabled() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
/*  656 */       mc.thePlayer.jump();
/*      */     }
/*      */     
/*  659 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     
/*  661 */     this.info = WorldUtil.getBlockInfo(pos, 4);
/*      */     
/*  663 */     if (WorldUtil.isAirOrLiquid(pos) && this.info != null) {
/*  664 */       placeBlock(WorldUtil.getVec3(this.info.pos, this.info.facing, true));
/*      */       
/*  666 */       this.placing = true;
/*      */     } else {
/*  668 */       this.placing = false;
/*      */     } 
/*      */     
/*  671 */     mc.gameSettings.keyBindUseItem.pressed = false;
/*  672 */     mc.objectMouseOver = null;
/*      */   }
/*      */   
/*      */   private void hypixelScaffold() {
/*  676 */     boolean allowSprint = false;
/*      */     
/*  678 */     switch (this.hypixelSprint.getMode()) {
/*      */       case "None":
/*  680 */         allowSprint = (mc.gameSettings.keyBindJump.pressed && hypixelTowerAllowSprint());
/*      */         break;
/*      */       case "Semi":
/*      */       case "Full":
/*  684 */         allowSprint = (!mc.gameSettings.keyBindJump.pressed || hypixelTowerAllowSprint());
/*      */         break;
/*      */     } 
/*      */     
/*  688 */     if (!allowSprint) {
/*  689 */       mc.thePlayer.setSprinting(false);
/*  690 */       mc.gameSettings.keyBindSprint.pressed = false;
/*      */     } 
/*      */     
/*  693 */     if (!this.hypixelSprint.is("Semi") || this.startedSprint) {
/*  694 */       this.placeY = mc.thePlayer.posY;
/*      */     }
/*      */ 
/*      */     
/*  698 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     
/*  700 */     this.info = WorldUtil.getBlockInfo(pos, 3);
/*  701 */     this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */     
/*  703 */     this.rotationVec3 = null;
/*      */     
/*  705 */     this.ticks++;
/*      */     
/*  707 */     float yaw = MovementUtil.getPlayerDirection() - 180.0F;
/*      */     
/*  709 */     float pitch = (float)((this.info != null && this.overAir) ? getPitch(yaw) : ((!this.hypixelSprint.is("None") && mc.thePlayer.onGround) ? ((this.ticks < 2) ? (58.0D - Math.random()) : (80.0D + Math.random())) : this.rotations.getPitch()));
/*      */     
/*  711 */     this.renderedYaw = yaw;
/*      */     
/*  713 */     if (this.info != null && this.overAir) {
/*  714 */       this.renderedPitch = pitch;
/*      */     }
/*      */     
/*  717 */     this.rotations.updateRotations(yaw, pitch);
/*      */     
/*  719 */     if (this.overAir && this.info != null) {
/*  720 */       Vec3 vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */       
/*  722 */       if (this.rotationVec3 != null) {
/*  723 */         vec3 = this.rotationVec3;
/*      */       } else {
/*  725 */         MovingObjectPosition raytrace = WorldUtil.raytrace(this.rotations.getYaw(), this.rotations.getPitch());
/*      */         
/*  727 */         if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos)) {
/*  728 */           boolean sameFace = (raytrace.sideHit == this.info.facing);
/*  729 */           boolean horizontalFace = (raytrace.sideHit != EnumFacing.UP && raytrace.sideHit != EnumFacing.DOWN);
/*      */           
/*  731 */           if (sameFace || (horizontalFace && !this.towering)) {
/*  732 */             if (!sameFace) {
/*  733 */               BlockPos oldPos = this.info.pos;
/*  734 */               this.info = new BlockInfo(oldPos, raytrace.sideHit);
/*      */             } 
/*      */             
/*  737 */             vec3 = raytrace.hitVec;
/*      */           } 
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/*  744 */       if (placeBlock(vec3)) {
/*  745 */         this.ticks = 0;
/*      */       }
/*      */       
/*  748 */       this.placing = true;
/*      */     } else {
/*  750 */       if (mc.thePlayer.ticksExisted % 2 == 0) {
/*  751 */         PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
/*      */       }
/*      */       
/*  754 */       this.placing = false;
/*      */     } 
/*      */     
/*  757 */     mc.gameSettings.keyBindUseItem.pressed = false;
/*  758 */     mc.objectMouseOver = null;
/*      */   }
/*      */   
/*      */   private Vec3 getNextTickPos() {
/*  762 */     float f5, f4 = 0.91F;
/*      */     
/*  764 */     if (mc.thePlayer.onGround)
/*      */     {
/*  766 */       f4 = (mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double((mc.thePlayer.getEntityBoundingBox()).minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock()).slipperiness * 0.91F;
/*      */     }
/*      */     
/*  769 */     float f = 0.16277136F / f4 * f4 * f4;
/*      */ 
/*      */     
/*  772 */     if (mc.thePlayer.onGround) {
/*      */       
/*  774 */       f5 = mc.thePlayer.getAIMoveSpeed() * f;
/*      */     }
/*      */     else {
/*      */       
/*  778 */       f5 = mc.thePlayer.jumpMovementFactor;
/*      */     } 
/*      */     
/*  781 */     double[] xzMotion = moveFlying(mc.thePlayer.moveStrafing, mc.thePlayer.moveForward, f5, mc.thePlayer.rotationYaw);
/*      */     
/*  783 */     double posX = mc.thePlayer.posX + xzMotion[0];
/*  784 */     double posY = mc.thePlayer.posY;
/*  785 */     double posZ = mc.thePlayer.posZ + xzMotion[1];
/*      */     
/*  787 */     return new Vec3(posX, posY - 1.0D, posZ);
/*      */   }
/*      */ 
/*      */   
/*      */   private double[] moveFlying(float strafe, float forward, float friction, float yaw) {
/*  792 */     float f = strafe * strafe + forward * forward;
/*      */     
/*  794 */     double motionX = mc.thePlayer.motionX;
/*  795 */     double motionZ = mc.thePlayer.motionZ;
/*      */     
/*  797 */     if (f >= 1.0E-4F) {
/*      */       
/*  799 */       f = MathHelper.sqrt_float(f);
/*      */       
/*  801 */       if (f < 1.0F)
/*      */       {
/*  803 */         f = 1.0F;
/*      */       }
/*      */       
/*  806 */       f = friction / f;
/*  807 */       strafe *= f;
/*  808 */       forward *= f;
/*  809 */       float f1 = MathHelper.sin(yaw * 3.1415927F / 180.0F);
/*  810 */       float f2 = MathHelper.cos(yaw * 3.1415927F / 180.0F);
/*  811 */       motionX += (strafe * f2 - forward * f1);
/*  812 */       motionZ += (forward * f2 + strafe * f1);
/*      */     } 
/*      */     
/*  815 */     return new double[] { motionX, motionZ };
/*      */   }
/*      */   
/*      */   private void hypixel2() {
/*  819 */     if (mc.thePlayer.onGround) {
/*  820 */       mc.thePlayer.setSprinting(false);
/*      */     } else {
/*  822 */       mc.thePlayer.setSprinting(true);
/*      */     } 
/*      */     
/*  825 */     if (!mc.gameSettings.keyBindJump.pressed && mc.thePlayer.onGround && this.ticks == 0) {
/*  826 */       mc.thePlayer.jump();
/*  827 */       this.ticks = 11;
/*  828 */     } else if (this.ticks > 0) {
/*  829 */       this.ticks--;
/*      */     } 
/*      */     
/*  832 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*  833 */     if (mc.thePlayer.posY - this.placeY > 1.25D && mc.thePlayer.onGround) {
/*  834 */       this.placeY = mc.thePlayer.posY;
/*      */     }
/*      */     
/*  837 */     if (!mc.gameSettings.keyBindJump.isKeyDown()) {
/*  838 */       pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     }
/*  840 */     if (mc.gameSettings.keyBindJump.isKeyDown()) {
/*  841 */       pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1D, mc.thePlayer.posZ);
/*      */     }
/*      */     
/*  844 */     if (WorldUtil.isAirOrLiquid(pos) && this.info != null) {
/*  845 */       Vec3 vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */ 
/*      */       
/*  848 */       float[] rots = RotationsUtil.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
/*      */       
/*  850 */       this.rotations.updateRotations(rots[0], rots[1]);
/*  851 */       placeBlock(vec3);
/*  852 */       this.placing = true;
/*      */     } else {
/*  854 */       this.placing = false;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void hypixelJumpScaffold() {
/*  859 */     if (this.jumpSprintMode.is("Novoline")) {
/*  860 */       if (mc.thePlayer.onGround || KeyboardUtil.isPressed(mc.gameSettings.keyBindJump)) {
/*  861 */         this.placeY = mc.thePlayer.posY;
/*      */         
/*  863 */         mc.gameSettings.keyBindJump.pressed = true;
/*  864 */       } else if (!KeyboardUtil.isPressed(mc.gameSettings.keyBindJump)) {
/*  865 */         mc.gameSettings.keyBindJump.pressed = false;
/*      */       } 
/*      */       
/*  868 */       BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */       
/*  870 */       this.info = WorldUtil.getBlockInfo(pos, 3);
/*  871 */       this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */ 
/*      */       
/*  874 */       if (!this.overAir) {
/*  875 */         pos = new BlockPos(mc.thePlayer.posX - mc.thePlayer.motionX * 4.0D, this.placeY, mc.thePlayer.posZ - mc.thePlayer.motionZ * 4.0D);
/*      */         
/*  877 */         this.info = WorldUtil.getBlockInfo(pos, 3);
/*  878 */         this.overAir = WorldUtil.isAirOrLiquid(pos);
/*  879 */         this.placing = true;
/*      */       } 
/*      */       
/*  882 */       if (WorldUtil.isAirOrLiquid(pos) && this.info != null) {
/*  883 */         Vec3 vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */         
/*  885 */         float[] rots = RotationsUtil.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
/*      */         
/*  887 */         this.rotations.updateRotations(rots[0], rots[1]);
/*  888 */         placeBlock(vec3);
/*  889 */         this.placing = true;
/*      */       } else {
/*  891 */         this.placing = false;
/*      */       } 
/*      */       
/*  894 */       mc.gameSettings.keyBindUseItem.pressed = false;
/*  895 */       mc.objectMouseOver = null;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  901 */     else if (this.jumpSprintMode.is("Rise/Opal")) {
/*  902 */       if (mc.thePlayer.onGround || KeyboardUtil.isPressed(mc.gameSettings.keyBindJump)) {
/*  903 */         this.placeY = mc.thePlayer.posY;
/*  904 */         mc.gameSettings.keyBindJump.pressed = true;
/*  905 */       } else if (!KeyboardUtil.isPressed(mc.gameSettings.keyBindJump)) {
/*  906 */         mc.gameSettings.keyBindJump.pressed = false;
/*      */       } 
/*      */ 
/*      */       
/*  910 */       BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */       
/*  912 */       this.info = WorldUtil.getBlockInfo(pos, 3);
/*  913 */       this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */       
/*  915 */       if (!this.jumpTick) {
/*  916 */         mc.thePlayer.motionX *= mc.thePlayer.motionZ = 0.0D;
/*      */       }
/*      */       
/*  919 */       if (this.sprintTicks >= 2) {
/*  920 */         this.jumpTick = true;
/*      */       }
/*      */ 
/*      */       
/*  924 */       if (this.jumpTick) {
/*  925 */         if (this.offGroundTicks == 5) {
/*  926 */           pos = new BlockPos(mc.thePlayer.posX, this.placeY, mc.thePlayer.posZ);
/*      */         }
/*      */ 
/*      */         
/*  930 */         if (this.offGroundTicks == 8) {
/*  931 */           MovementUtil.incrementMoveDirection(0.2F, 0.0F);
/*      */         } else {
/*  933 */           MovementUtil.incrementMoveDirection(1.2F, 0.0F);
/*      */         } 
/*      */         
/*  936 */         this.info = WorldUtil.getBlockInfo(pos, 3);
/*  937 */         this.overAir = WorldUtil.isAirOrLiquid(pos);
/*  938 */         this.placing = true;
/*      */       } 
/*      */       
/*  941 */       if (this.info != null) {
/*  942 */         Vec3 vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */         
/*  944 */         float[] rots = RotationsUtil.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
/*      */         
/*  946 */         this.rotations.updateRotations(rots[0], rots[1]);
/*  947 */         placeBlock(vec3);
/*  948 */         this.placing = true;
/*      */       } else {
/*  950 */         this.placing = false;
/*      */       } 
/*      */       
/*  953 */       mc.gameSettings.keyBindUseItem.pressed = false;
/*  954 */       mc.objectMouseOver = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private void riseOpal(Event event) {
/*  961 */     if (event instanceof UpdateEvent) {
/*  962 */       if (mc.thePlayer.onGround) {
/*  963 */         mc.thePlayer.setSprinting(false);
/*      */       } else {
/*  965 */         mc.thePlayer.setSprinting(true);
/*      */       } 
/*      */     }
/*  968 */     if (event instanceof MotionEvent) {
/*  969 */       MotionEvent e = (MotionEvent)event;
/*  970 */       if (!mc.gameSettings.keyBindJump.pressed && mc.thePlayer.onGround && this.ticks == 0) {
/*  971 */         mc.thePlayer.jump();
/*  972 */         this.ticks = 11;
/*  973 */       } else if (this.ticks > 0) {
/*  974 */         this.ticks--;
/*      */       } 
/*  976 */       BlockPos pos = null;
/*  977 */       if (mc.thePlayer.posY - this.placeY > 1.25D && mc.thePlayer.onGround) {
/*  978 */         this.placeY = mc.thePlayer.posY;
/*      */       }
/*  980 */       if (!mc.gameSettings.keyBindJump.isKeyDown()) {
/*  981 */         pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */       }
/*  983 */       if (mc.gameSettings.keyBindJump.isKeyDown()) {
/*  984 */         pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.1D, mc.thePlayer.posZ);
/*      */       }
/*  986 */       if (mc.theWorld.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir) {
/*  987 */         this.info = WorldUtil.getBlockInfo(pos, 3);
/*      */       }
/*      */       
/*  990 */       if (mc.thePlayer.motionY > -0.16D && mc.thePlayer.motionY < 0.17D && this.sneakCounter >= 4) {
/*  991 */         this.info = WorldUtil.getBlockInfo(pos, 3);
/*      */       }
/*      */       
/*  994 */       if (WorldUtil.isAirOrLiquid(pos) && this.info != null) {
/*  995 */         Vec3 vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */         
/*  997 */         float[] rots = RotationsUtil.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
/*      */         
/*  999 */         this.rotations.updateRotations(rots[0], rots[1]);
/* 1000 */         placeBlock(vec3);
/* 1001 */         this.placing = true;
/*      */       } else {
/* 1003 */         this.placing = false;
/*      */       } 
/*      */       
/* 1006 */       mc.gameSettings.keyBindUseItem.pressed = false;
/* 1007 */       mc.objectMouseOver = null;
/* 1008 */       LogUtil.addChatMessage(((MotionEvent)event).getPitch() + " " + ((MotionEvent)event).getYaw());
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean hypixelTowerAllowSprint() {
/* 1013 */     switch (this.hypixelTower.getMode()) {
/*      */       case "None":
/* 1015 */         return false;
/*      */       case "Faster vertically":
/* 1017 */         return false;
/*      */       case "Faster horizontally":
/* 1019 */         return !MovementUtil.isGoingDiagonally(0.12D);
/*      */       case "Legit":
/* 1021 */         return false;
/*      */     } 
/*      */     
/* 1024 */     return false;
/*      */   }
/*      */   private void godbridgeScaffold() {
/* 1027 */     mc.gameSettings.keyBindSprint.pressed = false;
/* 1028 */     mc.thePlayer.setSprinting(false);
/*      */     
/* 1030 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
/*      */     
/* 1032 */     this.info = WorldUtil.getBlockInfo(pos, 3);
/* 1033 */     this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */     
/* 1035 */     this.placing = false;
/*      */     
/* 1037 */     float yaw1 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + 720.0F;
/*      */     
/* 1039 */     if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/* 1040 */       this.diagonally = false;
/* 1041 */       this.requestedRotationYaw = yaw1 + 45.0F - (yaw1 + 45.0F) % 90.0F - 135.0F;
/* 1042 */       this.requestedRotationPitch = 76.0F;
/*      */     } else {
/* 1044 */       this.diagonally = true;
/*      */       
/* 1046 */       this.requestedRotationYaw = yaw1 - yaw1 % 90.0F + 45.0F - 180.0F;
/* 1047 */       this.requestedRotationPitch = 77.0F;
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1052 */     updateRotations(new float[] { this.requestedRotationYaw, this.requestedRotationPitch }, false, "Acceleration", 8.0D, 30.0D, 1.75D, 3.25D, false, 8.0D, 30.0D, "Randomised", 8.0D, 14.0D, 0.0D, 0.0D, false, 8.0D, 14.0D, 1.0D, 1.0D, 0.0D);
/*      */     
/* 1054 */     float yawDiff = Math.abs(MathHelper.wrapAngleTo180_float(this.rotations.getYaw()) - MathHelper.wrapAngleTo180_float(this.requestedRotationYaw));
/*      */     
/* 1056 */     if (yawDiff > 180.0F) {
/* 1057 */       yawDiff = 360.0F - yawDiff;
/*      */     }
/*      */     
/* 1060 */     float pitchDiff = Math.abs(this.rotations.getPitch() - this.requestedRotationPitch);
/*      */     
/* 1062 */     boolean finishedRotating = (yawDiff < 1.0F && pitchDiff < 1.0F);
/*      */     
/* 1064 */     this.sneakCounter++;
/*      */     
/* 1066 */     if (this.info != null && this.overAir) {
/* 1067 */       MovingObjectPosition raytrace = WorldUtil.raytrace(this.rotations.getYaw(), this.rotations.getPitch());
/*      */       
/* 1069 */       Vec3 vec3 = null;
/*      */       
/* 1071 */       Vec3 nextTickPos = getNextTickPos();
/*      */       
/* 1073 */       if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos) && raytrace.sideHit == this.info.facing) {
/* 1074 */         vec3 = raytrace.hitVec;
/* 1075 */       } else if (WorldUtil.negativeExpand(0.299D)) {
/* 1076 */         if (this.debugOnRaytraceFail.isEnabled()) {
/* 1077 */           LogUtil.addChatMessage("Raytrace fail : " + mc.thePlayer.ticksExisted + " | Diffs : " + (Math.round(yawDiff * 1000.0F) / 1000.0D) + " " + (Math.round(pitchDiff * 1000.0F) / 1000.0D));
/*      */         }
/*      */         
/* 1080 */         if (finishedRotating && this.stillPlaceOnRaytraceFail.isEnabled()) {
/* 1081 */           vec3 = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */         }
/*      */       } 
/*      */       
/* 1085 */       if (vec3 != null && 
/* 1086 */         placeBlock(vec3)) {
/* 1087 */         this.placing = true;
/* 1088 */         this.rightClickCounter = 0;
/* 1089 */         this.started = true;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1094 */     if (!this.placing && this.started) {
/* 1095 */       boolean rightClick = false;
/*      */       
/* 1097 */       switch (this.extraRightClicks.getMode()) {
/*      */         case "Disabled":
/* 1099 */           rightClick = false;
/*      */           break;
/*      */         case "Normal":
/* 1102 */           rightClick = (this.rightClickCounter % 2 == 0 || Math.random() < 0.2D);
/*      */           break;
/*      */         case "Dragclick":
/* 1105 */           rightClick = (this.rightClickCounter == 0 || this.rightClickCounter >= 3);
/*      */           break;
/*      */         case "Always":
/* 1108 */           rightClick = true;
/*      */           break;
/*      */       } 
/*      */       
/* 1112 */       if (rightClick) {
/* 1113 */         PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
/*      */       }
/*      */       
/* 1116 */       this.rightClickCounter++;
/*      */     } 
/*      */     
/* 1119 */     boolean pressSneak = false;
/*      */     
/* 1121 */     if (mc.thePlayer.onGround) {
/* 1122 */       pressSneak = (Acrimony.instance.getAcrimonyClientUtil().getGroundTicks() <= 2 || this.overAir || this.sneakCounter <= 1);
/*      */     } else {
/* 1124 */       pressSneak = (mc.thePlayer.motionY < 0.0D);
/*      */     } 
/*      */     
/* 1127 */     if (this.diagonally) {
/* 1128 */       this.blocksPlaced = 1;
/*      */     }
/* 1130 */     else if (this.blocksPlaced >= 9) {
/* 1131 */       mc.gameSettings.keyBindJump.pressed = true;
/* 1132 */       this.blocksPlaced = 0;
/*      */     } else {
/* 1134 */       KeyboardUtil.resetKeybinding(mc.gameSettings.keyBindJump);
/*      */     } 
/*      */ 
/*      */     
/* 1138 */     this.lastDiagonal = this.diagonally;
/*      */   }
/*      */   
/*      */   private void sneakScaffold() {
/* 1142 */     mc.gameSettings.keyBindSprint.pressed = false;
/* 1143 */     mc.thePlayer.setSprinting(false);
/*      */     
/* 1145 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
/*      */     
/* 1147 */     this.info = WorldUtil.getBlockInfo(pos, 3);
/* 1148 */     this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */     
/* 1150 */     this.placing = false;
/*      */     
/* 1152 */     float yaw1 = MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection()) + 720.0F;
/*      */     
/* 1154 */     if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/* 1155 */       this.diagonally = false;
/* 1156 */       this.requestedRotationYaw = yaw1 + 45.0F - (yaw1 + 45.0F) % 90.0F - 135.0F;
/*      */     } else {
/* 1158 */       this.diagonally = true;
/*      */       
/* 1160 */       this.requestedRotationYaw = yaw1 - yaw1 % 90.0F + 45.0F - 180.0F;
/*      */     } 
/*      */     
/* 1163 */     this.rotationVec3 = null;
/*      */     
/* 1165 */     if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.pressed) {
/* 1166 */       this.requestedRotationPitch = 79.8F;
/*      */       
/* 1168 */       MovingObjectPosition raytrace = WorldUtil.raytrace(this.rotations.getYaw(), this.requestedRotationPitch);
/*      */       
/* 1170 */       if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos)) {
/* 1171 */         boolean sameFace = (raytrace.sideHit == this.info.facing);
/* 1172 */         boolean horizontalFace = (raytrace.sideHit != EnumFacing.UP && raytrace.sideHit != EnumFacing.DOWN);
/*      */         
/* 1174 */         if (sameFace || horizontalFace) {
/* 1175 */           if (!sameFace) {
/* 1176 */             BlockPos oldPos = this.info.pos;
/* 1177 */             this.info = new BlockInfo(oldPos, raytrace.sideHit);
/*      */           } 
/*      */           
/* 1180 */           this.rotationVec3 = raytrace.hitVec;
/*      */         } 
/*      */       } 
/* 1183 */     } else if (this.info != null && this.overAir) {
/* 1184 */       this.requestedRotationPitch = getPitch(this.requestedRotationYaw);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1189 */     updateRotations(new float[] { this.requestedRotationYaw, this.requestedRotationPitch }, false, "Acceleration", 8.0D, 30.0D, 1.75D, 3.25D, false, 8.0D, 30.0D, "Randomised", 12.0D, 16.0D, 0.0D, 0.0D, false, 12.0D, 16.0D, 1.0D, 1.0D, 0.0D);
/*      */     
/* 1191 */     this.sneakCounter++;
/*      */     
/* 1193 */     if (this.info != null) {
/* 1194 */       if (this.rotationVec3 != null) {
/* 1195 */         if (placeBlock(this.rotationVec3)) {
/* 1196 */           this.placing = true;
/* 1197 */           this.sneakCounter = 0;
/* 1198 */           this.started = true;
/*      */         } 
/*      */       } else {
/* 1201 */         MovingObjectPosition raytrace = WorldUtil.raytrace(this.rotations.getYaw(), this.rotations.getPitch());
/*      */         
/* 1203 */         if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos) && raytrace.sideHit == this.info.facing && 
/* 1204 */           placeBlock(raytrace.hitVec)) {
/* 1205 */           this.placing = true;
/* 1206 */           this.sneakCounter = 0;
/* 1207 */           this.started = true;
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/* 1213 */     if (!this.placing && this.started) {
/* 1214 */       boolean rightClick = false;
/*      */       
/* 1216 */       switch (this.extraRightClicks.getMode()) {
/*      */         case "Disabled":
/* 1218 */           rightClick = false;
/*      */           break;
/*      */         case "Normal":
/* 1221 */           rightClick = (this.rightClickCounter % 2 == 0 || Math.random() < 0.2D);
/*      */           break;
/*      */         case "Dragclick":
/* 1224 */           rightClick = (this.rightClickCounter == 0 || this.rightClickCounter >= 3);
/*      */           
/* 1226 */           if (this.rightClickCounter == 4 && Math.random() > 0.2D) {
/* 1227 */             PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem())); break;
/* 1228 */           }  if (this.rightClickCounter == 0 && Math.random() > 0.3D) {
/* 1229 */             PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
/*      */           }
/*      */           break;
/*      */         case "Always":
/* 1233 */           rightClick = true;
/*      */           break;
/*      */       } 
/*      */       
/* 1237 */       if (rightClick) {
/* 1238 */         PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
/*      */       }
/*      */       
/* 1241 */       this.rightClickCounter++;
/*      */     } 
/*      */     
/* 1244 */     boolean pressSneak = false;
/*      */     
/* 1246 */     if (mc.thePlayer.onGround) {
/* 1247 */       pressSneak = (Acrimony.instance.getAcrimonyClientUtil().getGroundTicks() <= 2 || this.overAir || this.sneakCounter <= 1);
/*      */     } else {
/* 1249 */       pressSneak = (mc.thePlayer.motionY < 0.0D);
/*      */     } 
/*      */     
/* 1252 */     mc.gameSettings.keyBindSneak.pressed = pressSneak;
/*      */     
/* 1254 */     this.lastDiagonal = this.diagonally;
/*      */   }
/*      */   
/*      */   private float getPitch(float yaw) {
/* 1258 */     if (this.info == null) {
/* 1259 */       return 80.0F;
/*      */     }
/* 1261 */     FixedRotations testRotations = new FixedRotations(this.rotations.getYaw(), this.rotations.getPitch());
/*      */     
/* 1263 */     ArrayList<Float> pitchValues = new ArrayList<>();
/*      */     
/* 1265 */     pitchValues.add(Float.valueOf(this.rotations.getPitch()));
/*      */     float testPitch;
/* 1267 */     for (testPitch = 90.0F; testPitch >= 45.0F; testPitch -= (testPitch > 70.0F) ? 0.15F : 1.0F) {
/* 1268 */       pitchValues.add(Float.valueOf(testPitch));
/*      */     }
/*      */     
/* 1271 */     for (Iterator<Float> iterator = pitchValues.iterator(); iterator.hasNext(); ) { float f = ((Float)iterator.next()).floatValue();
/* 1272 */       testRotations.updateRotations(yaw, f);
/*      */       
/* 1274 */       MovingObjectPosition raytrace = WorldUtil.raytrace(testRotations.getYaw(), testRotations.getPitch());
/*      */       
/* 1276 */       if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos)) {
/* 1277 */         boolean sameFace = (raytrace.sideHit == this.info.facing);
/* 1278 */         boolean horizontalFace = (raytrace.sideHit != EnumFacing.UP && raytrace.sideHit != EnumFacing.DOWN);
/*      */         
/* 1280 */         if (sameFace || (horizontalFace && !this.towering)) {
/* 1281 */           if (!sameFace) {
/* 1282 */             BlockPos oldPos = this.info.pos;
/* 1283 */             this.info = new BlockInfo(oldPos, raytrace.sideHit);
/*      */           } 
/*      */           
/* 1286 */           this.rotationVec3 = raytrace.hitVec;
/* 1287 */           return f;
/*      */         } 
/*      */       }  }
/*      */ 
/*      */ 
/*      */     
/* 1293 */     return 80.0F;
/*      */   }
/*      */   
/*      */   private void andromedaScaffold() {
/* 1297 */     this.jumpTick = false;
/*      */     
/* 1299 */     if (mc.thePlayer.onGround) {
/* 1300 */       this.placeY = mc.thePlayer.posY;
/*      */       
/* 1302 */       if (MovementUtil.isMoving()) {
/* 1303 */         mc.thePlayer.jumpNoEvent();
/* 1304 */         MovementUtil.strafe(0.21D);
/* 1305 */         this.jumpTick = true;
/*      */       } 
/*      */     } 
/*      */     
/* 1309 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     
/* 1311 */     if (!this.jumpTick || !this.noPlaceOnJumpTick.isEnabled()) {
/* 1312 */       this.info = WorldUtil.getBlockInfo(pos, 3);
/* 1313 */       this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */       
/* 1315 */       if (!this.overAir) {
/* 1316 */         pos = new BlockPos(mc.thePlayer.posX, this.placeY + 2.0D, mc.thePlayer.posZ);
/*      */         
/* 1318 */         this.info = WorldUtil.getBlockInfo(pos, 3);
/* 1319 */         this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */       } 
/*      */     } 
/*      */     
/* 1323 */     Vec3 vec3 = null;
/*      */     
/* 1325 */     if (WorldUtil.isAirOrLiquid(pos) && this.info != null) {
/* 1326 */       vec3 = WorldUtil.getVec3(this.info.pos, this.info.facing, false);
/*      */       
/* 1328 */       placeBlock(vec3);
/*      */       
/* 1330 */       this.placing = true;
/*      */     } else {
/* 1332 */       this.placing = false;
/*      */     } 
/*      */     
/* 1335 */     mc.gameSettings.keyBindUseItem.pressed = false;
/* 1336 */     mc.objectMouseOver = null;
/*      */     
/* 1338 */     if (this.placing) {
/* 1339 */       float[] rots = RotationsUtil.getRotationsToPosition(vec3.xCoord, vec3.yCoord, vec3.zCoord);
/*      */       
/* 1341 */       this.requestedRotationYaw = rots[0];
/* 1342 */       this.requestedRotationPitch = rots[1];
/*      */     } else {
/* 1344 */       this.requestedRotationYaw = mc.thePlayer.rotationYaw;
/* 1345 */       this.requestedRotationPitch = mc.thePlayer.rotationPitch;
/*      */     } 
/*      */     
/* 1348 */     if (this.placing && this.rotationsEnabled.isEnabled() && this.moveFix.isEnabled()) {
/* 1349 */       mc.gameSettings.keyBindSprint.pressed = false;
/* 1350 */       mc.thePlayer.setSprinting(false);
/*      */     } 
/*      */     
/* 1353 */     this.rotations.updateRotations(this.requestedRotationYaw, this.requestedRotationPitch);
/*      */   }
/*      */   
/*      */   private void basicCustomScaffold() {
/* 1357 */     BlockPos pos = new BlockPos(mc.thePlayer.posX, this.placeY - 1.0D, mc.thePlayer.posZ);
/*      */     
/* 1359 */     this.info = WorldUtil.getBlockInfo(pos, this.range.getValue());
/* 1360 */     this.overAir = WorldUtil.isAirOrLiquid(pos);
/*      */ 
/*      */ 
/*      */     
/* 1364 */     boolean jumped = false;
/*      */     
/* 1366 */     float yaw1 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + 720.0F;
/*      */     
/* 1368 */     switch (this.customJumpMode.getMode()) {
/*      */       case "Godbridge":
/* 1370 */         if (this.blocksPlaced >= 9) {
/* 1371 */           mc.gameSettings.keyBindJump.pressed = true;
/* 1372 */           mc.thePlayer.jumpTicks = 0;
/* 1373 */           jumped = true;
/*      */           
/* 1375 */           this.blocksPlaced = 0;
/*      */         } 
/*      */         
/* 1378 */         if (yaw1 % 90.0F > 22.5F && yaw1 % 90.0F < 67.5F) {
/* 1379 */           this.blocksPlaced = 1;
/*      */         }
/*      */       case "None":
/* 1382 */         this.placeY = mc.thePlayer.posY;
/*      */         break;
/*      */       case "Normal":
/*      */       case "Place when falling":
/* 1386 */         if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
/* 1387 */           mc.gameSettings.keyBindJump.pressed = true;
/* 1388 */           mc.thePlayer.jumpTicks = 0;
/* 1389 */           jumped = true;
/*      */         } 
/*      */         
/* 1392 */         if (mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed) {
/* 1393 */           this.placeY = mc.thePlayer.posY;
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 1398 */     if (!jumped) {
/* 1399 */       KeyboardUtil.resetKeybinding(mc.gameSettings.keyBindJump);
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1404 */     if ((shouldNotSprint() && this.customNoSprintMode.is("Enabled")) || this.noMoveOnStartCounter < this.customNoMoveTicksOnStart.getValue()) {
/* 1405 */       mc.thePlayer.setSprinting(false);
/* 1406 */       mc.gameSettings.keyBindSprint.pressed = false;
/*      */     } 
/*      */     
/* 1409 */     this.sneakCounter++;
/*      */     
/* 1411 */     boolean hadToSneak = false;
/*      */     
/* 1413 */     if (shouldSneak(true) && this.customSneakMode.is("Enabled")) {
/* 1414 */       mc.gameSettings.keyBindSneak.pressed = true;
/* 1415 */       hadToSneak = true;
/*      */     } else {
/* 1417 */       mc.gameSettings.keyBindSneak.pressed = KeyboardUtil.isPressed(mc.gameSettings.keyBindSneak);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1422 */     boolean allowPlacing = true;
/*      */     
/* 1424 */     if (this.customJumpMode.is("Place when falling") && !mc.thePlayer.onGround && mc.thePlayer.motionY >= 0.0D) {
/* 1425 */       allowPlacing = false;
/*      */     }
/*      */     
/* 1428 */     this.placeDelay++;
/*      */     
/* 1430 */     if (this.placeDelay < this.nextPlaceDelay && (this.applyPlaceDelayOffground.isEnabled() || mc.thePlayer.onGround)) {
/* 1431 */       allowPlacing = false;
/*      */     }
/*      */     
/* 1434 */     if (!WorldUtil.negativeExpand(getMinDistFromBlock())) {
/* 1435 */       allowPlacing = false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1440 */     if (shouldRotate()) {
/* 1441 */       updateRotations(getBasicCustomRotations((this.overAir && this.info != null && allowPlacing)), this.instantRotations.isEnabled(), this.yawSpeedMode.getMode(), this.minYawSpeed.getValue(), this.maxYawSpeed.getValue(), this.minYawAccel.getValue(), this.maxYawAccel.getValue(), this.reduceYawSpeedWhenAlmostDone.isEnabled(), this.minYawSpeedWhenAlmostDone.getValue(), this.maxYawSpeedWhenAlmostDone.getValue(), this.pitchSpeedMode.getMode(), this.minPitchSpeed.getValue(), this.maxPitchSpeed.getValue(), this.minPitchAccel.getValue(), this.maxPitchAccel.getValue(), this.reducePitchSpeedWhenAlmostDone.isEnabled(), this.minPitchSpeedWhenAlmostDone.getValue(), this.maxPitchSpeedWhenAlmostDone.getValue(), this.minYawChange.getValue(), this.minPitchChange.getValue(), this.rotsRandomisation.getValue());
/* 1442 */     } else if (!this.bCustomRotationsTiming.is("Never") && this.bCustomResetRotsIfNotRotating.isEnabled()) {
/* 1443 */       this.rotations.updateRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 1448 */     mc.rightClickDelayTimer = 0;
/*      */     
/* 1450 */     Vec3 raytraceVec3 = null;
/*      */     
/* 1452 */     if (this.overAir && this.info != null && allowPlacing) {
/* 1453 */       MovingObjectPosition raytrace = WorldUtil.raytrace(this.rotations.getYaw(), this.rotations.getPitch());
/*      */       
/* 1455 */       if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos) && raytrace.sideHit == this.info.facing) {
/* 1456 */         raytraceVec3 = raytrace.hitVec;
/*      */       }
/*      */     } 
/*      */     
/* 1460 */     if (raytraceVec3 == null && this.bCustomOnlyPlaceIfRaytraceSuccess.isEnabled()) {
/* 1461 */       allowPlacing = false;
/*      */     }
/*      */     
/* 1464 */     if (this.overAir && this.info != null && allowPlacing) {
/*      */       
/* 1466 */       Vec3 vec3 = (this.rotationVec3 != null && this.yawDone && this.pitchDone) ? this.rotationVec3 : ((raytraceVec3 != null) ? raytraceVec3 : WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch()));
/*      */       
/* 1468 */       placeBlock(vec3);
/*      */       
/* 1470 */       this.nextPlaceDelay = getNextPlaceDelay();
/*      */       
/* 1472 */       this.placeDelay = 0;
/* 1473 */       this.rightClickCounter = 0;
/*      */       
/* 1475 */       switch (this.info.facing) {
/*      */         case NORTH:
/* 1477 */           this.facingYaw = 0.0F;
/* 1478 */           this.facingPitch = (float)(mc.gameSettings.keyBindJump.pressed ? 90.0D : this.bCustomPitchValue.getValue());
/*      */           break;
/*      */         case SOUTH:
/* 1481 */           this.facingYaw = 180.0F;
/* 1482 */           this.facingPitch = (float)(mc.gameSettings.keyBindJump.pressed ? 90.0D : this.bCustomPitchValue.getValue());
/*      */           break;
/*      */         case EAST:
/* 1485 */           this.facingYaw = 90.0F;
/* 1486 */           this.facingPitch = (float)(mc.gameSettings.keyBindJump.pressed ? 90.0D : this.bCustomPitchValue.getValue());
/*      */           break;
/*      */         case WEST:
/* 1489 */           this.facingYaw = -90.0F;
/* 1490 */           this.facingPitch = (float)(mc.gameSettings.keyBindJump.pressed ? 90.0D : this.bCustomPitchValue.getValue());
/*      */           break;
/*      */         case UP:
/* 1493 */           this.facingPitch = 90.0F;
/*      */           break;
/*      */         case DOWN:
/* 1496 */           this.facingPitch = -90.0F;
/*      */           break;
/*      */       } 
/*      */       
/* 1500 */       this.placing = true;
/* 1501 */       this.sneakPlacementCounter++;
/*      */     } else {
/* 1503 */       boolean rightClick = false;
/*      */       
/* 1505 */       switch (this.extraRightClicks.getMode()) {
/*      */         case "Disabled":
/* 1507 */           rightClick = false;
/*      */           break;
/*      */         case "Normal":
/* 1510 */           rightClick = (this.rightClickCounter % 2 == 0 || Math.random() < 0.2D);
/*      */           break;
/*      */         case "Dragclick":
/* 1513 */           rightClick = (this.rightClickCounter == 0 || this.rightClickCounter >= 3);
/*      */           break;
/*      */         case "Always":
/* 1516 */           rightClick = true;
/*      */           break;
/*      */       } 
/*      */       
/* 1520 */       if (rightClick) {
/* 1521 */         PacketUtil.sendPacket((Packet)new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
/*      */       }
/*      */       
/* 1524 */       this.rightClickCounter++;
/*      */       
/* 1526 */       this.placing = false;
/*      */     } 
/*      */     
/* 1529 */     mc.gameSettings.keyBindUseItem.pressed = false;
/* 1530 */     mc.objectMouseOver = null;
/*      */     
/* 1532 */     if (!hadToSneak && shouldSneak(false) && this.customSneakMode.is("Enabled")) {
/* 1533 */       mc.gameSettings.keyBindSneak.pressed = true;
/*      */     }
/*      */   }
/*      */   
/*      */   private boolean placeBlock(Vec3 vec3) {
/* 1538 */     boolean placed = false;
/*      */     
/* 1540 */     if (this.info != null && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemBlock)
/*      */     {
/* 1542 */       if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), this.info.pos, this.info.facing, vec3)) {
/* 1543 */         placed = true;
/*      */         
/* 1545 */         if (this.swingAnimation.isEnabled()) {
/* 1546 */           mc.thePlayer.swingItem();
/*      */         } else {
/* 1548 */           PacketUtil.sendPacket((Packet)new C0APacketAnimation());
/*      */         } 
/*      */         
/* 1551 */         this.blocksPlaced++;
/*      */       } 
/*      */     }
/*      */     
/* 1555 */     return placed;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void updateRotations(float[] rots, boolean instantRotations, String yawSpeedMode, double minYawSpeed, double maxYawSpeed, double minYawAccel, double maxYawAccel, boolean reduceYawSpeedWhenAlmostDone, double minYawSpeedWhenAlmostDone, double maxYawSpeedWhenAlmostDone, String pitchSpeedMode, double minPitchSpeed, double maxPitchSpeed, double minPitchAccel, double maxPitchAccel, boolean reducePitchSpeedWhenAlmostDone, double minPitchSpeedWhenAlmostDone, double maxPitchSpeedWhenAlmostDone, double minYawChange, double minPitchChange, double rotsRandomisation) {
/* 1562 */     float requestedYaw = rots[0];
/* 1563 */     float requestedPitch = rots[1];
/*      */     
/* 1565 */     if (instantRotations) {
/* 1566 */       this.rotations.updateRotations(requestedYaw, requestedPitch);
/*      */       
/*      */       return;
/*      */     } 
/* 1570 */     float yaw = this.rotations.getYaw();
/* 1571 */     float pitch = this.rotations.getPitch();
/*      */     
/* 1573 */     float aaaa = MathHelper.wrapAngleTo180_float(yaw);
/* 1574 */     float bbbb = MathHelper.wrapAngleTo180_float(requestedYaw);
/*      */     
/* 1576 */     float yawDiff = Math.abs(bbbb - aaaa);
/* 1577 */     float pitchDiff = Math.abs(requestedPitch - pitch);
/*      */     
/* 1579 */     double randomAmount = Math.random() * rotsRandomisation;
/*      */     
/* 1581 */     float to180YawDiff = (yawDiff > 180.0F) ? (360.0F - yawDiff) : yawDiff;
/*      */     
/* 1583 */     boolean reduceYawSpeed = (this.yawSpeed > to180YawDiff && reduceYawSpeedWhenAlmostDone);
/* 1584 */     boolean reducePitchSpeed = (this.pitchSpeed > pitchDiff && reducePitchSpeedWhenAlmostDone);
/*      */     
/* 1586 */     if (yawSpeedMode.equals("Randomised")) {
/* 1587 */       this.yawSpeed = (maxYawSpeed > minYawSpeed) ? ThreadLocalRandom.current().nextDouble(minYawSpeed, maxYawSpeed) : minYawSpeed;
/* 1588 */     } else if (yawSpeedMode.equals("Acceleration")) {
/* 1589 */       if (reduceYawSpeed) {
/* 1590 */         this.yawSpeed = (maxYawSpeedWhenAlmostDone > minYawSpeedWhenAlmostDone) ? ThreadLocalRandom.current().nextDouble(minYawSpeedWhenAlmostDone, maxYawSpeedWhenAlmostDone) : minYawSpeedWhenAlmostDone;
/* 1591 */         this.shouldResetYawAccel = true;
/* 1592 */       } else if (this.shouldResetYawAccel) {
/* 1593 */         this.yawSpeed = minYawSpeed;
/*      */       } else {
/* 1595 */         this.yawSpeed += (maxYawAccel > minYawAccel) ? ThreadLocalRandom.current().nextDouble(minYawAccel, maxYawAccel) : minYawAccel;
/*      */         
/* 1597 */         this.yawSpeed = Math.min(this.yawSpeed, maxYawSpeed);
/*      */       } 
/*      */       
/* 1600 */       this.shouldResetYawAccel = false;
/*      */     } 
/*      */     
/* 1603 */     if (pitchSpeedMode.equals("Randomised")) {
/* 1604 */       this.pitchSpeed = (maxPitchSpeed > minPitchSpeed) ? ThreadLocalRandom.current().nextDouble(minPitchSpeed, maxPitchSpeed) : minPitchSpeed;
/* 1605 */     } else if (pitchSpeedMode.equals("Acceleration")) {
/* 1606 */       if (reduceYawSpeed) {
/* 1607 */         this.pitchSpeed = (maxPitchSpeedWhenAlmostDone > minPitchSpeedWhenAlmostDone) ? ThreadLocalRandom.current().nextDouble(minPitchSpeedWhenAlmostDone, maxPitchSpeedWhenAlmostDone) : minPitchSpeedWhenAlmostDone;
/* 1608 */         this.shouldResetPitchAccel = true;
/* 1609 */       } else if (this.shouldResetPitchAccel) {
/* 1610 */         this.pitchSpeed = minPitchSpeed;
/*      */       } else {
/* 1612 */         this.pitchSpeed += (maxPitchAccel > minPitchAccel) ? ThreadLocalRandom.current().nextDouble(minPitchAccel, maxPitchAccel) : minPitchAccel;
/*      */         
/* 1614 */         this.pitchSpeed = Math.min(this.pitchSpeed, maxPitchSpeed);
/*      */       } 
/*      */       
/* 1617 */       this.shouldResetPitchAccel = false;
/*      */     } 
/*      */     
/* 1620 */     boolean yawChange = (yawDiff > minYawChange && yawDiff < 360.0D - minYawChange);
/* 1621 */     boolean pitchChange = (pitchDiff > minPitchChange);
/*      */     
/* 1623 */     if (yawChange) {
/* 1624 */       this.yawDone = false;
/*      */       
/* 1626 */       if (yawDiff > 180.0F) {
/* 1627 */         if (bbbb > aaaa) {
/* 1628 */           yaw = (float)(yaw - Math.min(this.yawSpeed, yawDiff));
/*      */         } else {
/* 1630 */           yaw = (float)(yaw + Math.min(this.yawSpeed, yawDiff));
/*      */         }
/*      */       
/* 1633 */       } else if (bbbb > aaaa) {
/* 1634 */         yaw = (float)(yaw + Math.min(this.yawSpeed, yawDiff));
/*      */       } else {
/* 1636 */         yaw = (float)(yaw - Math.min(this.yawSpeed, yawDiff));
/*      */       } 
/*      */ 
/*      */       
/* 1640 */       yaw = (float)(yaw + Math.random() * randomAmount - randomAmount * 0.5D);
/*      */     } else {
/* 1642 */       this.yawDone = true;
/* 1643 */       this.shouldResetYawAccel = true;
/*      */     } 
/*      */     
/* 1646 */     this.yawSpeed = Math.min(this.yawSpeed, Math.abs(this.rotations.getYaw() - yaw));
/*      */     
/* 1648 */     if (pitchChange) {
/* 1649 */       this.pitchDone = false;
/*      */       
/* 1651 */       if (requestedPitch > pitch) {
/* 1652 */         pitch = (float)(pitch + Math.min(this.pitchSpeed, pitchDiff));
/*      */       } else {
/* 1654 */         pitch = (float)(pitch - Math.min(this.pitchSpeed, pitchDiff));
/*      */       } 
/*      */       
/* 1657 */       pitch = (float)(pitch + Math.random() * randomAmount - randomAmount * 0.5D);
/*      */       
/* 1659 */       if (pitch > 88.0F) {
/* 1660 */         pitch = 90.0F;
/* 1661 */       } else if (pitch < -88.0F) {
/* 1662 */         pitch = -90.0F;
/*      */       } 
/*      */     } else {
/* 1665 */       this.pitchDone = true;
/* 1666 */       this.shouldResetPitchAccel = true;
/*      */     } 
/*      */     
/* 1669 */     this.rotations.updateRotations(yaw, pitch);
/*      */   }
/*      */   
/*      */   private float[] getBasicCustomRotations(boolean canPlace) {
/* 1673 */     float yaw1, yaw = this.rotations.getYaw();
/* 1674 */     float pitch = this.rotations.getPitch();
/*      */     
/* 1676 */     this.rotationVec3 = null;
/*      */     
/* 1678 */     switch (this.bCustomRotationsMode.getMode()) {
/*      */       case "Facing":
/* 1680 */         yaw = this.facingYaw;
/* 1681 */         pitch = this.facingPitch;
/*      */         break;
/*      */       case "Block center":
/* 1684 */         if (canPlace) {
/* 1685 */           Vec3 hitVec = WorldUtil.getVec3ClosestFromRots(this.info.pos, this.info.facing, true, this.rotations.getYaw(), this.rotations.getPitch());
/*      */           
/* 1687 */           this.rotationVec3 = hitVec;
/*      */           
/* 1689 */           float[] rots = RotationsUtil.getRotationsToPosition(hitVec.xCoord, hitVec.yCoord, hitVec.zCoord);
/*      */           
/* 1691 */           this.requestedRotationYaw = yaw = rots[0];
/* 1692 */           this.requestedRotationPitch = pitch = rots[1];
/*      */           
/* 1694 */           this.startedRotating = true; break;
/* 1695 */         }  if (!this.startedRotating) {
/* 1696 */           this.requestedRotationYaw = yaw = MovementUtil.getPlayerDirection() - 180.0F;
/* 1697 */           this.requestedRotationPitch = pitch = mc.gameSettings.keyBindJump.pressed ? 90.0F : 82.0F;
/*      */           
/* 1699 */           this.startedRotating = true; break;
/*      */         } 
/* 1701 */         yaw = this.requestedRotationYaw;
/* 1702 */         pitch = this.requestedRotationPitch;
/*      */         break;
/*      */       
/*      */       case "Movement based":
/* 1706 */         if (!this.startedRotating || MovementUtil.isMoving()) {
/* 1707 */           this.requestedRotationYaw = yaw = MovementUtil.getPlayerDirection() - this.bCustomYawOffset.getValue();
/* 1708 */           this.requestedRotationPitch = pitch = (float)(mc.gameSettings.keyBindJump.pressed ? 90.0D : this.bCustomPitchValue.getValue());
/*      */           
/* 1710 */           this.startedRotating = true; break;
/*      */         } 
/* 1712 */         yaw = this.requestedRotationYaw;
/* 1713 */         pitch = this.requestedRotationPitch;
/*      */         break;
/*      */       
/*      */       case "Godbridge":
/* 1717 */         yaw1 = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) + 720.0F;
/*      */         
/* 1719 */         if (yaw1 % 90.0F < 22.5F || yaw1 % 90.0F > 67.5F) {
/*      */           
/* 1721 */           this.requestedRotationYaw = yaw = yaw1 + 45.0F - (yaw1 + 45.0F) % 90.0F - 135.0F;
/* 1722 */           this.requestedRotationPitch = pitch = 75.9F;
/*      */           break;
/*      */         } 
/* 1725 */         this.requestedRotationYaw = yaw = yaw1 - yaw1 % 90.0F + 45.0F - 180.0F;
/* 1726 */         this.requestedRotationPitch = pitch = 77.0F;
/*      */         break;
/*      */       
/*      */       case "Raytrace pitch":
/* 1730 */         yaw = MovementUtil.getPlayerDirection() - this.bCustomYawOffset.getValue();
/*      */         
/* 1732 */         if (canPlace) {
/* 1733 */           FixedRotations testRotations = new FixedRotations(this.rotations.getYaw(), this.rotations.getPitch());
/*      */           float testPitch;
/* 1735 */           for (testPitch = 90.0F; testPitch >= 45.0F; testPitch -= (pitch > 70.0F) ? 0.15F : 1.0F) {
/* 1736 */             testRotations.updateRotations(MovementUtil.getPlayerDirection() - this.bCustomYawOffset.getValue(), testPitch);
/*      */             
/* 1738 */             MovingObjectPosition raytrace = WorldUtil.raytrace(testRotations.getYaw(), testRotations.getPitch());
/*      */             
/* 1740 */             if (raytrace != null && raytrace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && raytrace.getBlockPos().equals(this.info.pos)) {
/* 1741 */               boolean sameFace = (raytrace.sideHit == this.info.facing);
/* 1742 */               boolean horizontalFace = (raytrace.sideHit != EnumFacing.UP && raytrace.sideHit != EnumFacing.DOWN);
/*      */               
/* 1744 */               if (sameFace || horizontalFace) {
/* 1745 */                 if (!sameFace) {
/* 1746 */                   BlockPos oldPos = this.info.pos;
/* 1747 */                   this.info = new BlockInfo(oldPos, raytrace.sideHit);
/*      */                   
/* 1749 */                   LogUtil.addChatMessage("Changed block face");
/*      */                 } 
/*      */ 
/*      */                 
/* 1753 */                 yaw = MovementUtil.getPlayerDirection() - this.bCustomYawOffset.getValue();
/* 1754 */                 pitch = testPitch;
/*      */                 
/* 1756 */                 this.rotationVec3 = raytrace.hitVec; break;
/*      */               } 
/*      */             } 
/*      */           }  break;
/*      */         } 
/* 1761 */         if (!this.startedRotating) {
/* 1762 */           pitch = 80.0F;
/* 1763 */           this.startedRotating = true;
/*      */         } 
/*      */         break;
/*      */       case "Static":
/* 1767 */         yaw = this.startingYaw - 180.0F;
/* 1768 */         pitch = 82.0F;
/*      */         break;
/*      */     } 
/*      */     
/* 1772 */     return new float[] { yaw, pitch };
/*      */   }
/*      */   
/*      */   private boolean shouldRotate() {
/* 1776 */     return this.bCustomRotationsTiming.is("Always") ? true : (this.bCustomRotationsTiming.is("Never") ? false : (this.bCustomRotationsTiming.is("Over air") ? this.overAir : (this.bCustomRotationsTiming.is("When placing") ? this.placing : (this.bCustomRotationsTiming.is("When not jumping") ? ((this.customJumpMode.is("None") || this.customJumpMode.is("Godbridge")) ? ((!mc.thePlayer.onGround || !mc.gameSettings.keyBindJump.pressed)) : (!mc.thePlayer.onGround)) : true))));
/*      */   }
/*      */   
/*      */   private boolean shouldNotSprint() {
/* 1780 */     return this.customNoSprintTiming.is("Always") ? true : (this.customNoSprintTiming.is("Never") ? false : (
/* 1781 */       this.customNoSprintTiming.is("Over air") ? this.overAir : (this.customNoSprintTiming.is("When placing") ? this.placing : (
/* 1782 */       this.customNoSprintTiming.is("Onground") ? mc.thePlayer.onGround : (this.customNoSprintTiming.is("Offground") ? (!mc.thePlayer.onGround) : true)))));
/*      */   }
/*      */   
/*      */   private boolean shouldSneak(boolean prePlacement) {
/* 1786 */     if (!this.customSneakOffground.isEnabled() && (!mc.thePlayer.onGround || mc.gameSettings.keyBindJump.pressed)) {
/* 1787 */       return false;
/*      */     }
/*      */     
/* 1790 */     return this.customSneakTiming.is("Always") ? true : (this.customSneakTiming.is("Never") ? false : (this.customSneakTiming.is("Over air") ? this.overAir : (this.customSneakTiming.is("Over air and place fail") ? ((this.overAir && !prePlacement && !this.placing)) : (this.customSneakTiming.is("When placing") ? this.placing : (this.customSneakTiming.is("Every x blocks") ? ((this.sneakPlacementCounter % this.customSneakFrequency.getValue() == 0 && this.overAir && prePlacement)) : (this.customSneakTiming.is("Alternate") ? ((this.sneakCounter % 2 == 0)) : false))))));
/*      */   }
/*      */   
/*      */   private int getNextPlaceDelay() {
/* 1794 */     if (this.minPlaceDelay.getValue() >= this.maxPlaceDelay.getValue()) {
/* 1795 */       return this.minPlaceDelay.getValue();
/*      */     }
/*      */     
/* 1798 */     return ThreadLocalRandom.current().nextInt(this.minPlaceDelay.getValue(), this.maxPlaceDelay.getValue());
/*      */   }
/*      */   
/*      */   private double getMinDistFromBlock() {
/* 1802 */     return mc.thePlayer.onGround ? this.distFromBlock.getValue() : this.offGroundDistFromBlock.getValue();
/*      */   }
/*      */   
/*      */   private double getMotionMult() {
/* 1806 */     return mc.thePlayer.onGround ? this.customMotionMultOnGround.getValue() : this.customMotionMultOffGround.getValue();
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onEntityAction(EntityActionEvent event) {
/* 1811 */     switch (this.mode.getMode()) {
/*      */       case "Basic":
/* 1813 */         if (this.noSprint.is("Spoof")) {
/* 1814 */           event.setSprinting(false);
/*      */         }
/*      */         break;
/*      */       case "Basic custom":
/* 1818 */         if (shouldNotSprint() && this.customNoSprintMode.is("Spoof")) {
/* 1819 */           event.setSprinting(false);
/*      */         }
/*      */         
/* 1822 */         if (shouldSneak(false) && this.customSneakMode.is("Spoof")) {
/* 1823 */           event.setSneaking(true);
/*      */         }
/*      */         break;
/*      */       case "Hypixel":
/* 1827 */         if (!mc.thePlayer.onGround || MovementUtil.getSpeedAmplifier() > 0);
/*      */ 
/*      */ 
/*      */         
/* 1831 */         if (this.hypixelSprint.is("None") || !MovementUtil.isMoving() || MovementUtil.getHorizontalMotion() > 0.1D);
/*      */         break;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Listener
/*      */   public void onStrafe(StrafeEvent event) {
/* 1843 */     switch (this.mode.getMode()) {
/*      */       case "Basic custom":
/* 1845 */         if (this.customIgnoreSpeedPot.isEnabled() && 
/* 1846 */           mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
/* 1847 */           float speed = (float)(mc.thePlayer.isSprinting() ? 0.13D : 0.1D);
/*      */           
/* 1849 */           float f4 = (mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double((mc.thePlayer.getEntityBoundingBox()).minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock()).slipperiness * 0.91F;
/*      */           
/* 1851 */           float f = 0.16277136F / f4 * f4 * f4;
/* 1852 */           float f5 = speed * f;
/*      */           
/* 1854 */           event.setAttributeSpeed(f5);
/*      */         } 
/*      */ 
/*      */         
/* 1858 */         if (this.movementType.is("Fixed") && shouldRotate()) {
/* 1859 */           fixMovement(event);
/*      */         }
/*      */         
/* 1862 */         if (this.noMoveOnStartCounter < this.customNoMoveTicksOnStart.getValue()) {
/* 1863 */           event.setForward(0.0F);
/* 1864 */           event.setStrafe(0.0F);
/*      */         } 
/*      */         break;
/*      */       case "Hypixel":
/* 1868 */         if (mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
/* 1869 */           float speed = (float)(mc.thePlayer.isSprinting() ? 0.13D : 0.1D);
/*      */           
/* 1871 */           float f4 = (mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double((mc.thePlayer.getEntityBoundingBox()).minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock()).slipperiness * 0.91F;
/*      */           
/* 1873 */           float f = 0.16277136F / f4 * f4 * f4;
/* 1874 */           float f5 = speed * f;
/*      */           
/* 1876 */           event.setAttributeSpeed(f5);
/*      */         } 
/*      */         break;
/*      */       case "Hypixel jump":
/* 1880 */         if (mc.thePlayer.onGround || mc.thePlayer.isSprinting());
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1885 */         mc.gameSettings.keyBindSprint.pressed = MovementUtil.isMoving();
/* 1886 */         mc.thePlayer.setSprinting(MovementUtil.isMoving());
/*      */         
/* 1888 */         if (mc.thePlayer.onGround && mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
/* 1889 */           float speed = (float)(mc.thePlayer.isSprinting() ? 0.13D : 0.1D);
/*      */           
/* 1891 */           float f4 = (mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double((mc.thePlayer.getEntityBoundingBox()).minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock()).slipperiness * 0.91F;
/*      */           
/* 1893 */           float f = 0.16277136F / f4 * f4 * f4;
/* 1894 */           float f5 = speed * f;
/*      */           
/* 1896 */           event.setAttributeSpeed(f5);
/*      */         } 
/*      */         break;
/*      */       case "Andromeda":
/* 1900 */         if (this.rotationsEnabled.isEnabled() && this.moveFix.isEnabled()) {
/* 1901 */           fixMovement(event);
/*      */         }
/*      */         break;
/*      */       case "Sneak":
/*      */       case "Godbridge":
/* 1906 */         fixMovement(event);
/*      */         break;
/*      */     } 
/*      */   }
/*      */   
/*      */   private void fixMovement(StrafeEvent event) {
/* 1912 */     float diff = MathHelper.wrapAngleTo180_float(MathHelper.wrapAngleTo180_float(this.rotations.getYaw()) - MathHelper.wrapAngleTo180_float(getYawDirection())) + 22.5F;
/*      */     
/* 1914 */     if (diff < 0.0F) {
/* 1915 */       diff = 360.0F + diff;
/*      */     }
/*      */     
/* 1918 */     int a = (int)(diff / 45.0D);
/*      */     
/* 1920 */     float value = (event.getForward() != 0.0F) ? Math.abs(event.getForward()) : Math.abs(event.getStrafe());
/*      */     
/* 1922 */     float forward = value;
/* 1923 */     float strafe = 0.0F;
/*      */     
/* 1925 */     for (int i = 0; i < 8 - a; i++) {
/* 1926 */       float[] dirs = MovementUtil.incrementMoveDirection(forward, strafe);
/*      */       
/* 1928 */       forward = dirs[0];
/* 1929 */       strafe = dirs[1];
/*      */     } 
/*      */     
/* 1932 */     event.setForward(forward);
/* 1933 */     event.setStrafe(strafe);
/*      */     
/* 1935 */     event.setYaw(this.rotations.getYaw());
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onJump(JumpEvent event) {
/* 1940 */     switch (this.mode.getMode()) {
/*      */       case "Basic custom":
/* 1942 */         if (this.movementType.is("Fixed") && shouldRotate() && !this.mode.is("Raytrace custom")) {
/* 1943 */           event.setBoosting(false);
/* 1944 */         } else if (!this.customAllowJumpBoost.isEnabled()) {
/* 1945 */           event.setBoosting(false);
/*      */         } 
/*      */         
/* 1948 */         if (this.movementType.is("Normal")) {
/* 1949 */           event.setBoostAmount((float)this.customJumpBoostAmount.getValue());
/*      */         }
/*      */         break;
/*      */       case "Hypixel":
/* 1953 */         if (this.hypixelSprint.is("Semi") && this.startedSprint && this.wasHovering) {
/* 1954 */           event.setCancelled(true);
/*      */         }
/*      */         
/* 1957 */         event.setBoostAmount(0.1F);
/*      */         break;
/*      */       case "Hypixel jump":
/* 1960 */         this.sprintTicks++;
/* 1961 */         if (this.sprintTicks % 2 == 0) {
/* 1962 */           if (this.jumpSprintMode.is("Novoline")) {
/* 1963 */             event.setBoostAmount(0.201F);
/*      */           } else {
/* 1965 */             event.setBoostAmount(0.201F);
/*      */           } 
/*      */         }
/* 1968 */         if (this.sprintTicks > 1) {
/* 1969 */           event.setBoosting(true);
/*      */         }
/*      */         break;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       case "Andromeda":
/* 1979 */         if (this.rotationsEnabled.isEnabled() && this.moveFix.isEnabled())
/* 1980 */           event.setYaw(this.rotations.getYaw()); 
/*      */         break;
/*      */     }  } @Listener
/*      */   public void onMove(MoveEvent event) {
/*      */     boolean allowTower;
/*      */     boolean airUnder;
/*      */     boolean diagonal;
/*      */     double speed;
/* 1988 */     switch (this.mode.getMode()) {
/*      */       case "Basic custom":
/* 1990 */         if (this.movementType.is("Strafe")) {
/* 1991 */           if (this.noMoveOnStartCounter < this.customNoMoveTicksOnStart.getValue()) {
/* 1992 */             MovementUtil.strafe(event, 0.0D);
/* 1993 */           } else if (mc.thePlayer.onGround) {
/* 1994 */             MovementUtil.strafe(event, this.customOnGroundSpeed.getValue() + MovementUtil.getSpeedAmplifier() * this.customOnGroundPotionExtra.getValue() - randomAmount());
/* 1995 */           } else if (this.customOffGroundStrafe.isEnabled()) {
/* 1996 */             MovementUtil.strafe(event, this.customOffGroundSpeed.getValue() + MovementUtil.getSpeedAmplifier() * this.customOffGroundPotionExtra.getValue() - randomAmount());
/*      */           } 
/* 1998 */         } else if (this.movementType.is("Normal")) {
/* 1999 */           double mult = (this.customMultAffectsNextMotion.isEnabled() && !mc.thePlayer.onGround) ? Math.min(getMotionMult(), 1.1D) : getMotionMult();
/*      */ 
/*      */           
/* 2002 */           event.setX(mc.thePlayer.motionX = event.getX() * mult);
/* 2003 */           event.setZ(mc.thePlayer.motionZ = event.getZ() * mult);
/*      */           
/* 2005 */           event.setX(event.getX() * mult);
/* 2006 */           event.setZ(event.getZ() * mult);
/*      */         } 
/*      */ 
/*      */         
/* 2010 */         this.noMoveOnStartCounter++;
/*      */         break;
/*      */       case "Hypixel":
/* 2013 */         allowTower = true;
/*      */         
/* 2015 */         switch (this.hypixelSprint.getMode()) {
/*      */           case "Semi":
/* 2017 */             if (!this.startedSprint) {
/* 2018 */               MovementUtil.strafe(event, 0.185D - randomAmount());
/*      */               
/* 2020 */               if (mc.thePlayer.onGround) {
/* 2021 */                 if (this.sprintTicks == 0) {
/* 2022 */                   MovementUtil.jump(event);
/* 2023 */                   this.sprintTicks++;
/*      */                 } else {
/* 2025 */                   this.startedSprint = true;
/* 2026 */                   this.sprintTicks = 0;
/*      */                 } 
/*      */               }
/*      */               
/* 2030 */               allowTower = false; break;
/*      */             } 
/* 2032 */             diagonal = MovementUtil.isGoingDiagonally(0.12D);
/*      */             
/* 2034 */             if (this.towering != this.wasTowering) {
/* 2035 */               if (!this.towering && this.toweringTicks > 4) {
/* 2036 */                 this.pendingMovementStop = true;
/*      */               }
/*      */               
/* 2039 */               this.toweringTicks = 0;
/*      */             } 
/*      */             
/* 2042 */             if (mc.thePlayer.onGround && this.pendingMovementStop) {
/* 2043 */               MovementUtil.strafe(event, 0.0D);
/* 2044 */               this.pendingMovementStop = false;
/*      */             }
/* 2046 */             else if (this.towering) {
/* 2047 */               if (!hypixelTowerAllowSprint() || diagonal || !mc.thePlayer.onGround);
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2052 */               this.sprintTicks = 0;
/*      */             } else {
/* 2054 */               this.sprintTicks++;
/*      */               
/* 2056 */               double d = 0.26999D - Math.random() * 1.0E-5D;
/*      */               
/* 2058 */               if (mc.thePlayer.onGround) {
/*      */                 
/* 2060 */                 MovementUtil.strafe(event, d - 0.15D);
/*      */                 
/* 2062 */                 this.spoofedX = mc.thePlayer.posX + event.getX();
/* 2063 */                 this.spoofedZ = mc.thePlayer.posZ + event.getZ();
/*      */                 
/* 2065 */                 MovementUtil.strafe(event, d);
/*      */               } else {
/* 2067 */                 this.sprintTicks = 0;
/*      */               } 
/*      */             } 
/*      */ 
/*      */             
/* 2072 */             this.wasTowering = this.towering;
/* 2073 */             this.toweringTicks++;
/*      */             break;
/*      */           
/*      */           case "Full":
/* 2077 */             this.sprintTicks++;
/*      */             
/* 2079 */             speed = 0.24989D - Math.random() * 1.0E-5D;
/*      */             
/* 2081 */             if (mc.thePlayer.onGround && !this.towering) {
/* 2082 */               MovementUtil.strafe(speed - 0.18D);
/*      */               
/* 2084 */               this.spoofedX = mc.thePlayer.posX + event.getX();
/* 2085 */               this.spoofedZ = mc.thePlayer.posZ + event.getZ();
/*      */ 
/*      */ 
/*      */ 
/*      */               
/* 2090 */               MovementUtil.strafe(event, speed); break;
/*      */             } 
/* 2092 */             this.sprintTicks = 0;
/*      */             break;
/*      */           
/*      */           case "None":
/* 2096 */             if (this.towering != this.wasTowering) {
/* 2097 */               if (!this.towering && this.toweringTicks > 4) {
/* 2098 */                 this.pendingMovementStop = true;
/*      */               }
/*      */               
/* 2101 */               if (!this.towering);
/*      */ 
/*      */ 
/*      */               
/* 2105 */               this.toweringTicks = 0;
/*      */             } 
/*      */             
/* 2108 */             if (mc.thePlayer.onGround && this.pendingMovementStop) {
/* 2109 */               MovementUtil.strafe(event, 0.04D);
/* 2110 */               this.pendingMovementStop = false;
/*      */             } 
/*      */             
/* 2113 */             if (!this.towering) {
/* 2114 */               event.setX(event.getX() * 0.949999988079071D);
/* 2115 */               event.setZ(event.getZ() * 0.949999988079071D);
/*      */             } 
/*      */             
/* 2118 */             this.wasTowering = this.towering;
/* 2119 */             this.toweringTicks++;
/*      */             break;
/*      */         } 
/*      */         
/* 2123 */         airUnder = WorldUtil.negativeExpand(0.299D);
/*      */         
/* 2125 */         if (allowTower) {
/* 2126 */           switch (this.hypixelTower.getMode()) {
/*      */             case "Faster vertically":
/* 2128 */               if (MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() > 0.1D && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2129 */                 double towerSpeed = MovementUtil.isGoingDiagonally(0.1D) ? this.towerSpeedWhenDiagonal.getValue() : this.towerSpeed.getValue();
/*      */                 
/* 2131 */                 if (mc.thePlayer.onGround) {
/* 2132 */                   this.towering = (mc.gameSettings.keyBindJump.pressed && !airUnder);
/*      */                   
/* 2134 */                   if (this.towering) {
/* 2135 */                     this.towerTicks = 0;
/* 2136 */                     mc.thePlayer.jumpTicks = 0;
/* 2137 */                     if (event.getY() > 0.0D) {
/* 2138 */                       event.setY(mc.thePlayer.motionY = 0.4198499917984009D);
/*      */                       
/* 2140 */                       MovementUtil.strafe(event, towerSpeed - randomAmount());
/*      */                     } 
/*      */                   } 
/* 2143 */                 } else if (this.towering) {
/* 2144 */                   if (this.towerTicks == 2) {
/* 2145 */                     event.setY(Math.floor(mc.thePlayer.posY + 1.0D) - mc.thePlayer.posY);
/* 2146 */                   } else if (this.towerTicks == 3) {
/*      */                     
/* 2148 */                     event.setY(mc.thePlayer.motionY = 0.4198499917984009D);
/*      */                     
/* 2150 */                     MovementUtil.strafe(event, towerSpeed - randomAmount());
/* 2151 */                     this.towerTicks = 0;
/*      */                     
/* 2153 */                     this.towering = false;
/*      */                   } 
/*      */                 } 
/*      */ 
/*      */                 
/* 2158 */                 if (this.towering);
/*      */ 
/*      */ 
/*      */                 
/* 2162 */                 this.towerTicks++;
/*      */               } 
/*      */               break;
/*      */             case "Faster horizontally":
/* 2166 */               if (mc.thePlayer.onGround) {
/* 2167 */                 this.towerTicks = 0;
/*      */               }
/*      */               
/* 2170 */               mc.thePlayer.jumpTicks = 0;
/*      */               
/* 2172 */               if (MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() > 0.1D && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2173 */                 this.towering = mc.gameSettings.keyBindJump.pressed;
/*      */                 
/* 2175 */                 if (this.towering) {
/* 2176 */                   if (mc.thePlayer.onGround) {
/* 2177 */                     this.towerTicks = 0;
/* 2178 */                   } else if (this.towerTicks == 7) {
/*      */                   
/*      */                   } 
/*      */                   
/* 2182 */                   this.towerTicks++;
/*      */                 } 
/*      */               } 
/*      */               break;
/*      */             case "Legit":
/* 2187 */               mc.thePlayer.jumpTicks = 0;
/*      */               break;
/*      */           } 
/*      */ 
/*      */         
/*      */         }
/*      */         break;
/*      */     } 
/*      */     
/* 2196 */     if (!this.mode.is("Hypixel") && !this.mode.is("Hypixel jump")) {
/* 2197 */       boolean canTower = false;
/*      */       
/* 2199 */       switch (this.towerTiming.getMode()) {
/*      */         case "Always":
/* 2201 */           canTower = true;
/*      */           break;
/*      */         case "Only when moving":
/* 2204 */           canTower = (MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() >= this.minMotionForMovement.getValue());
/*      */           break;
/*      */         case "Only when not moving":
/* 2207 */           canTower = (!MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() <= this.minMotionForMovement.getValue());
/*      */           break;
/*      */       } 
/*      */       
/* 2211 */       boolean pressingSpace = KeyboardUtil.isPressed(mc.gameSettings.keyBindJump);
/*      */       
/* 2213 */       switch (this.tower.getMode()) {
/*      */         case "Vanilla":
/* 2215 */           if (pressingSpace) {
/* 2216 */             MovementUtil.jump(event);
/*      */           }
/*      */           break;
/*      */         case "NCP":
/* 2220 */           if (pressingSpace && MovementUtil.getHorizontalMotion() < 0.1D && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2221 */             if (mc.thePlayer.onGround) {
/* 2222 */               MovementUtil.jump(event);
/* 2223 */               this.towerTicks = 0;
/*      */             } else {
/* 2225 */               mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2226 */               event.setY(mc.thePlayer.motionY = 0.0D);
/* 2227 */               if (this.towerTicks == 3) {
/* 2228 */                 MovementUtil.jump(event);
/* 2229 */                 this.towerTicks = 0;
/*      */               } 
/*      */             } 
/* 2232 */             this.towerTicks++;
/*      */           } 
/*      */           break;
/*      */         case "NCP2":
/* 2236 */           if (pressingSpace && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2237 */             if (mc.thePlayer.onGround) {
/* 2238 */               MovementUtil.jump(event);
/* 2239 */               this.towerTicks = 0;
/*      */             } else {
/* 2241 */               mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2242 */               event.setY(mc.thePlayer.motionY = 0.0D);
/* 2243 */               if (this.towerTicks == 4) {
/* 2244 */                 MovementUtil.jump(event);
/* 2245 */                 this.towerTicks = 0;
/*      */               } 
/*      */             } 
/* 2248 */             this.towerTicks++;
/*      */           } 
/*      */           break;
/*      */         case "Hypixel":
/* 2252 */           if (MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() > 0.1D && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2253 */             if (mc.thePlayer.onGround) {
/* 2254 */               this.towering = mc.gameSettings.keyBindJump.pressed;
/*      */               
/* 2256 */               if (this.towering) {
/* 2257 */                 this.towerTicks = 0;
/* 2258 */                 mc.thePlayer.jumpTicks = 0;
/* 2259 */                 if (event.getY() > 0.0D) {
/* 2260 */                   event.setY(mc.thePlayer.motionY = 0.4198499917984009D);
/* 2261 */                   MovementUtil.strafe(event, 0.26D);
/*      */                 }
/*      */               
/*      */               } 
/* 2265 */             } else if (this.towerTicks == 2) {
/* 2266 */               event.setY(Math.floor(mc.thePlayer.posY + 1.0D) - mc.thePlayer.posY);
/* 2267 */             } else if (this.towerTicks == 3) {
/* 2268 */               this.towering = mc.gameSettings.keyBindJump.pressed;
/*      */               
/* 2270 */               if (this.towering) {
/* 2271 */                 event.setY(mc.thePlayer.motionY = 0.4198499917984009D);
/*      */                 
/* 2273 */                 this.towerTicks = 0;
/*      */               } 
/*      */             } 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 2280 */             this.towerTicks++; break;
/*      */           } 
/* 2282 */           this.towering = false;
/*      */           break;
/*      */         
/*      */         case "Hypixel2":
/* 2286 */           if (mc.thePlayer.onGround) {
/* 2287 */             this.towerTicks = 0;
/*      */           }
/*      */           
/* 2290 */           mc.thePlayer.jumpTicks = 0;
/*      */           
/* 2292 */           if (MovementUtil.isMoving() && MovementUtil.getHorizontalMotion() > 0.1D && !mc.thePlayer.isPotionActive(Potion.jump)) {
/* 2293 */             this.towering = mc.gameSettings.keyBindJump.pressed;
/*      */             
/* 2295 */             if (this.towering) {
/* 2296 */               if (mc.thePlayer.onGround) {
/* 2297 */                 this.towerTicks = 0;
/* 2298 */               } else if (this.towerTicks == 7) {
/* 2299 */                 mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
/*      */               } 
/*      */               
/* 2302 */               this.towerTicks++;
/*      */             }  break;
/*      */           } 
/* 2305 */           this.towering = false;
/*      */           break;
/*      */         
/*      */         case "Legit":
/* 2309 */           if (pressingSpace) {
/* 2310 */             mc.thePlayer.jumpTicks = 0;
/*      */           }
/*      */           break;
/*      */         case "Custom":
/* 2314 */           if (pressingSpace && canTower) {
/* 2315 */             mc.thePlayer.jumpTicks = 0;
/*      */             
/* 2317 */             if (mc.thePlayer.onGround) {
/* 2318 */               if (event.getY() > 0.0D) {
/* 2319 */                 event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2320 */                 MovementUtil.strafe(event, 0.26D);
/*      */               } 
/*      */               
/* 2323 */               this.towerTicks = 0; break;
/*      */             } 
/* 2325 */             this.towerTicks++;
/*      */             
/* 2327 */             switch (this.towerTicks) {
/*      */               case 1:
/* 2329 */                 switch (this.teleportTick1.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2331 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2332 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2335 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2336 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2340 */                 switch (this.yMotionTick1.getMode()) {
/*      */                   case "Set motionY":
/* 2342 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2343 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue1.getValue());
/*      */                     }
/* 2345 */                     event.setY(this.yMotionValue1.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2349 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2350 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue1.getValue());
/*      */                     }
/* 2352 */                     event.setY(event.getY() + this.yMotionValue1.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2356 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2357 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 2:
/* 2362 */                 switch (this.teleportTick2.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2364 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2365 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2368 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2369 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2373 */                 switch (this.yMotionTick2.getMode()) {
/*      */                   case "Set motionY":
/* 2375 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2376 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue2.getValue());
/*      */                     }
/* 2378 */                     event.setY(this.yMotionValue2.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2382 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2383 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue2.getValue());
/*      */                     }
/* 2385 */                     event.setY(event.getY() + this.yMotionValue2.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2389 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2390 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 3:
/* 2395 */                 switch (this.teleportTick3.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2397 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2398 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2401 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2402 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2406 */                 switch (this.yMotionTick3.getMode()) {
/*      */                   case "Set motionY":
/* 2408 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2409 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue3.getValue());
/*      */                     }
/* 2411 */                     event.setY(this.yMotionValue3.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2415 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2416 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue3.getValue());
/*      */                     }
/* 2418 */                     event.setY(event.getY() + this.yMotionValue3.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2422 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2423 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 4:
/* 2428 */                 switch (this.teleportTick4.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2430 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2431 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2434 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2435 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2439 */                 switch (this.yMotionTick4.getMode()) {
/*      */                   case "Set motionY":
/* 2441 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2442 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue4.getValue());
/*      */                     }
/* 2444 */                     event.setY(this.yMotionValue4.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2448 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2449 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue4.getValue());
/*      */                     }
/* 2451 */                     event.setY(event.getY() + this.yMotionValue4.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2455 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2456 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 5:
/* 2461 */                 switch (this.teleportTick5.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2463 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2464 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2467 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2468 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2472 */                 switch (this.yMotionTick5.getMode()) {
/*      */                   case "Set motionY":
/* 2474 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2475 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue5.getValue());
/*      */                     }
/* 2477 */                     event.setY(this.yMotionValue5.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2481 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2482 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue5.getValue());
/*      */                     }
/* 2484 */                     event.setY(event.getY() + this.yMotionValue5.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2488 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2489 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 6:
/* 2494 */                 switch (this.teleportTick6.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2496 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2497 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2500 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2501 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2505 */                 switch (this.yMotionTick6.getMode()) {
/*      */                   case "Set motionY":
/* 2507 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2508 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue6.getValue());
/*      */                     }
/* 2510 */                     event.setY(this.yMotionValue6.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2514 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2515 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue6.getValue());
/*      */                     }
/* 2517 */                     event.setY(event.getY() + this.yMotionValue6.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2521 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2522 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 7:
/* 2527 */                 switch (this.teleportTick7.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2529 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2530 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2533 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2534 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2538 */                 switch (this.yMotionTick7.getMode()) {
/*      */                   case "Set motionY":
/* 2540 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2541 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue7.getValue());
/*      */                     }
/* 2543 */                     event.setY(this.yMotionValue7.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2547 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2548 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue7.getValue());
/*      */                     }
/* 2550 */                     event.setY(event.getY() + this.yMotionValue7.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2554 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2555 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */               case 8:
/* 2560 */                 switch (this.teleportTick8.getMode()) {
/*      */                   case "Set pos to rounded Y":
/* 2562 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
/* 2563 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                   
/*      */                   case "Teleport to block over":
/* 2566 */                     mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY) + 1.0D, mc.thePlayer.posZ);
/* 2567 */                     event.setY(mc.thePlayer.motionY = 0.0D);
/*      */                     break;
/*      */                 } 
/*      */                 
/* 2571 */                 switch (this.yMotionTick8.getMode()) {
/*      */                   case "Set motionY":
/* 2573 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2574 */                       event.setY(mc.thePlayer.motionY = this.yMotionValue8.getValue());
/*      */                     }
/* 2576 */                     event.setY(this.yMotionValue8.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Add motionY":
/* 2580 */                     if (this.yChangeAffectsNextMotion.isEnabled()) {
/* 2581 */                       event.setY(mc.thePlayer.motionY = event.getY() + this.yMotionValue8.getValue());
/*      */                     }
/* 2583 */                     event.setY(event.getY() + this.yMotionValue8.getValue());
/*      */                     break;
/*      */                   
/*      */                   case "Jump again":
/* 2587 */                     event.setY(mc.thePlayer.motionY = (float)this.jumpMotionY.getValue());
/* 2588 */                     this.towerTicks = 0;
/*      */                     break;
/*      */                 } 
/*      */                 break;
/*      */             } 
/*      */           } 
/*      */           break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private double randomAmount() {
/* 2601 */     return 1.0E-4D + Math.random() * 0.001D;
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onMotion(MotionEvent event) {
/* 2606 */     switch (this.mode.getMode()) {
/*      */       case "Basic custom":
/* 2608 */         switch (this.customGroundspoof.getMode()) {
/*      */           case "Offground":
/* 2610 */             event.setOnGround(false);
/*      */             break;
/*      */           case "Alternate":
/* 2613 */             if (mc.thePlayer.onGround) {
/* 2614 */               if (this.groundSpoofCounter % 2 == 0) {
/* 2615 */                 event.setOnGround(false);
/*      */               }
/*      */               
/* 2618 */               this.groundSpoofCounter++;
/*      */             } 
/*      */             break;
/*      */         } 
/*      */         break;
/*      */       case "Hypixel":
/* 2624 */         switch (this.hypixelSprint.getMode()) {
/*      */           case "Full":
/* 2626 */             if (mc.thePlayer.onGround) {
/* 2627 */               if (this.sprintTicks % 2 != 0) {
/* 2628 */                 event.setX(this.spoofedX);
/* 2629 */                 event.setZ(this.spoofedZ); break;
/*      */               } 
/* 2631 */               event.setOnGround(false);
/*      */             } 
/*      */             break;
/*      */ 
/*      */           
/*      */           case "Semi":
/* 2637 */             if (mc.thePlayer.onGround) {
/* 2638 */               if (this.ticksHovering != 0 || !KeyboardUtil.isPressed(mc.gameSettings.keyBindJump))
/* 2639 */                 switch (++this.ticksHovering) {
/*      */                   case 1:
/* 2641 */                     if (MovementUtil.isGoingDiagonally(0.1D) && this.overAir) {
/*      */                       
/* 2643 */                       this.ticksHovering = 0;
/* 2644 */                       this.wasHovering = false; break;
/*      */                     } 
/* 2646 */                     event.setY(event.getY() + 5.0E-4D);
/* 2647 */                     event.setOnGround(false);
/* 2648 */                     this.wasHovering = true;
/*      */                     break;
/*      */ 
/*      */ 
/*      */ 
/*      */                   
/*      */                   case 2:
/* 2655 */                     this.ticksHovering = 0;
/* 2656 */                     this.wasHovering = false;
/*      */                     break;
/*      */                 }  
/*      */               break;
/*      */             } 
/* 2661 */             this.ticksHovering = 0;
/*      */             break;
/*      */         } 
/*      */         
/*      */         break;
/*      */     } 
/*      */     
/* 2668 */     if (shouldRotateSilently()) {
/* 2669 */       event.setYaw(this.rotations.getYaw());
/* 2670 */       event.setPitch(this.rotations.getPitch());
/*      */     } 
/*      */   }
/*      */   
/*      */   public boolean shouldOverrideRenderedRots() {
/* 2675 */     return (this.mode.is("Hypixel") && !this.hypixelSprint.is("None"));
/*      */   }
/*      */ 
/*      */   
/*      */   @Listener
/*      */   public void onPostMotion(PostMotionEvent event) {}
/*      */   
/*      */   @Listener
/*      */   public void onPacketSend(PacketSendEvent event) {
/* 2684 */     if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
/* 2685 */       C08PacketPlayerBlockPlacement packet = (C08PacketPlayerBlockPlacement)event.getPacket();
/*      */       
/* 2687 */       if (packet.getPlacedBlockDirection() != 255) {
/* 2688 */         BlockPos pos = packet.getPosition().offset(EnumFacing.values()[packet.getPlacedBlockDirection()]);
/* 2689 */         if (this.blockPlaceESP.isEnabled()) {
/* 2690 */           int color = ClientTheme.color1.getRGB();
/*      */           
/* 2692 */           this.blockPlaceList.add(new PlacedBlock(pos, color));
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */   
/*      */   @Listener
/*      */   public void onRender3D(Render3DEvent event) {
/* 2700 */     ArrayList<PlacedBlock> toRemove = new ArrayList<>();
/*      */     
/* 2702 */     for (PlacedBlock block : this.blockPlaceList) {
/* 2703 */       float alpha = Math.max(1.0F - (float)block.timer.getTimeElapsed() / 1200.0F, 0.01F);
/*      */       
/* 2705 */       if (alpha <= 0.01F) {
/* 2706 */         toRemove.add(block);
/*      */       }
/*      */       
/* 2709 */       Color finalColor = new Color(block.color);
/*      */       
/* 2711 */       float r = finalColor.getRed() / 255.0F;
/* 2712 */       float g = finalColor.getGreen() / 255.0F;
/* 2713 */       float b = finalColor.getBlue() / 255.0F;
/*      */       
/* 2715 */       RenderUtil.prepareBoxRender(2.5F, r, g, b, alpha);
/*      */       
/* 2717 */       RenderUtil.renderBlockBox(mc.getRenderManager(), event.getPartialTicks(), block.pos.getX(), block.pos.getY(), block.pos.getZ());
/* 2718 */       RenderUtil.stopBoxRender();
/*      */     } 
/*      */     
/* 2721 */     for (PlacedBlock block : toRemove) {
/* 2722 */       this.blockPlaceList.remove(block);
/*      */     }
/*      */     
/* 2725 */     toRemove.clear();
/*      */   }
/*      */ 
/*      */   
/*      */   private boolean shouldRotateSilently() {
/* 2730 */     return (this.mode.is("Basic") || this.mode.is("Hypixel") || this.mode.is("Hypixel jump") || this.mode.is("Sneak") || this.mode.is("Godbridge") || (this.mode.is("Andromeda") && this.rotationsEnabled.isEnabled()) || (this.mode.is("Basic custom") && shouldRotate()));
/*      */   }
/*      */   
/*      */   private float getYawDirection() {
/* 2734 */     switch (this.mode.getMode()) {
/*      */       case "Basic custom":
/* 2736 */         return MovementUtil.getPlayerDirection();
/*      */     } 
/*      */     
/* 2739 */     return MovementUtil.getPlayerDirection();
/*      */   }
/*      */   public int getBlockSlot() {
/* 2742 */     int bestSlot = 0;
/*      */     
/* 2744 */     for (int i = 8; i >= 0; i--) {
/* 2745 */       ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
/*      */       
/* 2747 */       if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBlock && !InventoryUtil.isBlockBlacklisted(stack.getItem()) && stack.stackSize >= 3) {
/* 2748 */         bestSlot = i;
/* 2749 */         this.changedSlot = true;
/*      */         break;
/*      */       } 
/*      */     } 
/* 2753 */     return bestSlot;
/*      */   }
/*      */   
/*      */   public String getSuffix() {
/* 2757 */     return this.mode.getMode();
/*      */   }
/*      */   
/*      */   public class PlacedBlock {
/*      */     public final BlockPos pos;
/*      */     public final int color;
/*      */     public final TimerUtil timer;
/*      */     
/*      */     public PlacedBlock(BlockPos pos, int color) {
/* 2766 */       this.pos = pos;
/* 2767 */       this.color = color;
/* 2768 */       this.timer = new TimerUtil();
/*      */     }
/*      */   }
/*      */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\player\Scaffold.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */