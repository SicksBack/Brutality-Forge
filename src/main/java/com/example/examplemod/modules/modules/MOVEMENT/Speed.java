/*     */ package Acrimony.module.impl.movement;
/*     */ import Acrimony.event.Listener;
/*     */ import Acrimony.event.impl.EntityActionEvent;
/*     */ import Acrimony.event.impl.JumpEvent;
/*     */ import Acrimony.event.impl.MotionEvent;
/*     */ import Acrimony.event.impl.MoveEvent;
/*     */ import Acrimony.event.impl.PacketReceiveEvent;
/*     */ import Acrimony.event.impl.Render3DEvent;
/*     */ import Acrimony.event.impl.StrafeEvent;
/*     */ import Acrimony.event.impl.UpdateEvent;
/*     */ import Acrimony.module.Category;
/*     */ import Acrimony.setting.AbstractSetting;
/*     */ import Acrimony.setting.impl.BooleanSetting;
/*     */ import Acrimony.setting.impl.DoubleSetting;
/*     */ import Acrimony.setting.impl.IntegerSetting;
/*     */ import Acrimony.setting.impl.ModeSetting;
/*     */ import Acrimony.util.network.PacketUtil;
/*     */ import Acrimony.util.player.MovementUtil;
/*     */ import Acrimony.util.player.PlayerUtil;
/*     */ import Acrimony.util.player.RotationsUtil;
/*     */ import Acrimony.util.render.RenderUtil;
/*     */ import java.util.ArrayList;
/*     */ import net.minecraft.block.Block;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.play.server.S12PacketEntityVelocity;
/*     */ import net.minecraft.potion.Potion;
/*     */ import net.minecraft.util.BlockPos;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.MathHelper;
/*     */ 
/*     */ public class Speed extends Module {
/*  32 */   public final ModeSetting mode = new ModeSetting("Mode", "Vanilla", new String[] { "Vanilla", "NCP", "Watchdog", "Blocksmc", "Strafe", "Fake strafe" });
/*     */   
/*  34 */   private final DoubleSetting vanillaSpeed = new DoubleSetting("Vanilla speed", () -> Boolean.valueOf(this.mode.is("Vanilla")), 1.0D, 0.2D, 9.0D, 0.1D);
/*  35 */   private final BooleanSetting autoJump = new BooleanSetting("Autojump", () -> Boolean.valueOf((this.mode.is("Vanilla") || this.mode.is("Strafe"))), true);
/*     */   
/*  37 */   private final ModeSetting ncpMode = new ModeSetting("NCP Mode", () -> Boolean.valueOf(this.mode.is("NCP")), "Hop", new String[] { "Hop", "Updated Hop" });
/*  38 */   private final BooleanSetting damageBoost = new BooleanSetting("Damage Boost", () -> Boolean.valueOf((this.mode.is("NCP") && this.ncpMode.is("Updated Hop"))), true);
/*     */   
/*  40 */   public final ModeSetting watchdogMode = new ModeSetting("Watchdog Mode", () -> Boolean.valueOf(this.mode.is("Watchdog")), "Strafe", new String[] { "Strafe", "Semi-Strafe", "Strafeless", "Ground" });
/*     */   
/*  42 */   private final BooleanSetting fast = new BooleanSetting("Fast", () -> Boolean.valueOf((this.mode.is("Watchdog") && (this.watchdogMode.is("Strafe") || this.watchdogMode.is("Strafeless")))), true);
/*     */   
/*  44 */   private final DoubleSetting attributeSpeedOffground = new DoubleSetting("Attribute speed offground", () -> Boolean.valueOf((this.mode.is("Watchdog") && this.watchdogMode.is("Strafe"))), 0.023D, 0.02D, 0.026D, 0.001D);
/*     */   
/*  46 */   private final DoubleSetting mult = new DoubleSetting("Mult", () -> Boolean.valueOf((this.mode.is("Watchdog") && this.watchdogMode.is("Strafeless") && this.fast.isEnabled())), 1.24D, 1.0D, 1.3D, 0.005D);
/*  47 */   private final DoubleSetting speedPotMult = new DoubleSetting("Speed pot mult", () -> Boolean.valueOf((this.mode.is("Watchdog") && this.watchdogMode.is("Strafeless") && this.fast.isEnabled())), 1.24D, 1.0D, 1.3D, 0.005D);
/*     */   
/*  49 */   private final BooleanSetting allDirSprint = new BooleanSetting("All directions sprint", () -> Boolean.valueOf(this.mode.is("Strafe")), true);
/*  50 */   private final IntegerSetting minHurtTime = new IntegerSetting("Min hurttime", () -> Boolean.valueOf(this.mode.is("Strafe")), 10, 0, 10, 1);
/*     */   
/*  52 */   private final BooleanSetting sprint = new BooleanSetting("Sprint", () -> Boolean.valueOf(this.mode.is("Fake strafe")), true);
/*  53 */   private final BooleanSetting rotate = new BooleanSetting("Rotate", () -> Boolean.valueOf(this.mode.is("Fake strafe")), false);
/*  54 */   private final BooleanSetting groundStrafe = new BooleanSetting("Ground Strafe", () -> Boolean.valueOf(this.mode.is("Fake strafe")), false);
/*  55 */   private final ModeSetting velocityMode = new ModeSetting("Velocity handling", () -> Boolean.valueOf(this.mode.is("Fake strafe")), "Ignore", new String[] { "Ignore", "Vertical", "Legit" });
/*  56 */   private final ModeSetting clientSpeed = new ModeSetting("Client speed", () -> Boolean.valueOf(this.mode.is("Fake strafe")), "Normal", new String[] { "Normal", "Custom" });
/*  57 */   private final DoubleSetting customClientSpeed = new DoubleSetting("Custom client speed", () -> Boolean.valueOf((this.mode.is("Fake strafe") && this.clientSpeed.is("Custom"))), 0.5D, 0.15D, 1.0D, 0.025D);
/*  58 */   private final BooleanSetting fakeFly = new BooleanSetting("Fake fly", () -> Boolean.valueOf(this.mode.is("Fake strafe")), false);
/*  59 */   private final BooleanSetting renderRealPosBox = new BooleanSetting("Render box at real pos", () -> Boolean.valueOf(this.mode.is("Fake strafe")), true);
/*     */   
/*  61 */   private final ModeSetting timerMode = new ModeSetting("Timer mode", () -> Boolean.valueOf(this.mode.is("NCP")), "None", new String[] { "None", "Bypass", "Custom" });
/*  62 */   private final DoubleSetting customTimer = new DoubleSetting("Custom timer", () -> Boolean.valueOf(((this.mode.is("NCP") && this.timerMode.is("Custom")) || this.mode.is("Watchdog"))), 1.0D, 0.1D, 3.0D, 0.05D);
/*     */   private double speed; private boolean prevOnGround; private int counter; private int ticks; private int offGroundTicks; private int ticksSinceVelocity; private boolean takingVelocity; private boolean wasTakingVelocity; private double velocityX; private double velocityY; private double velocityZ; private double velocityDist; private float lastDirection;
/*     */   private float lastYaw;
/*     */   private double motionX;
/*     */   private double motionY;
/*     */   private double motionZ;
/*     */   private double actualX;
/*     */   private double actualY;
/*     */   private double actualZ;
/*     */   private double lastActualX;
/*     */   private double lastActualY;
/*     */   private double lastActualZ;
/*     */   private boolean actualGround;
/*     */   private boolean started;
/*     */   private boolean firstJumpDone;
/*     */   private boolean wasCollided;
/*     */   private int oldSlot;
/*     */   
/*  80 */   public double getActualX() { return this.actualX; } public double getActualY() { return this.actualY; } public double getActualZ() { return this.actualZ; } public double getLastActualX() { return this.lastActualX; } public double getLastActualY() { return this.lastActualY; } public double getLastActualZ() { return this.lastActualZ; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  91 */   private final ArrayList<BlockPos> barriers = new ArrayList<>();
/*     */   private float lastForward;
/*     */   private float lastStrafe;
/*     */   
/*     */   public Speed() {
/*  96 */     super("Speed", Category.MOVEMENT);
/*  97 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.mode, (AbstractSetting)this.vanillaSpeed, (AbstractSetting)this.autoJump, (AbstractSetting)this.ncpMode, (AbstractSetting)this.damageBoost, (AbstractSetting)this.watchdogMode, (AbstractSetting)this.fast, (AbstractSetting)this.mult, (AbstractSetting)this.speedPotMult, (AbstractSetting)this.attributeSpeedOffground, (AbstractSetting)this.allDirSprint, (AbstractSetting)this.minHurtTime, (AbstractSetting)this.sprint, (AbstractSetting)this.rotate, (AbstractSetting)this.groundStrafe, (AbstractSetting)this.velocityMode, (AbstractSetting)this.clientSpeed, (AbstractSetting)this.customClientSpeed, (AbstractSetting)this.fakeFly, (AbstractSetting)this.renderRealPosBox, (AbstractSetting)this.timerMode, (AbstractSetting)this.customTimer });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/* 102 */     this.prevOnGround = false;
/* 103 */     this.speed = 0.28D;
/*     */     
/* 105 */     this.ticks = this.offGroundTicks = this.counter = 0;
/*     */     
/* 107 */     this.ticksSinceVelocity = Integer.MAX_VALUE;
/*     */     
/* 109 */     this.started = this.firstJumpDone = false;
/*     */     
/* 111 */     this.takingVelocity = this.wasTakingVelocity = false;
/*     */     
/* 113 */     this.motionX = mc.thePlayer.motionX;
/* 114 */     this.motionY = mc.thePlayer.motionY;
/* 115 */     this.motionZ = mc.thePlayer.motionZ;
/*     */     
/* 117 */     this.actualX = mc.thePlayer.posX;
/* 118 */     this.actualY = mc.thePlayer.posY;
/* 119 */     this.actualZ = mc.thePlayer.posZ;
/*     */     
/* 121 */     this.actualGround = mc.thePlayer.onGround;
/*     */     
/* 123 */     this.lastDirection = MovementUtil.getPlayerDirection();
/*     */     
/* 125 */     this.lastYaw = mc.thePlayer.rotationYaw;
/*     */     
/* 127 */     this.lastForward = mc.thePlayer.moveForward;
/* 128 */     this.lastStrafe = mc.thePlayer.moveStrafing;
/*     */     
/* 130 */     this.oldSlot = mc.thePlayer.inventory.currentItem;
/*     */     
/* 132 */     this.wasCollided = false;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/* 137 */     mc.timer.timerSpeed = 1.0F;
/*     */     
/* 139 */     switch (this.mode.getMode()) {
/*     */       case "Vulcan":
/* 141 */         mc.thePlayer.inventory.currentItem = this.oldSlot;
/*     */         break;
/*     */       case "Watchdog":
/* 144 */         if (this.watchdogMode.is("Strafe")) {
/* 145 */           mc.thePlayer.motionX *= 0.2D;
/* 146 */           mc.thePlayer.motionZ *= 0.2D;
/*     */         } 
/*     */         break;
/*     */       case "Fake strafe":
/* 150 */         mc.thePlayer.setPosition(this.actualX, this.actualY, this.actualZ);
/* 151 */         mc.thePlayer.motionX = this.motionX;
/* 152 */         mc.thePlayer.motionY = this.motionY;
/* 153 */         mc.thePlayer.motionZ = this.motionZ;
/*     */         
/* 155 */         mc.thePlayer.onGround = this.actualGround;
/*     */         break;
/*     */     } 
/*     */     
/* 159 */     if (!this.barriers.isEmpty()) {
/* 160 */       for (BlockPos pos : this.barriers) {
/* 161 */         mc.theWorld.setBlockToAir(pos);
/*     */       }
/*     */       
/* 164 */       this.barriers.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onStrafe(StrafeEvent event) {
/* 170 */     switch (this.mode.getMode()) {
/*     */       case "Watchdog":
/* 172 */         if (this.watchdogMode.is("Test")) {
/* 173 */           if (!mc.thePlayer.isSprinting()) {
/* 174 */             event.setAttributeSpeed(event.getAttributeSpeed() * 1.3F);
/*     */           }
/*     */           
/* 177 */           if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
/* 178 */             mc.thePlayer.jump();
/*     */           }
/*     */         } 
/*     */         break;
/*     */       case "Strafe":
/* 183 */         if (this.allDirSprint.isEnabled() && 
/* 184 */           !mc.thePlayer.isSprinting()) {
/* 185 */           event.setAttributeSpeed(event.getAttributeSpeed() * 1.3F);
/*     */         }
/*     */ 
/*     */         
/* 189 */         if (this.autoJump.isEnabled() && mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
/* 190 */           mc.thePlayer.jump();
/*     */         }
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onJump(JumpEvent event) {
/* 198 */     switch (this.mode.getMode()) {
/*     */       case "Strafe":
/* 200 */         if (this.allDirSprint.isEnabled()) {
/* 201 */           event.setBoosting(MovementUtil.isMoving());
/* 202 */           event.setYaw(MovementUtil.getPlayerDirection());
/*     */         } 
/*     */         break;
/*     */       case "Watchdog":
/* 206 */         if (this.watchdogMode.is("Test")) {
/* 207 */           event.setBoosting(MovementUtil.isMoving());
/* 208 */           event.setYaw(MovementUtil.getPlayerDirection());
/*     */         } 
/*     */         break;
/*     */       case "Test":
/*     */       case "Test2":
/* 213 */         event.setBoosting(MovementUtil.isMoving());
/* 214 */         event.setYaw(MovementUtil.getPlayerDirection());
/*     */         break;
/*     */     } 
/*     */   }
/*     */   @Listener
/*     */   public void onUpdate(UpdateEvent event) {
/*     */     int i;
/* 221 */     switch (this.mode.getMode()) {
/*     */       case "Vulcan":
/* 223 */         for (i = 8; i >= 0; i--) {
/* 224 */           ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
/*     */           
/* 226 */           if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBlock && !PlayerUtil.isBlockBlacklisted(stack.getItem())) {
/* 227 */             mc.thePlayer.inventory.currentItem = i;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 232 */         if (mc.thePlayer.onGround) {
/* 233 */           if (MovementUtil.isMoving()) {
/* 234 */             mc.thePlayer.jump();
/* 235 */             this.ticks = 0;
/*     */           }  break;
/*     */         } 
/* 238 */         if (this.ticks == 4) {
/* 239 */           if (this.started) {
/* 240 */             mc.thePlayer.motionY = -1.0D;
/*     */           }
/*     */           
/* 243 */           double x = (mc.thePlayer.motionX > 0.0D) ? 1.5D : -1.5D;
/* 244 */           double z = (mc.thePlayer.motionZ > 0.0D) ? 1.5D : -1.5D;
/*     */           
/* 246 */           mc.playerController.syncCurrentPlayItem();
/* 247 */           PacketUtil.sendPacketNoEvent((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 2.0D, mc.thePlayer.posZ + z), EnumFacing.UP.getIndex(), mc.thePlayer.inventory.getStackInSlot(mc.thePlayer.inventory.currentItem), 0.5F, 1.0F, 0.5F));
/* 248 */           mc.thePlayer.swingItem();
/*     */           
/* 250 */           this.started = true;
/*     */         } 
/*     */         
/* 253 */         this.ticks++;
/*     */         break;
/*     */ 
/*     */       
/*     */       case "Test":
/* 258 */         if (mc.thePlayer.onGround) {
/* 259 */           mc.thePlayer.jump();
/* 260 */           mc.thePlayer.motionY = 0.0D;
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onMove(MoveEvent event) { double distance;
/* 268 */     if (!this.takingVelocity && mc.thePlayer.onGround) {
/* 269 */       this.wasTakingVelocity = false;
/*     */     }
/*     */     
/* 272 */     double velocityExtra = 0.28D + MovementUtil.getSpeedAmplifier() * 0.07D;
/*     */     
/* 274 */     float direction = MathHelper.wrapAngleTo180_float(MovementUtil.getPlayerDirection());
/*     */     
/* 276 */     float forward = mc.thePlayer.moveForward;
/* 277 */     float strafe = mc.thePlayer.moveStrafing;
/*     */     
/* 279 */     switch (this.mode.getMode()) {
/*     */       case "Vanilla":
/* 281 */         if (mc.thePlayer.onGround && MovementUtil.isMoving() && this.autoJump.isEnabled()) {
/* 282 */           event.setY(mc.thePlayer.motionY = mc.thePlayer.getJumpUpwardsMotion());
/*     */         }
/*     */         
/* 285 */         MovementUtil.strafe(event, this.vanillaSpeed.getValue());
/*     */         break;
/*     */       case "NCP":
/* 288 */         switch (this.ncpMode.getMode()) {
/*     */           case "Hop":
/* 290 */             if (mc.thePlayer.onGround) {
/* 291 */               this.prevOnGround = true;
/*     */               
/* 293 */               if (MovementUtil.isMoving()) {
/* 294 */                 event.setY(mc.thePlayer.motionY = mc.thePlayer.getJumpUpwardsMotion());
/*     */                 
/* 296 */                 this.speed *= 0.91D;
/* 297 */                 this.speed += ((this.ticks >= 8) ? 0.2D : 0.15D) + mc.thePlayer.getAIMoveSpeed();
/*     */                 
/* 299 */                 this.ticks = 0;
/*     */               } 
/* 301 */             } else if (this.prevOnGround) {
/* 302 */               this.speed *= 0.58D;
/* 303 */               this.speed += 0.026D;
/*     */               
/* 305 */               this.prevOnGround = false;
/*     */             } else {
/* 307 */               this.speed *= 0.91D;
/* 308 */               this.speed += 0.026D;
/*     */               
/* 310 */               this.ticks++;
/*     */             } 
/*     */             
/* 313 */             if (this.speed > 0.2D) {
/* 314 */               this.speed -= 1.0E-6D;
/*     */             }
/*     */             break;
/*     */           case "Updated Hop":
/* 318 */             if (mc.thePlayer.onGround) {
/* 319 */               this.prevOnGround = true;
/*     */               
/* 321 */               if (MovementUtil.isMoving()) {
/* 322 */                 MovementUtil.jump(event);
/*     */                 
/* 324 */                 this.speed *= 0.91D;
/*     */                 
/* 326 */                 if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 327 */                   this.speed = this.velocityDist + velocityExtra;
/*     */                 }
/*     */                 
/* 330 */                 this.speed += 0.2D + mc.thePlayer.getAIMoveSpeed();
/*     */               }  break;
/* 332 */             }  if (this.prevOnGround) {
/* 333 */               this.speed *= 0.53D;
/*     */               
/* 335 */               if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 336 */                 this.speed = this.velocityDist + velocityExtra;
/*     */               }
/*     */               
/* 339 */               this.speed += 0.026000000536441803D;
/*     */               
/* 341 */               this.prevOnGround = false; break;
/*     */             } 
/* 343 */             this.speed *= 0.91D;
/*     */             
/* 345 */             if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 346 */               this.speed = this.velocityDist + velocityExtra;
/*     */             }
/*     */             
/* 349 */             this.speed += 0.026000000536441803D;
/*     */             break;
/*     */         } 
/*     */ 
/*     */         
/* 354 */         switch (this.timerMode.getMode()) {
/*     */           case "None":
/* 356 */             mc.timer.timerSpeed = 1.0F;
/*     */             break;
/*     */           case "Bypass":
/* 359 */             mc.timer.timerSpeed = 1.08F;
/*     */             break;
/*     */           case "Custom":
/* 362 */             mc.timer.timerSpeed = (float)this.customTimer.getValue();
/*     */             break;
/*     */         } 
/*     */         
/* 366 */         MovementUtil.strafe(event, this.speed);
/*     */         break;
/*     */       case "Watchdog":
/* 369 */         switch (this.watchdogMode.getMode()) {
/*     */           case "Strafe":
/* 371 */             if (mc.thePlayer.onGround) {
/* 372 */               if (MovementUtil.isMoving()) {
/* 373 */                 this.prevOnGround = true;
/*     */                 
/* 375 */                 MovementUtil.jump(event);
/*     */                 
/* 377 */                 this.speed = 0.585D + MovementUtil.getSpeedAmplifier() * 0.065D;
/*     */               } 
/* 379 */             } else if (this.prevOnGround) {
/* 380 */               if (this.ticks++ % 5 > 0 && this.fast.isEnabled()) {
/* 381 */                 this.speed *= 0.6499999761581421D;
/*     */               } else {
/* 383 */                 this.speed *= 0.5299999713897705D;
/*     */               } 
/* 385 */               this.prevOnGround = false;
/*     */             } else {
/* 387 */               this.speed = Math.min(this.speed, 0.35D + MovementUtil.getSpeedAmplifier() * 0.02D);
/*     */               
/* 389 */               this.speed *= 0.9100000262260437D;
/*     */               
/* 391 */               this.speed += ((float)this.attributeSpeedOffground.getValue() * 0.98F);
/*     */             } 
/*     */             
/* 394 */             MovementUtil.strafe(event, this.speed);
/*     */             break;
/*     */           case "Semi-Strafe":
/* 397 */             if (mc.thePlayer.onGround) {
/* 398 */               this.prevOnGround = true;
/*     */               
/* 400 */               if (MovementUtil.isMoving()) {
/* 401 */                 MovementUtil.jump(event);
/*     */                 
/* 403 */                 this.speed = 0.6D + MovementUtil.getSpeedAmplifier() * 0.075D;
/*     */               } 
/* 405 */             } else if (this.prevOnGround) {
/* 406 */               this.speed *= 0.5400000214576721D;
/* 407 */               this.prevOnGround = false;
/*     */             } else {
/* 409 */               this.speed *= 0.6000000238418579D;
/*     */               
/* 411 */               this.speed += ((mc.thePlayer.isSprinting() ? 0.026F : 0.02F) * 0.6F);
/*     */             } 
/*     */             
/* 414 */             direction = MovementUtil.getPlayerDirection();
/*     */             
/* 416 */             if (!mc.thePlayer.onGround) {
/* 417 */               float dirChange = Math.abs(direction - this.lastDirection);
/*     */               
/* 419 */               if (dirChange > 180.0F) {
/* 420 */                 dirChange = 360.0F - dirChange;
/*     */               }
/*     */               
/* 423 */               double reduceMult = 1.0D - dirChange * 0.01D;
/*     */               
/* 425 */               this.speed *= reduceMult;
/*     */               
/* 427 */               this.speed = Math.max(this.speed, 0.09D);
/*     */             } 
/*     */             
/* 430 */             if (mc.thePlayer.isCollidedHorizontally) {
/* 431 */               this.speed = 0.09D;
/*     */             }
/*     */             
/* 434 */             MovementUtil.strafe(event, this.speed);
/*     */             
/* 436 */             this.lastDirection = direction;
/*     */             break;
/*     */           case "Strafeless":
/* 439 */             if (MovementUtil.isMoving()) {
/* 440 */               if (mc.thePlayer.onGround) {
/* 441 */                 this.prevOnGround = true;
/*     */                 
/* 443 */                 MovementUtil.jump(event);
/*     */                 
/* 445 */                 if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
/* 446 */                   MovementUtil.strafeNoTargetStrafe(event, 0.59D - Math.random() * 0.001D + MovementUtil.getSpeedAmplifier() * 0.08D);
/*     */                 } else {
/* 448 */                   MovementUtil.strafeNoTargetStrafe(event, 0.6D - Math.random() * 0.001D);
/*     */                 }
/*     */               
/* 451 */               } else if (this.prevOnGround) {
/* 452 */                 if (mc.thePlayer.isSprinting() && 
/* 453 */                   ++this.counter > 1 && this.fast.isEnabled()) {
/* 454 */                   if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
/* 455 */                     event.setX(event.getX() * this.speedPotMult.getValue());
/* 456 */                     event.setZ(event.getZ() * this.speedPotMult.getValue());
/*     */                   } else {
/* 458 */                     event.setX(event.getX() * this.mult.getValue());
/* 459 */                     event.setZ(event.getZ() * this.mult.getValue());
/*     */                   } 
/*     */                 }
/*     */ 
/*     */                 
/* 464 */                 this.prevOnGround = false;
/*     */               } 
/*     */             }
/*     */ 
/*     */             
/* 469 */             this.lastForward = forward;
/* 470 */             this.lastStrafe = strafe;
/*     */             break;
/*     */           case "Ground":
/* 473 */             if (mc.thePlayer.onGround) {
/* 474 */               this.ticks = 0;
/*     */               
/* 476 */               if (!this.started) {
/* 477 */                 MovementUtil.jump(event);
/* 478 */                 MovementUtil.strafe(event, 0.55D + MovementUtil.getSpeedAmplifier() * 0.07D);
/* 479 */                 this.started = true;
/*     */               } else {
/* 481 */                 event.setY(mc.thePlayer.motionY = 5.0E-4D);
/* 482 */                 this.firstJumpDone = true;
/*     */                 
/* 484 */                 this.speed = 0.335D + (MovementUtil.getSpeedAmplifier() * 0.045F);
/*     */               } 
/*     */             } else {
/* 487 */               this.ticks++;
/*     */               
/* 489 */               if (this.speed > 0.28D) {
/* 490 */                 this.speed *= 0.995D;
/*     */               }
/*     */             } 
/*     */             
/* 494 */             if (this.firstJumpDone && this.ticks <= 2) {
/* 495 */               MovementUtil.strafe(event, this.speed);
/*     */             }
/*     */             break;
/*     */         } 
/*     */         
/* 500 */         mc.timer.timerSpeed = (float)this.customTimer.getValue();
/*     */         break;
/*     */       case "Blocksmc":
/* 503 */         if (mc.thePlayer.onGround) {
/* 504 */           this.prevOnGround = true;
/*     */           
/* 506 */           if (MovementUtil.isMoving()) {
/* 507 */             MovementUtil.jump(event);
/*     */             
/* 509 */             this.speed = 0.57D + MovementUtil.getSpeedAmplifier() * 0.065D;
/*     */             
/* 511 */             if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 512 */               this.speed = this.velocityDist + velocityExtra;
/*     */             }
/*     */             
/* 515 */             this.ticks = 1;
/*     */           } 
/* 517 */         } else if (this.prevOnGround) {
/* 518 */           this.speed *= 0.53D;
/*     */           
/* 520 */           if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 521 */             this.speed = this.velocityDist + velocityExtra;
/*     */           }
/*     */           
/* 524 */           this.speed += 0.026000000536441803D;
/*     */           
/* 526 */           this.prevOnGround = false;
/*     */         } else {
/* 528 */           this.speed *= 0.91D;
/*     */           
/* 530 */           if (this.takingVelocity && this.damageBoost.isEnabled()) {
/* 531 */             this.speed = this.velocityDist + velocityExtra;
/*     */           }
/*     */           
/* 534 */           this.speed += 0.026000000536441803D;
/*     */         } 
/*     */         
/* 537 */         if (this.takingVelocity) {
/* 538 */           this.ticks = -7;
/*     */         }
/*     */         
/* 541 */         if (++this.ticks == 0 && !mc.thePlayer.onGround) {
/* 542 */           this.speed = 0.28D + MovementUtil.getSpeedAmplifier() * 0.065D;
/*     */         }
/*     */         
/* 545 */         MovementUtil.strafe(event, this.speed);
/*     */         break;
/*     */       case "Strafe":
/* 548 */         if (mc.thePlayer.hurtTime <= this.minHurtTime.getValue()) {
/* 549 */           MovementUtil.strafe(event);
/*     */         }
/*     */         break;
/*     */       case "Fake strafe":
/* 553 */         distance = Math.hypot(mc.thePlayer.posX - this.actualX, mc.thePlayer.posZ - this.actualZ);
/*     */         
/* 555 */         if (this.fakeFly.isEnabled()) {
/* 556 */           if (mc.gameSettings.keyBindJump.isKeyDown()) {
/* 557 */             event.setY(0.35D);
/* 558 */           } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
/* 559 */             event.setY(-0.35D);
/*     */           } else {
/* 561 */             event.setY(0.0D);
/*     */           } 
/*     */           
/* 564 */           mc.thePlayer.motionY = 0.0D;
/*     */         }
/* 566 */         else if (mc.thePlayer.onGround && MovementUtil.isMoving()) {
/* 567 */           MovementUtil.jump(event);
/*     */         } 
/*     */ 
/*     */         
/* 571 */         if (!this.started) {
/* 572 */           this.speed = 0.65D;
/* 573 */           this.started = true;
/*     */         }
/* 575 */         else if (this.clientSpeed.is("Normal")) {
/* 576 */           double baseSpeed = 0.33D + MovementUtil.getSpeedAmplifier() * 0.02D;
/*     */           
/* 578 */           if (mc.thePlayer.onGround) {
/* 579 */             this.speed = 0.33D + baseSpeed;
/*     */           } else {
/* 581 */             this.speed = Math.min(this.speed - baseSpeed * distance * 0.15D, baseSpeed);
/*     */           } 
/*     */           
/* 584 */           this.speed = Math.max(this.speed, 0.2D);
/* 585 */         } else if (this.clientSpeed.is("Custom")) {
/*     */           
/* 587 */           this.speed = this.customClientSpeed.getValue();
/*     */         } 
/*     */ 
/*     */         
/* 591 */         MovementUtil.strafe(event, this.speed);
/*     */         
/* 593 */         this.lastDirection = direction; break;
/*     */     }  } @Listener public void onEntityAction(EntityActionEvent event) { float direction; float gcd; float yawDiff; float fixedYawDiff; float dir; float friction; float aa; float attributeSpeed; boolean oldActualGround; float forward; float strafe; float thing; double clientX; double clientY; double clientZ;
/*     */     double clientMotionX;
/*     */     double clientMotionY;
/*     */     double clientMotionZ;
/*     */     boolean clientGround;
/*     */     boolean collided;
/* 600 */     switch (this.mode.getMode()) {
/*     */       case "Fake strafe":
/* 602 */         this.lastActualX = this.actualX;
/* 603 */         this.lastActualY = this.actualY;
/* 604 */         this.lastActualZ = this.actualZ;
/*     */         
/* 606 */         direction = RotationsUtil.getRotationsToPosition(this.lastActualX, this.lastActualY, this.lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];
/*     */         
/* 608 */         gcd = RotationsUtil.getGCD();
/*     */         
/* 610 */         yawDiff = direction - this.lastYaw;
/*     */         
/* 612 */         fixedYawDiff = yawDiff - yawDiff % gcd;
/*     */         
/* 614 */         direction = this.lastYaw + fixedYawDiff;
/*     */         
/* 616 */         dir = direction * 0.017453292F;
/*     */         
/* 618 */         friction = getFriction(this.actualX, this.actualY, this.actualZ) * 0.91F;
/*     */         
/* 620 */         if (this.actualGround) {
/* 621 */           this.motionY = mc.thePlayer.getJumpUpwardsMotion();
/*     */           
/* 623 */           if (mc.thePlayer.isPotionActive(Potion.jump)) {
/* 624 */             this.motionY += ((mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
/*     */           }
/*     */           
/* 627 */           if (!this.wasCollided) {
/* 628 */             this.motionX -= (MathHelper.sin(dir) * 0.2F);
/* 629 */             this.motionZ += (MathHelper.cos(dir) * 0.2F);
/*     */           } 
/*     */         } 
/*     */         
/* 633 */         aa = 0.16277136F / friction * friction * friction;
/*     */ 
/*     */ 
/*     */         
/* 637 */         mc.thePlayer.setSprinting(!this.wasCollided);
/*     */         
/* 639 */         if (this.actualGround) {
/*     */           
/* 641 */           attributeSpeed = mc.thePlayer.getAIMoveSpeed() * aa;
/*     */         }
/*     */         else {
/*     */           
/* 645 */           attributeSpeed = this.wasCollided ? 0.02F : 0.026F;
/*     */         } 
/*     */         
/* 648 */         oldActualGround = this.actualGround;
/*     */         
/* 650 */         forward = 0.98F;
/* 651 */         strafe = 0.0F;
/*     */         
/* 653 */         thing = strafe * strafe + forward * forward;
/*     */         
/* 655 */         if (thing >= 1.0E-4F) {
/*     */           
/* 657 */           thing = MathHelper.sqrt_float(thing);
/*     */           
/* 659 */           if (thing < 1.0F)
/*     */           {
/* 661 */             thing = 1.0F;
/*     */           }
/*     */           
/* 664 */           thing = attributeSpeed / thing;
/* 665 */           strafe *= thing;
/* 666 */           forward *= thing;
/* 667 */           float f1 = MathHelper.sin(direction * 3.1415927F / 180.0F);
/* 668 */           float f2 = MathHelper.cos(direction * 3.1415927F / 180.0F);
/* 669 */           this.motionX += (strafe * f2 - forward * f1);
/* 670 */           this.motionZ += (forward * f2 + strafe * f1);
/*     */         } 
/*     */         
/* 673 */         if (this.groundStrafe.isEnabled() && this.actualGround) {
/* 674 */           double speed = Math.hypot(this.motionX, this.motionZ);
/*     */           
/* 676 */           this.motionX = -Math.sin(Math.toRadians(direction)) * speed;
/* 677 */           this.motionZ = Math.cos(Math.toRadians(direction)) * speed;
/*     */         } 
/*     */         
/* 680 */         clientX = mc.thePlayer.posX;
/* 681 */         clientY = mc.thePlayer.posY;
/* 682 */         clientZ = mc.thePlayer.posZ;
/*     */         
/* 684 */         clientMotionX = mc.thePlayer.motionX;
/* 685 */         clientMotionY = mc.thePlayer.motionY;
/* 686 */         clientMotionZ = mc.thePlayer.motionZ;
/*     */         
/* 688 */         clientGround = mc.thePlayer.onGround;
/*     */         
/* 690 */         mc.thePlayer.setPosition(this.actualX, this.actualY, this.actualZ);
/*     */         
/* 692 */         mc.thePlayer.onGround = this.actualGround;
/*     */         
/* 694 */         mc.thePlayer.moveEntityNoEvent(this.motionX, this.motionY, this.motionZ);
/*     */         
/* 696 */         collided = mc.thePlayer.isCollidedHorizontally;
/*     */         
/* 698 */         this.motionX = mc.thePlayer.posX - this.lastActualX;
/* 699 */         this.motionY = mc.thePlayer.posY - this.lastActualY;
/* 700 */         this.motionZ = mc.thePlayer.posZ - this.lastActualZ;
/*     */         
/* 702 */         this.actualX = mc.thePlayer.posX;
/* 703 */         this.actualY = mc.thePlayer.posY;
/* 704 */         this.actualZ = mc.thePlayer.posZ;
/*     */         
/* 706 */         this.actualGround = mc.thePlayer.onGround;
/*     */         
/* 708 */         mc.thePlayer.setPosition(clientX, clientY, clientZ);
/* 709 */         mc.thePlayer.onGround = clientGround;
/*     */         
/* 711 */         mc.thePlayer.motionX = clientMotionX;
/* 712 */         mc.thePlayer.motionY = clientMotionY;
/* 713 */         mc.thePlayer.motionZ = clientMotionZ;
/*     */         
/* 715 */         if (oldActualGround) {
/* 716 */           this.motionX *= (friction * 0.91F);
/* 717 */           this.motionZ *= (friction * 0.91F);
/*     */         } else {
/* 719 */           this.motionX *= 0.9100000262260437D;
/* 720 */           this.motionZ *= 0.9100000262260437D;
/*     */         } 
/*     */         
/* 723 */         this.motionY -= 0.08D;
/* 724 */         this.motionY *= 0.9800000190734863D;
/*     */         
/* 726 */         if (Math.abs(this.motionX) < 0.005D) {
/* 727 */           this.motionX = 0.0D;
/*     */         }
/*     */         
/* 730 */         if (Math.abs(this.motionY) < 0.005D) {
/* 731 */           this.motionY = 0.0D;
/*     */         }
/*     */         
/* 734 */         if (Math.abs(this.motionZ) < 0.005D) {
/* 735 */           this.motionZ = 0.0D;
/*     */         }
/*     */         
/* 738 */         if (this.sprint.isEnabled()) {
/* 739 */           event.setSprinting(!this.wasCollided);
/*     */         } else {
/* 741 */           event.setSprinting(false);
/*     */         } 
/*     */         
/* 744 */         mc.thePlayer.setSprinting(true);
/*     */         
/* 746 */         event.setSneaking(false);
/*     */         
/* 748 */         this.wasCollided = collided;
/*     */         break;
/*     */       case "Test":
/*     */       case "Test2":
/* 752 */         event.setSprinting(MovementUtil.isMoving());
/*     */         break;
/*     */     }  }
/*     */   
/*     */   @Listener
/*     */   public void onMotion(MotionEvent event) {
/*     */     float direction;
/* 759 */     switch (this.mode.getMode()) {
/*     */       case "Fake strafe":
/* 761 */         event.setX(this.actualX);
/* 762 */         event.setY(this.actualY);
/* 763 */         event.setZ(this.actualZ);
/* 764 */         event.setOnGround(this.actualGround);
/*     */         
/* 766 */         direction = RotationsUtil.getRotationsToPosition(this.lastActualX, this.lastActualY, this.lastActualZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)[0];
/*     */         
/* 768 */         if (this.rotate.isEnabled()) {
/* 769 */           float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
/* 770 */           float gcd = f * f * f * 1.2F;
/*     */           
/* 772 */           float deltaYaw = direction - this.lastYaw;
/*     */           
/* 774 */           float fixedDeltaYaw = deltaYaw - deltaYaw % gcd;
/*     */           
/* 776 */           direction = this.lastYaw + fixedDeltaYaw;
/*     */           
/* 778 */           this.lastYaw = direction;
/*     */           
/* 780 */           event.setYaw(direction);
/*     */         } 
/*     */         break;
/*     */     } 
/*     */     
/* 785 */     this.takingVelocity = false;
/*     */     
/* 787 */     this.ticksSinceVelocity++;
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onRender3D(Render3DEvent event) {
/* 792 */     switch (this.mode.getMode()) {
/*     */       case "Fake strafe":
/* 794 */         if (this.renderRealPosBox.isEnabled() && mc.gameSettings.thirdPersonView > 0) {
/* 795 */           RenderUtil.prepareBoxRender(3.25F, 1.0D, 1.0D, 1.0D, 0.800000011920929D);
/*     */           
/* 797 */           RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), event.getPartialTicks(), this.actualX, this.actualY, this.actualZ, this.lastActualX, this.lastActualY, this.lastActualZ);
/*     */           
/* 799 */           RenderUtil.stopBoxRender();
/*     */         } 
/*     */         break;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onReceive(PacketReceiveEvent event) {
/* 807 */     if (event.getPacket() instanceof S12PacketEntityVelocity) {
/* 808 */       S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
/*     */       
/* 810 */       if (mc.thePlayer.getEntityId() == packet.getEntityID()) {
/* 811 */         this.takingVelocity = this.wasTakingVelocity = true;
/*     */         
/* 813 */         this.velocityX = packet.getMotionX() / 8000.0D;
/* 814 */         this.velocityY = packet.getMotionY() / 8000.0D;
/* 815 */         this.velocityZ = packet.getMotionZ() / 8000.0D;
/*     */         
/* 817 */         this.velocityDist = Math.hypot(this.velocityX, this.velocityZ);
/*     */         
/* 819 */         this.ticksSinceVelocity = 0;
/*     */         
/* 821 */         if (this.mode.is("Fake strafe")) {
/* 822 */           event.setCancelled(true);
/*     */           
/* 824 */           switch (this.velocityMode.getMode()) {
/*     */             case "Vertical":
/* 826 */               this.motionY = this.velocityY;
/*     */               break;
/*     */             case "Legit":
/* 829 */               this.motionX = this.velocityX;
/* 830 */               this.motionY = this.velocityY;
/* 831 */               this.motionZ = this.velocityZ;
/*     */               break;
/*     */           } 
/*     */         } 
/*     */       } 
/* 836 */     } else if (event.getPacket() instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook && 
/* 837 */       this.mode.is("Fake strafe")) {
/* 838 */       setEnabled(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private float getFriction(double x, double y, double z) {
/* 844 */     Block block = mc.theWorld.getBlockState(new BlockPos(x, Math.floor(y) - 1.0D, z)).getBlock();
/*     */     
/* 846 */     if (block != null) {
/* 847 */       if (block instanceof net.minecraft.block.BlockIce || block instanceof net.minecraft.block.BlockPackedIce)
/* 848 */         return 0.98F; 
/* 849 */       if (block instanceof net.minecraft.block.BlockSlime) {
/* 850 */         return 0.8F;
/*     */       }
/*     */     } 
/*     */     
/* 854 */     return 0.6F;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSuffix() {
/* 859 */     return this.mode.getMode();
/*     */   }
/*     */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\movement\Speed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */