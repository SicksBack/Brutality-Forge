/*     */ package com.example.examplemod.modules.modules.COMBAT;
/*     */ import Acrimony.Acrimony;
/*     */ import Acrimony.event.Listener;
/*     */ import Acrimony.event.impl.ItemRenderEvent;
/*     */ import Acrimony.event.impl.JumpEvent;
/*     */ import Acrimony.event.impl.MotionEvent;
/*     */ import Acrimony.event.impl.PostMotionEvent;
/*     */ import Acrimony.event.impl.SlowdownEvent;
/*     */ import Acrimony.event.impl.StrafeEvent;
/*     */ import Acrimony.event.impl.TickEvent;
/*     */ import Acrimony.module.Category;
/*     */ import Acrimony.module.Module;
/*     */ import Acrimony.module.impl.movement.Speed;
/*     */ import Acrimony.module.impl.player.Antivoid;
/*     */ import Acrimony.module.impl.player.Breaker;
/*     */ import Acrimony.module.impl.player.Scaffold;
/*     */ import Acrimony.module.impl.visual.ClientTheme;
/*     */ import Acrimony.setting.AbstractSetting;
/*     */ import Acrimony.setting.impl.BooleanSetting;
/*     */ import Acrimony.setting.impl.DoubleSetting;
/*     */ import Acrimony.setting.impl.IntegerSetting;
/*     */ import Acrimony.setting.impl.ModeSetting;
/*     */ import Acrimony.util.misc.LogUtil;
/*     */ import Acrimony.util.misc.TimerUtil;
/*     */ import Acrimony.util.network.PacketUtil;
/*     */ import Acrimony.util.player.FixedRotations;
/*     */ import Acrimony.util.player.MovementUtil;
/*     */ import Acrimony.util.player.RotationsUtil;
/*     */ import com.viaversion.viarewind.utils.PacketUtil;
/*     */ import com.viaversion.viaversion.api.Via;
/*     */ import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
/*     */ import com.viaversion.viaversion.api.type.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import net.minecraft.entity.Entity;
/*     */ import net.minecraft.entity.EntityLivingBase;
/*     */ import net.minecraft.entity.player.EntityPlayer;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.C07PacketPlayerDigging;
/*     */ import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
/*     */ import net.minecraft.network.play.client.C09PacketHeldItemChange;
/*     */ import net.minecraft.util.BlockPos;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.MathHelper;
/*     */ import net.minecraft.util.Vec3;
/*     */ 
/*     */ public class Killaura extends Module {
/*     */   private EntityLivingBase target;
/*     */   
/*     */   public EntityLivingBase getTarget() {
/*  52 */     return this.target;
/*     */   }
/*     */   
/*  55 */   public final ModeSetting mode = new ModeSetting("Mode", "Single", new String[] { "Single", "Switch", "Fast Switch" });
/*  56 */   private final ModeSetting filter = new ModeSetting("Filter", "Range", new String[] { "Range", "Health" });
/*  57 */   private final ModeSetting rotations = new ModeSetting("Rotations", "Normal", new String[] { "Normal", "Randomised", "Smooth", "None" });
/*  58 */   private final DoubleSetting randomAmount = new DoubleSetting("Random amount", () -> Boolean.valueOf(this.mode.is("Randomised")), 4.0D, 0.25D, 10.0D, 0.25D);
/*     */   
/*  60 */   public final DoubleSetting startingRange = new DoubleSetting("Starting range", 4.0D, 3.0D, 6.0D, 0.05D);
/*  61 */   public final DoubleSetting range = new DoubleSetting("Range", 4.0D, 3.0D, 6.0D, 0.05D);
/*  62 */   public final DoubleSetting rotationRange = new DoubleSetting("Rotation range", 4.0D, 3.0D, 6.0D, 0.05D);
/*     */   
/*  64 */   private final ModeSetting raycast = new ModeSetting("Raycast", "Disabled", new String[] { "Disabled", "Normal", "Legit" });
/*     */   
/*  66 */   private final ModeSetting attackDelayMode = new ModeSetting("Attack delay mode", "APS", new String[] { "APS", "Delay in ticks" });
/*     */   
/*  68 */   private final IntegerSetting minAPS = new IntegerSetting("Min APS", () -> Boolean.valueOf(this.attackDelayMode.is("APS")), 10, 1, 20, 1);
/*  69 */   private final IntegerSetting maxAPS = new IntegerSetting("Max APS", () -> Boolean.valueOf(this.attackDelayMode.is("APS")), 10, 1, 20, 1);
/*     */   
/*  71 */   private final IntegerSetting attackDelay = new IntegerSetting("Attack delay", () -> Boolean.valueOf(this.attackDelayMode.is("Delay in ticks")), 2, 1, 20, 1);
/*     */   
/*  73 */   private final IntegerSetting failRate = new IntegerSetting("Fail rate", 0, 0, 30, 1);
/*     */   
/*  75 */   private final IntegerSetting hurtTime = new IntegerSetting("Hurt time", 10, 0, 10, 1);
/*     */   
/*  77 */   public final ModeSetting autoblock = new ModeSetting("Autoblock", "Fake", new String[] { "Vanilla", "NCP", "AAC5", "Spoof", "Spoof2", "Blink", "Not moving", "Fake", "Watchdog", "None" });
/*     */   
/*  79 */   private final BooleanSetting noHitOnFirstTick = new BooleanSetting("No hit on first tick", () -> Boolean.valueOf(this.autoblock.is("Vanilla")), false);
/*     */   
/*  81 */   private final ModeSetting blockTiming = new ModeSetting("Block timing", () -> Boolean.valueOf((this.autoblock.is("Spoof") || this.autoblock.is("Spoof2"))), "Post", new String[] { "Pre", "Post" });
/*     */   
/*  83 */   private final IntegerSetting blockHurtTime = new IntegerSetting("Block hurt time", () -> Boolean.valueOf((this.autoblock.is("Spoof") || this.autoblock.is("Spoof2") || this.autoblock.is("Blink"))), 5, 0, 10, 1);
/*  84 */   private final BooleanSetting whileTargetNotLooking = new BooleanSetting("While target not looking", () -> Boolean.valueOf(this.autoblock.is("Blink")), true);
/*  85 */   private final ModeSetting slowdown = new ModeSetting("Slowdown", () -> Boolean.valueOf(this.autoblock.is("Blink")), "Enabled", new String[] { "Enabled", "Onground", "Offground", "Disabled" });
/*  86 */   private final IntegerSetting blinkTicks = new IntegerSetting("Blink ticks", () -> Boolean.valueOf(this.autoblock.is("Blink")), 5, 3, 10, 1);
/*     */   
/*  88 */   private final BooleanSetting whileHitting = new BooleanSetting("While hitting", () -> Boolean.valueOf(this.autoblock.is("Not moving")), false);
/*     */   
/*  90 */   private final BooleanSetting whileSpeedEnabled = new BooleanSetting("While speed enabled", () -> Boolean.valueOf((!this.autoblock.is("None") && !this.autoblock.is("Fake"))), true);
/*  91 */   private final ModeSetting moveFix = new ModeSetting("Move fix", "Disabled", new String[] { "Disabled", "Normal", "Silent" });
/*     */   
/*  93 */   private final BooleanSetting delayTransactions = new BooleanSetting("Delay transactions", false);
/*     */   
/*  95 */   private final BooleanSetting whileInventoryOpened = new BooleanSetting("While inventory", false);
/*  96 */   private final BooleanSetting whileScaffoldEnabled = new BooleanSetting("While scaffold", false);
/*  97 */   private final BooleanSetting whileUsingBreaker = new BooleanSetting("While using BedNuker", false);
/*     */   
/*  99 */   private final BooleanSetting players = new BooleanSetting("Players", true);
/* 100 */   private final BooleanSetting animals = new BooleanSetting("Animals", false);
/* 101 */   private final BooleanSetting monsters = new BooleanSetting("Monsters", false);
/* 102 */   private final BooleanSetting invisibles = new BooleanSetting("Invisibles", false);
/* 103 */   private final BooleanSetting attackDead = new BooleanSetting("Attack dead", false);
/* 104 */   public BooleanSetting visualiseTarget = new BooleanSetting("Visualise", true);
/*     */   
/*     */   private boolean hadTarget;
/*     */   
/*     */   private ClientTheme theme;
/*     */   
/*     */   private FixedRotations fixedRotations;
/*     */   
/*     */   private double random;
/*     */   
/*     */   private boolean attackNextTick;
/*     */   
/*     */   private double rotSpeed;
/*     */   
/*     */   private boolean done;
/*     */   
/*     */   public static boolean blocking;
/*     */   
/*     */   public static boolean fakeBlocking;
/*     */   private int autoblockTicks;
/*     */   private int attackCounter;
/*     */   private Antibot antibotModule;
/*     */   private Teams teamsModule;
/*     */   private Speed speedModule;
/*     */   private Scaffold scaffoldModule;
/*     */   private Breaker breakerModule;
/*     */   private Antivoid antivoidModule;
/*     */   private Velocity velocityModule;
/*     */   private boolean couldBlock;
/*     */   private boolean blinking;
/*     */   private int lastSlot;
/* 135 */   private final TimerUtil attackTimer = new TimerUtil();
/*     */   
/*     */   public Killaura() {
/* 138 */     super("Killaura", Category.COMBAT);
/* 139 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.mode, (AbstractSetting)this.filter, (AbstractSetting)this.rotations, (AbstractSetting)this.randomAmount, (AbstractSetting)this.startingRange, (AbstractSetting)this.range, (AbstractSetting)this.rotationRange, (AbstractSetting)this.raycast, (AbstractSetting)this.attackDelayMode, (AbstractSetting)this.minAPS, (AbstractSetting)this.maxAPS, (AbstractSetting)this.attackDelay, (AbstractSetting)this.failRate, (AbstractSetting)this.hurtTime, (AbstractSetting)this.autoblock, (AbstractSetting)this.noHitOnFirstTick, (AbstractSetting)this.blockTiming, (AbstractSetting)this.blinkTicks, (AbstractSetting)this.blockHurtTime, (AbstractSetting)this.whileTargetNotLooking, (AbstractSetting)this.slowdown, (AbstractSetting)this.whileHitting, (AbstractSetting)this.whileSpeedEnabled, (AbstractSetting)this.moveFix, (AbstractSetting)this.delayTransactions, (AbstractSetting)this.whileInventoryOpened, (AbstractSetting)this.whileScaffoldEnabled, (AbstractSetting)this.whileUsingBreaker, (AbstractSetting)this.players, (AbstractSetting)this.animals, (AbstractSetting)this.monsters, (AbstractSetting)this.invisibles, (AbstractSetting)this.attackDead, (AbstractSetting)this.visualiseTarget });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 144 */     this.fixedRotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
/*     */     
/* 146 */     this.rotSpeed = 15.0D;
/* 147 */     this.done = false;
/*     */     
/* 149 */     this.random = 0.5D;
/*     */     
/* 151 */     this.attackNextTick = false;
/*     */     
/* 153 */     this.couldBlock = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 158 */     fakeBlocking = false;
/* 159 */     if (mc.thePlayer != null) {
/* 160 */       if (this.hadTarget && this.rotations.is("Smooth")) {
/* 161 */         mc.thePlayer.rotationYaw = this.fixedRotations.getYaw();
/*     */       }
/*     */       
/* 164 */       stopTargeting();
/*     */     } 
/*     */     
/* 167 */     Acrimony.instance.getSlotSpoofHandler().stopSpoofing();
/* 168 */     Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*     */   }
/*     */   
/*     */   private void stopTargeting() {
/* 172 */     this.target = null;
/*     */     
/* 174 */     releaseBlocking();
/*     */     
/* 176 */     this.hadTarget = false;
/* 177 */     this.attackCounter = this.attackDelay.getValue();
/*     */     
/* 179 */     this.attackNextTick = false;
/*     */     
/* 181 */     if (this.delayTransactions.isEnabled()) {
/* 182 */       Acrimony.instance.getPacketDelayHandler().stopAll();
/*     */     }
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onRender(RenderEvent event) {
/* 188 */     if (mc.thePlayer == null || mc.thePlayer.ticksExisted < 10) {
/* 189 */       setEnabled(false);
/*     */       
/*     */       return;
/*     */     } 
/* 193 */     if (this.target != null && this.attackDelayMode.is("APS")) {
/* 194 */       long delay1 = (long)(1000.0D / this.minAPS.getValue());
/* 195 */       long delay2 = (long)(1000.0D / this.maxAPS.getValue());
/*     */       
/* 197 */       delay1 = Math.max(delay1, delay2);
/*     */       
/* 199 */       long delay = (long)(delay2 + (delay1 - delay2) * this.random);
/*     */       
/* 201 */       if (this.attackTimer.getTimeElapsed() >= delay) {
/* 202 */         this.attackNextTick = true;
/* 203 */         this.attackTimer.reset();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClientStarted() {
/* 210 */     this.antibotModule = (Antibot)Acrimony.instance.getModuleManager().getModule(Antibot.class);
/* 211 */     this.speedModule = (Speed)Acrimony.instance.getModuleManager().getModule(Speed.class);
/* 212 */     this.teamsModule = (Teams)Acrimony.instance.getModuleManager().getModule(Teams.class);
/* 213 */     this.scaffoldModule = (Scaffold)Acrimony.instance.getModuleManager().getModule(Scaffold.class);
/*     */     
/* 215 */     this.breakerModule = (Breaker)Acrimony.instance.getModuleManager().getModule(Breaker.class);
/* 216 */     this.antivoidModule = (Antivoid)Acrimony.instance.getModuleManager().getModule(Antivoid.class);
/* 217 */     this.velocityModule = (Velocity)Acrimony.instance.getModuleManager().getModule(Velocity.class);
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onTick(TickEvent event) {
/* 222 */     if (mc.thePlayer.ticksExisted < 10) {
/* 223 */       setEnabled(false);
/*     */       
/*     */       return;
/*     */     } 
/* 227 */     this.random = Math.random();
/*     */     
/* 229 */     switch (this.mode.getMode()) {
/*     */       case "Single":
/* 231 */         if (this.target == null || !canAttack(this.target)) {
/* 232 */           this.target = findTarget(true);
/*     */         }
/*     */         break;
/*     */       case "Switch":
/* 236 */         this.target = findTarget(true);
/*     */         break;
/*     */       case "Fast Switch":
/* 239 */         this.target = findTarget(false);
/*     */         break;
/*     */     } 
/*     */     
/* 243 */     getRotations();
/*     */     
/* 245 */     boolean inventoryOpened = (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiContainer && !this.whileInventoryOpened.isEnabled());
/* 246 */     boolean scaffoldEnabled = (this.scaffoldModule.isEnabled() && !this.whileScaffoldEnabled.isEnabled());
/* 247 */     boolean usingBreaker = (this.breakerModule.isEnabled() && this.breakerModule.isBreakingBed() && !this.whileUsingBreaker.isEnabled());
/*     */     
/* 249 */     if (this.target == null || inventoryOpened || scaffoldEnabled || usingBreaker) {
/* 250 */       stopTargeting();
/* 251 */       this.couldBlock = false;
/* 252 */       fakeBlocking = false;
/*     */       
/*     */       return;
/*     */     } 
/* 256 */     boolean attackTick = false;
/*     */     
/* 258 */     if (getDistanceToEntity(this.target) <= (this.hadTarget ? this.range.getValue() : this.startingRange.getValue())) {
/* 259 */       if (this.target.hurtTime <= this.hurtTime.getValue()) {
/* 260 */         switch (this.attackDelayMode.getMode()) {
/*     */           case "APS":
/* 262 */             if (!this.hadTarget) {
/* 263 */               attackTick = true;
/* 264 */               this.attackTimer.reset(); break;
/* 265 */             }  if (this.attackNextTick) {
/* 266 */               attackTick = true;
/* 267 */               this.attackNextTick = false;
/*     */             } 
/*     */             break;
/*     */           case "Delay in ticks":
/* 271 */             if (++this.attackCounter >= this.attackDelay.getValue()) {
/* 272 */               attackTick = true;
/*     */             }
/*     */             break;
/*     */         } 
/*     */       
/*     */       }
/* 278 */       if (this.delayTransactions.isEnabled()) {
/* 279 */         Acrimony.instance.getPacketDelayHandler().startDelayingPing(2000L);
/*     */       }
/*     */       
/* 282 */       this.hadTarget = true;
/*     */     } else {
/* 284 */       this.hadTarget = false;
/*     */     } 
/*     */     
/* 287 */     boolean shouldBlock = canBlock();
/* 288 */     this.couldBlock = shouldBlock;
/*     */     
/* 290 */     if (shouldBlock) {
/* 291 */       fakeBlocking = true;
/* 292 */       if (!autoblockAllowAttack()) {
/* 293 */         attackTick = false;
/*     */       }
/*     */       
/* 296 */       beforeAttackAutoblock(attackTick);
/*     */     } else {
/* 298 */       if (blocking) {
/* 299 */         attackTick = false;
/*     */       }
/*     */       
/* 302 */       releaseBlocking();
/*     */     } 
/*     */     
/* 305 */     if (attackTick) {
/* 306 */       boolean canAttack = true;
/*     */       
/* 308 */       if (!this.raycast.is("Disabled"))
/*     */       {
/*     */         
/* 311 */         canAttack = this.raycast.is("Legit") ? RotationsUtil.raycastEntity(this.target, this.fixedRotations.getYaw(), this.fixedRotations.getPitch(), this.fixedRotations.getLastYaw(), this.fixedRotations.getLastPitch(), this.range.getValue() + 0.3D) : RotationsUtil.raycastEntity(this.target, this.fixedRotations.getYaw(), this.fixedRotations.getPitch(), this.fixedRotations.getYaw(), this.fixedRotations.getPitch(), this.range.getValue() + 0.3D);
/*     */       }
/*     */       
/* 314 */       double aaa = this.failRate.getValue() / 100.0D;
/*     */       
/* 316 */       if (Math.random() > 1.0D - aaa) {
/* 317 */         canAttack = false;
/*     */       }
/*     */       
/* 320 */       if (canAttack) {
/* 321 */         AttackOrder.sendFixedAttack((EntityPlayer)mc.thePlayer, (Entity)this.target);
/*     */       }
/*     */       
/* 324 */       this.attackCounter = 0;
/*     */     } 
/*     */     
/* 327 */     if (shouldBlock) {
/* 328 */       afterAttackAutoblock(attackTick);
/*     */     } else {
/* 330 */       releaseBlocking();
/* 331 */       Acrimony.instance.getPacketBlinkHandler().releasePackets();
/* 332 */       this.autoblockTicks = 0;
/*     */     } 
/*     */     
/* 335 */     mc.gameSettings.keyBindAttack.pressed = false;
/*     */     
/* 337 */     if (!this.autoblock.is("None") && !this.autoblock.is("Blink")) {
/* 338 */       mc.gameSettings.keyBindUseItem.pressed = false;
/*     */     }
/*     */     
/* 341 */     if (!this.rotations.is("None") && isRotating() && this.moveFix.is("Silent")) {
/* 342 */       float diff = MathHelper.wrapAngleTo180_float(MathHelper.wrapAngleTo180_float(this.fixedRotations.getYaw()) - MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection())) + 22.5F;
/*     */       
/* 344 */       if (diff < 0.0F) {
/* 345 */         diff = 360.0F + diff;
/*     */       }
/*     */       
/* 348 */       int a = (int)(diff / 45.0D);
/*     */       
/* 350 */       float value = (mc.thePlayer.moveForward != 0.0F) ? Math.abs(mc.thePlayer.moveForward) : Math.abs(mc.thePlayer.moveStrafing);
/*     */       
/* 352 */       float forward = value;
/* 353 */       float strafe = 0.0F;
/*     */       
/* 355 */       for (int i = 0; i < 8 - a; i++) {
/* 356 */         float[] dirs = MovementUtil.incrementMoveDirection(forward, strafe);
/*     */         
/* 358 */         forward = dirs[0];
/* 359 */         strafe = dirs[1];
/*     */       } 
/*     */       
/* 362 */       if (forward < 0.8F) {
/* 363 */         mc.gameSettings.keyBindSprint.pressed = false;
/* 364 */         mc.thePlayer.setSprinting(false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onSlowdown(SlowdownEvent event) {
/* 371 */     if (canBlock()) {
/* 372 */       switch (this.autoblock.getMode()) {
/*     */         case "Blink":
/* 374 */           switch (this.slowdown.getMode()) {
/*     */             case "Onground":
/* 376 */               if (!mc.thePlayer.onGround) {
/* 377 */                 event.setAllowedSprinting(true);
/* 378 */                 event.setForward(1.0F);
/* 379 */                 event.setStrafe(1.0F);
/*     */               } 
/*     */               break;
/*     */             case "Offground":
/* 383 */               if (mc.thePlayer.onGround) {
/* 384 */                 event.setAllowedSprinting(true);
/* 385 */                 event.setForward(1.0F);
/* 386 */                 event.setStrafe(1.0F);
/*     */               } 
/*     */               break;
/*     */             case "Disabled":
/* 390 */               event.setAllowedSprinting(true);
/* 391 */               event.setForward(1.0F);
/* 392 */               event.setStrafe(1.0F);
/*     */               break;
/*     */           } 
/*     */           break;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onJump(JumpEvent event) {
/* 402 */     if (this.target != null && !this.rotations.is("None") && !this.moveFix.is("Disabled")) {
/* 403 */       event.setYaw(this.fixedRotations.getYaw());
/*     */     }
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onStrafe(StrafeEvent event) {
/* 409 */     if (!this.rotations.is("None") && isRotating()) {
/* 410 */       float diff; int a; float value; float forward; float strafe; int i; switch (this.moveFix.getMode()) {
/*     */         case "Normal":
/* 412 */           event.setYaw(this.fixedRotations.getYaw());
/*     */           break;
/*     */         case "Silent":
/* 415 */           event.setYaw(this.fixedRotations.getYaw());
/*     */           
/* 417 */           diff = MathHelper.wrapAngleTo180_float(MathHelper.wrapAngleTo180_float(this.fixedRotations.getYaw()) - MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection())) + 22.5F;
/*     */           
/* 419 */           if (diff < 0.0F) {
/* 420 */             diff = 360.0F + diff;
/*     */           }
/*     */           
/* 423 */           a = (int)(diff / 45.0D);
/*     */           
/* 425 */           value = (event.getForward() != 0.0F) ? Math.abs(event.getForward()) : Math.abs(event.getStrafe());
/*     */           
/* 427 */           forward = value;
/* 428 */           strafe = 0.0F;
/*     */           
/* 430 */           for (i = 0; i < 8 - a; i++) {
/* 431 */             float[] dirs = MovementUtil.incrementMoveDirection(forward, strafe);
/*     */             
/* 433 */             forward = dirs[0];
/* 434 */             strafe = dirs[1];
/*     */           } 
/*     */           
/* 437 */           event.setForward(forward);
/* 438 */           event.setStrafe(strafe);
/*     */           break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean canRenderBlocking() {
/* 445 */     return (canBlock() || this.autoblock.is("Fake"));
/*     */   }
/*     */   
/*     */   private boolean canBlock() {
/* 449 */     fakeBlocking = true;
/* 450 */     ItemStack stack = mc.thePlayer.getHeldItem();
/*     */     
/* 452 */     if (this.autoblock.is("Blink")) {
/* 453 */       if (this.antivoidModule.isBlinking()) {
/* 454 */         return false;
/*     */       }
/*     */       
/* 457 */       if (mc.thePlayer.hurtTime > this.blockHurtTime.getValue()) {
/* 458 */         return false;
/*     */       }
/*     */       
/* 461 */       if (this.target != null && !this.whileTargetNotLooking.isEnabled()) {
/* 462 */         float targetYaw = MathHelper.wrapAngleTo180_float(this.target.rotationYaw);
/* 463 */         float diff = Math.abs(MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - targetYaw);
/*     */         
/* 465 */         boolean targetLooking = ((diff > 90.0F && diff < 270.0F) || mc.thePlayer.getDistanceToEntity((Entity)this.target) < 1.3D);
/*     */         
/* 467 */         if (!targetLooking) {
/* 468 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 473 */     if (this.autoblock.is("Spoof") || this.autoblock.is("Spoof2")) {
/* 474 */       if (mc.thePlayer.hurtTime > this.blockHurtTime.getValue()) {
/* 475 */         return false;
/*     */       }
/*     */       
/* 478 */       if (this.autoblock.is("Spoof2") && this.target != null) {
/* 479 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 483 */     return (this.target != null && stack != null && stack.getItem() instanceof net.minecraft.item.ItemSword && (this.whileSpeedEnabled.isEnabled() || !((Speed)Acrimony.instance.getModuleManager().getModule(Speed.class)).isEnabled()));
/*     */   }
/*     */   
/*     */   private void beforeAttackAutoblock(boolean attackTick) {
/* 487 */     int slot = mc.thePlayer.inventory.currentItem;
/*     */     
/* 489 */     switch (this.autoblock.getMode()) {
/*     */       case "Vanilla":
/* 491 */         if (!blocking) {
/* 492 */           PacketUtil.sendBlocking(true, false);
/* 493 */           blocking = true;
/*     */         } 
/*     */         
/* 496 */         this.autoblockTicks++;
/*     */         break;
/*     */       case "NCP":
/* 499 */         if (blocking) {
/* 500 */           PacketUtil.releaseUseItem(true);
/* 501 */           blocking = false;
/*     */         } 
/*     */         break;
/*     */       case "Spoof":
/* 505 */         PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange((slot < 8) ? (slot + 1) : 0));
/* 506 */         PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(slot));
/*     */         
/* 508 */         if (this.blockTiming.is("Pre")) {
/* 509 */           PacketUtil.sendBlocking(true, false);
/* 510 */           blocking = true;
/*     */         } 
/*     */         break;
/*     */       case "Spoof2":
/* 514 */         if (this.autoblockTicks >= 2) {
/* 515 */           mc.thePlayer.inventory.currentItem = this.lastSlot;
/* 516 */           mc.playerController.syncCurrentPlayItem();
/* 517 */           Acrimony.instance.getSlotSpoofHandler().stopSpoofing();
/*     */           
/* 519 */           if (this.blinking) {
/* 520 */             Acrimony.instance.getPacketBlinkHandler().releasePackets();
/*     */           }
/* 522 */           this.autoblockTicks = 0;
/*     */         } 
/*     */         
/* 525 */         if (this.autoblockTicks == 0) {
/* 526 */           if (this.blockTiming.is("Pre")) {
/* 527 */             PacketUtil.sendBlocking(true, false);
/* 528 */             blocking = true;
/*     */           }  break;
/* 530 */         }  if (this.autoblockTicks == 1) {
/* 531 */           if (!this.velocityModule.isEnabled() || !this.mode.is("Hypixel") || !this.speedModule.isEnabled()) {
/* 532 */             Acrimony.instance.getPacketBlinkHandler().startBlinking();
/* 533 */             this.blinking = true;
/*     */           } 
/*     */           
/* 536 */           this.lastSlot = slot;
/*     */           
/* 538 */           Acrimony.instance.getSlotSpoofHandler().startSpoofing(slot);
/* 539 */           mc.thePlayer.inventory.currentItem = (slot < 8) ? (slot + 1) : 0;
/*     */         } 
/*     */         break;
/*     */       case "Blink":
/* 543 */         if (this.autoblockTicks > 0 && this.autoblockTicks < this.blinkTicks.getValue()) {
/* 544 */           mc.gameSettings.keyBindUseItem.pressed = false;
/*     */         }
/*     */         
/* 547 */         if (this.autoblockTicks == this.blinkTicks.getValue() || this.autoblockTicks == 0) {
/* 548 */           mc.gameSettings.keyBindUseItem.pressed = true;
/*     */           
/* 550 */           this.autoblockTicks = 0;
/*     */         } 
/* 552 */         fakeBlocking = true;
/* 553 */         blocking = true;
/*     */         break;
/*     */       case "Not moving":
/* 556 */         if (MovementUtil.isMoving() || (this.target.hurtTime < this.hurtTime.getValue() + 1 && !this.whileHitting.isEnabled())) {
/* 557 */           mc.gameSettings.keyBindUseItem.pressed = false;
/* 558 */           blocking = false;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */   private void afterAttackAutoblock(boolean attackTick) {
/*     */     PacketWrapper useItem;
/* 565 */     switch (this.autoblock.getMode()) {
/*     */       case "AAC5":
/* 567 */         PacketUtil.sendBlocking(true, false);
/* 568 */         blocking = true;
/*     */         break;
/*     */       case "Watchdog":
/* 571 */         if (this.autoblockTicks % 5 != 0) {
/* 572 */           Acrimony.instance.getPacketBlinkHandler().startBlinking();
/*     */         } else {
/* 574 */           Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*     */         } 
/* 576 */         PacketUtil.sendPacketNoEvent((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
/* 577 */         useItem = PacketWrapper.create(29, null, Via.getManager().getConnectionManager().getConnections().iterator().next());
/* 578 */         useItem.write((Type)Type.VAR_INT, Integer.valueOf(1));
/* 579 */         PacketUtil.sendToServer(useItem, Protocol1_8To1_9.class, true, true);
/* 580 */         blocking = true;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void postAutoblock() {
/* 586 */     switch (this.autoblock.getMode()) {
/*     */       case "NCP":
/* 588 */         if (!blocking) {
/* 589 */           PacketUtil.sendBlocking(true, false);
/* 590 */           blocking = true;
/*     */         } 
/*     */         break;
/*     */       case "Spoof":
/* 594 */         if (this.blockTiming.is("Post")) {
/* 595 */           PacketUtil.sendBlocking(true, false);
/* 596 */           blocking = true;
/*     */         } 
/*     */         break;
/*     */       case "Spoof2":
/* 600 */         if (this.blockTiming.is("Post")) {
/* 601 */           PacketUtil.sendBlocking(true, false);
/* 602 */           blocking = true;
/*     */         } 
/*     */         
/* 605 */         this.autoblockTicks++;
/*     */         break;
/*     */       case "Not moving":
/* 608 */         if (!MovementUtil.isMoving() && (this.target.hurtTime >= this.hurtTime.getValue() + 1 || this.whileHitting.isEnabled())) {
/* 609 */           mc.gameSettings.keyBindUseItem.pressed = true;
/* 610 */           blocking = true;
/*     */         } 
/*     */         break;
/*     */       case "Blink":
/* 614 */         if (this.target == null) {
/* 615 */           LogUtil.addChatMessage("Autoblock test 2");
/*     */         }
/*     */         
/* 618 */         if (this.autoblockTicks == 0) {
/* 619 */           Acrimony.instance.getPacketBlinkHandler().releasePackets();
/* 620 */           Acrimony.instance.getPacketBlinkHandler().startBlinking();
/*     */         } 
/*     */         
/* 623 */         this.autoblockTicks++;
/* 624 */         this.blinking = true;
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean autoblockAllowAttack() {
/* 630 */     switch (this.autoblock.getMode()) {
/*     */       case "Vanilla":
/* 632 */         return this.noHitOnFirstTick.isEnabled() ? ((this.autoblockTicks > 1)) : true;
/*     */       case "Spoof2":
/* 634 */         return (this.autoblockTicks == 2);
/*     */       case "Blink":
/* 636 */         return (this.autoblockTicks >= 2 && this.autoblockTicks < this.blinkTicks.getValue());
/*     */     } 
/*     */     
/* 639 */     return true;
/*     */   }
/*     */   
/*     */   private void releaseBlocking() {
/* 643 */     fakeBlocking = false;
/* 644 */     ItemStack stack = mc.thePlayer.getHeldItem();
/*     */     
/* 646 */     if (this.hadTarget && this.autoblock.is("Blink") && !blocking && this.target == null) {
/* 647 */       LogUtil.addChatMessage("Autoblock test : " + Acrimony.instance.getPacketBlinkHandler().isBlinking());
/*     */     }
/*     */     
/* 650 */     int slot = mc.thePlayer.inventory.currentItem;
/*     */     
/* 652 */     if (blocking) {
/* 653 */       switch (this.autoblock.getMode()) {
/*     */         case "Vanilla":
/*     */         case "NCP":
/*     */         case "AAC5":
/* 657 */           if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemSword) {
/* 658 */             PacketUtil.releaseUseItem(true);
/*     */           }
/*     */           break;
/*     */         case "Spoof":
/* 662 */           PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange((slot < 8) ? (slot + 1) : 0));
/* 663 */           PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(slot));
/*     */           break;
/*     */         case "Watchdog":
/* 666 */           mc.gameSettings.keyBindUseItem.pressed = false;
/* 667 */           mc.getNetHandler().getNetworkManager().sendPacketNoEvent((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
/*     */           break;
/*     */         case "Spoof2":
/* 670 */           if (this.autoblockTicks == 1) {
/* 671 */             mc.thePlayer.inventory.currentItem = (this.lastSlot < 8) ? (this.lastSlot + 1) : 0;
/*     */             
/* 673 */             (new Thread(() -> {
/*     */                   try {
/*     */                     Thread.sleep(40L);
/* 676 */                   } catch (InterruptedException e) {
/*     */                     e.printStackTrace();
/*     */                   } 
/*     */                   mc.thePlayer.inventory.currentItem = this.lastSlot;
/*     */                   mc.playerController.syncCurrentPlayItem();
/* 681 */                 })).start();
/*     */           } else {
/* 683 */             mc.thePlayer.inventory.currentItem = this.lastSlot;
/*     */           } 
/*     */           
/* 686 */           mc.playerController.syncCurrentPlayItem();
/*     */           
/* 688 */           Acrimony.instance.getSlotSpoofHandler().stopSpoofing();
/*     */           
/* 690 */           if (this.blinking) {
/* 691 */             Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/* 692 */             this.blinking = false;
/*     */           } 
/*     */           break;
/*     */         case "Not moving":
/* 696 */           mc.gameSettings.keyBindUseItem.pressed = false;
/*     */           break;
/*     */       } 
/*     */       
/* 700 */       blocking = false;
/*     */     } 
/*     */     
/* 703 */     if (this.autoblock.is("Blink") && (this.blinking || blocking)) {
/* 704 */       Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/* 705 */       this.blinking = false;
/* 706 */       blocking = false;
/* 707 */       mc.gameSettings.keyBindUseItem.pressed = false;
/*     */     } 
/*     */     
/* 710 */     this.autoblockTicks = 0;
/* 711 */     fakeBlocking = false;
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onItemRender(ItemRenderEvent event) {
/* 716 */     if (canRenderBlocking() && (blocking || !this.autoblock.is("Not moving")) && !this.autoblock.is("None")) {
/* 717 */       event.setRenderBlocking(true);
/*     */     }
/*     */   }
/*     */   
/*     */   private void getRotations() {
/* 722 */     float yaw = this.fixedRotations.getYaw();
/* 723 */     float pitch = this.fixedRotations.getPitch();
/*     */     
/* 725 */     if (this.target != null) {
/* 726 */       float yaw1, diff; double amount; float currentYaw, rots[] = RotationsUtil.getRotationsToEntity(this.target, false);
/*     */       
/* 728 */       if (this.speedModule.isEnabled() && this.speedModule.mode.is("Pathfind")) {
/* 729 */         rots = RotationsUtil.getRotationsToEntity(this.speedModule.getActualX(), this.speedModule.getActualY(), this.speedModule.getActualZ(), this.target, false);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 735 */       switch (this.rotations.getMode()) {
/*     */         case "Normal":
/* 737 */           yaw = rots[0];
/* 738 */           pitch = rots[1];
/*     */           break;
/*     */         case "Randomised":
/* 741 */           amount = this.randomAmount.getValue();
/*     */           
/* 743 */           yaw = (float)(rots[0] + Math.random() * amount - amount / 2.0D);
/* 744 */           pitch = (float)(rots[1] + Math.random() * amount - amount / 2.0D);
/*     */           break;
/*     */         case "Smooth":
/* 747 */           yaw1 = rots[0];
/* 748 */           currentYaw = MathHelper.wrapAngleTo180_float(yaw);
/*     */           
/* 750 */           diff = Math.abs(currentYaw - yaw1);
/*     */           
/* 752 */           if (diff >= 8.0F) {
/* 753 */             if (diff > 35.0F) {
/* 754 */               this.rotSpeed += 4.0D - Math.random();
/*     */               
/* 756 */               this.rotSpeed = Math.max(this.rotSpeed, (float)(31.0D - Math.random()));
/*     */             } else {
/* 758 */               this.rotSpeed -= 6.5D - Math.random();
/*     */               
/* 760 */               this.rotSpeed = Math.max(this.rotSpeed, (float)(14.0D - Math.random()));
/*     */             } 
/*     */             
/* 763 */             if (diff <= 180.0F) {
/* 764 */               if (currentYaw > yaw1) {
/* 765 */                 yaw = (float)(yaw - this.rotSpeed);
/*     */               } else {
/* 767 */                 yaw = (float)(yaw + this.rotSpeed);
/*     */               }
/*     */             
/* 770 */             } else if (currentYaw > yaw1) {
/* 771 */               yaw = (float)(yaw + this.rotSpeed);
/*     */             } else {
/* 773 */               yaw = (float)(yaw - this.rotSpeed);
/*     */             }
/*     */           
/*     */           }
/* 777 */           else if (currentYaw > yaw1) {
/* 778 */             yaw = (float)(yaw - diff * 0.8D);
/*     */           } else {
/* 780 */             yaw = (float)(yaw + diff * 0.8D);
/*     */           } 
/*     */ 
/*     */           
/* 784 */           yaw = (float)(yaw + Math.random() * 0.7D - 0.35D);
/* 785 */           pitch = (float)(mc.thePlayer.rotationPitch + (rots[1] - mc.thePlayer.rotationPitch) * 0.6D);
/* 786 */           pitch = (float)(pitch + Math.random() * 0.5D - 0.25D);
/*     */           
/* 788 */           this.done = false;
/*     */           break;
/*     */       } 
/*     */     } else {
/* 792 */       switch (this.rotations.getMode()) {
/*     */         case "Smooth":
/* 794 */           this.rotSpeed = 15.0D;
/*     */           
/* 796 */           if (!this.hadTarget) {
/* 797 */             this.done = true;
/*     */           }
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/* 803 */     this.fixedRotations.updateRotations(yaw, pitch);
/*     */   }
/*     */   
/*     */   private boolean isRotating() {
/* 807 */     switch (this.rotations.getMode()) {
/*     */       case "Normal":
/*     */       case "Randomised":
/* 810 */         return (this.target != null);
/*     */       case "Smooth":
/* 812 */         return (this.target != null || !this.done);
/*     */       case "None":
/* 814 */         return false;
/*     */     } 
/*     */     
/* 817 */     return false;
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onMotion(MotionEvent event) {
/* 822 */     if (isRotating()) {
/* 823 */       event.setYaw(this.fixedRotations.getYaw());
/* 824 */       event.setPitch(this.fixedRotations.getPitch());
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onPostMotion(PostMotionEvent event) {
/* 830 */     if (this.couldBlock) {
/* 831 */       postAutoblock();
/*     */     }
/*     */   }
/*     */   
/*     */   public EntityLivingBase findTarget(boolean allowSame) {
/* 836 */     return findTarget(allowSame, this.rotationRange.getValue());
/*     */   }
/*     */   
/*     */   public EntityLivingBase findTarget(boolean allowSame, double range) {
/* 840 */     ArrayList<EntityLivingBase> entities = new ArrayList<>();
/* 841 */     for (Entity entity : mc.theWorld.loadedEntityList) {
/* 842 */       if (entity instanceof EntityLivingBase && entity != mc.thePlayer && 
/* 843 */         canAttack((EntityLivingBase)entity, range)) {
/* 844 */         entities.add((EntityLivingBase)entity);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 849 */     if (entities != null && entities.size() > 0) {
/* 850 */       switch (this.filter.getMode()) {
/*     */         case "Range":
/* 852 */           entities.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity((Entity)mc.thePlayer)));
/*     */           break;
/*     */         case "Health":
/* 855 */           entities.sort(Comparator.comparingDouble(entity -> entity.getHealth()));
/*     */           break;
/*     */       } 
/*     */       
/* 859 */       if (!allowSame && entities.size() > 1 && entities.get(0) == this.target) {
/* 860 */         return entities.get(1);
/*     */       }
/* 862 */       return entities.get(0);
/*     */     } 
/*     */ 
/*     */     
/* 866 */     return null;
/*     */   }
/*     */   
/*     */   public boolean canAttack(EntityLivingBase entity) {
/* 870 */     return canAttack(entity, this.rotationRange.getValue());
/*     */   }
/*     */   
/*     */   public boolean canAttack(EntityLivingBase entity, double range) {
/* 874 */     if (getDistanceToEntity(entity) > range) {
/* 875 */       return false;
/*     */     }
/*     */     
/* 878 */     if ((entity.isInvisible() || entity.isInvisibleToPlayer((EntityPlayer)mc.thePlayer)) && !this.invisibles.isEnabled()) {
/* 879 */       return false;
/*     */     }
/*     */     
/* 882 */     if (entity instanceof EntityPlayer && (
/* 883 */       !this.players.isEnabled() || !this.teamsModule.canAttack((EntityPlayer)entity))) {
/* 884 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 888 */     if (entity instanceof net.minecraft.entity.passive.EntityAnimal && !this.animals.isEnabled()) {
/* 889 */       return false;
/*     */     }
/*     */     
/* 892 */     if (entity instanceof net.minecraft.entity.monster.EntityMob && !this.monsters.isEnabled()) {
/* 893 */       return false;
/*     */     }
/*     */     
/* 896 */     if (!(entity instanceof EntityPlayer) && !(entity instanceof net.minecraft.entity.passive.EntityAnimal) && !(entity instanceof net.minecraft.entity.monster.EntityMob)) {
/* 897 */       return false;
/*     */     }
/*     */     
/* 900 */     if (entity.isDead && !this.attackDead.isEnabled()) {
/* 901 */       return false;
/*     */     }
/*     */     
/* 904 */     if (!this.antibotModule.canAttack(entity, this)) {
/* 905 */       return false;
/*     */     }
/*     */     
/* 908 */     return true;
/*     */   }
/*     */   
/*     */   public double getDistanceToEntity(EntityLivingBase entity) {
/* 912 */     Vec3 playerVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
/*     */     
/* 914 */     if (this.speedModule.isEnabled() && this.speedModule.mode.is("Pathfind")) {
/* 915 */       playerVec = new Vec3(this.speedModule.getActualX(), this.speedModule.getActualY() + mc.thePlayer.getEyeHeight(), this.speedModule.getActualZ());
/*     */     }
/*     */     
/* 918 */     double yDiff = mc.thePlayer.posY - entity.posY;
/*     */     
/* 920 */     double targetY = (yDiff > 0.0D) ? (entity.posY + entity.getEyeHeight()) : ((-yDiff < mc.thePlayer.getEyeHeight()) ? (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) : entity.posY);
/*     */     
/* 922 */     Vec3 targetVec = new Vec3(entity.posX, targetY, entity.posZ);
/*     */     
/* 924 */     return playerVec.distanceTo(targetVec) - 0.30000001192092896D;
/*     */   }
/*     */   
/*     */   public double getDistanceCustomPosition(double x, double y, double z, double eyeHeight) {
/* 928 */     Vec3 playerVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
/*     */     
/* 930 */     double yDiff = mc.thePlayer.posY - y;
/*     */     
/* 932 */     double targetY = (yDiff > 0.0D) ? (y + eyeHeight) : ((-yDiff < mc.thePlayer.getEyeHeight()) ? (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) : y);
/*     */     
/* 934 */     Vec3 targetVec = new Vec3(x, targetY, z);
/*     */     
/* 936 */     return playerVec.distanceTo(targetVec) - 0.30000001192092896D;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSuffix() {
/* 941 */     return this.mode.getMode() + "," + this.autoblock.getMode();
/*     */   }
/*     */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\combat\Killaura.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */