/*     */ package Acrimony.module.impl.movement;
/*     */ import Acrimony.Acrimony;
/*     */ import Acrimony.event.Listener;
/*     */ import Acrimony.event.impl.EntityActionEvent;
/*     */ import Acrimony.event.impl.MotionEvent;
/*     */ import Acrimony.event.impl.MoveEvent;
/*     */ import Acrimony.event.impl.PacketReceiveEvent;
/*     */ import Acrimony.event.impl.PacketSendEvent;
/*     */ import Acrimony.event.impl.UpdateEvent;
/*     */ import Acrimony.event.impl.VelocityEvent;
/*     */ import Acrimony.module.Category;
/*     */ import Acrimony.module.Module;
/*     */ import Acrimony.setting.AbstractSetting;
/*     */ import Acrimony.setting.impl.BooleanSetting;
/*     */ import Acrimony.setting.impl.DoubleSetting;
/*     */ import Acrimony.setting.impl.ModeSetting;
/*     */ import Acrimony.util.misc.LogUtil;
/*     */ import Acrimony.util.network.PacketUtil;
/*     */ import Acrimony.util.player.MovementUtil;
/*     */ import Acrimony.util.player.PlayerUtil;
/*     */ import Acrimony.util.world.WorldUtil;
/*     */ import net.minecraft.item.ItemStack;
/*     */ import net.minecraft.network.play.client.C0APacketAnimation;
/*     */ import net.minecraft.network.play.server.S12PacketEntityVelocity;
/*     */ import net.minecraft.util.BlockPos;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import net.minecraft.util.MathHelper;
/*     */
/*     */ public class Fly extends Module {
    /*  30 */   private final ModeSetting mode = new ModeSetting("Mode", "Vanilla", new String[] { "Vanilla", "Collision", "NCP", "Blocksmc", "Velocity", "Hypixel" });
    /*     */
    /*  32 */   private final ModeSetting vanillaMode = new ModeSetting("Vanilla Mode", () -> Boolean.valueOf(this.mode.is("Vanilla")), "Motion", new String[] { "Motion", "Creative" });
    /*  33 */   private final DoubleSetting vanillaSpeed = new DoubleSetting("Vanilla speed", () -> Boolean.valueOf((this.mode.is("Vanilla") && this.vanillaMode.is("Motion"))), 2.0D, 0.2D, 9.0D, 0.2D);
    /*  34 */   private final DoubleSetting vanillaVerticalSpeed = new DoubleSetting("Vanilla vertical speed", () -> Boolean.valueOf((this.mode.is("Vanilla") && this.vanillaMode.is("Motion"))), 2.0D, 0.2D, 9.0D, 0.2D);
    /*     */
    /*  36 */   private final ModeSetting collisionMode = new ModeSetting("Collision mode", () -> Boolean.valueOf(this.mode.is("Collision")), "Airwalk", new String[] { "Airwalk", "Airjump" });
    /*     */
    /*  38 */   private final ModeSetting ncpMode = new ModeSetting("NCP Mode", () -> Boolean.valueOf(this.mode.is("NCP")), "Old", new String[] { "Old" });
    /*  39 */   private final DoubleSetting ncpSpeed = new DoubleSetting("NCP speed", () -> Boolean.valueOf((this.mode.is("NCP") && this.ncpMode.is("Old"))), 1.0D, 0.3D, 1.7D, 0.05D);
    /*  40 */   private final BooleanSetting damage = new BooleanSetting("Damage", () -> Boolean.valueOf((this.mode.is("NCP") && this.ncpMode.is("Old"))), false);
    /*     */
    /*  42 */   private final ModeSetting velocityMode = new ModeSetting("Velocity Mode", () -> Boolean.valueOf(this.mode.is("Velocity")), "Bow", new String[] { "Bow", "Bow2", "Wait for hit" });
    /*  43 */   private final BooleanSetting legit = new BooleanSetting("Legit", () -> Boolean.valueOf((this.mode.is("Bow") || this.mode.is("Bow2"))), false);
    /*     */
    /*  45 */   private final BooleanSetting automated = new BooleanSetting("Automated", () -> Boolean.valueOf(this.mode.is("Blocksmc")), false);
    /*     */
    /*     */   private double speed;
    /*     */
    /*     */   private boolean takingVelocity;
    /*     */
    /*     */   private double velocityX;
    /*     */
    /*     */   private double velocityY;
    /*     */   private double velocityZ;
    /*     */   private double velocityDist;
    /*     */   private int ticksSinceVelocity;
    /*     */   private int counter;
    /*     */   private int ticks;
    /*     */   private int veloTicks;
    /*     */   private boolean started;
    /*     */   private boolean done;
    /*     */   private double lastMotionX;
    /*     */   private double lastMotionY;
    /*     */   private double lastMotionZ;
    /*     */   private boolean hasBow;
    /*     */   private int oldSlot;
    /*     */   private boolean notMoving;
    /*     */   private float lastYaw;
    /*     */   private float lastPitch;
    /*     */   private BlockPos lastBarrier;
    /*     */   private double lastY;
    /*     */
    /*     */   public Fly() {
        /*  74 */     super("Fly", Category.MOVEMENT);
        /*  75 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.mode, (AbstractSetting)this.vanillaMode, (AbstractSetting)this.vanillaSpeed, (AbstractSetting)this.vanillaVerticalSpeed, (AbstractSetting)this.ncpMode, (AbstractSetting)this.ncpSpeed, (AbstractSetting)this.damage, (AbstractSetting)this.velocityMode, (AbstractSetting)this.legit, (AbstractSetting)this.automated });
        /*     */   }
    /*     */
    /*     */
    /*     */   public void onEnable() {
        /*  80 */     this.ticksSinceVelocity = Integer.MAX_VALUE;
        /*     */
        /*  82 */     this.counter = this.ticks = this.veloTicks = 0;
        /*     */
        /*  84 */     this.started = this.done = false;
        /*     */
        /*  86 */     this.hasBow = false;
        /*     */
        /*  88 */     this.notMoving = false;
        /*     */
        /*  90 */     this.lastMotionX = mc.thePlayer.motionX;
        /*  91 */     this.lastMotionY = mc.thePlayer.motionY;
        /*  92 */     this.lastMotionZ = mc.thePlayer.motionZ;
        /*     */
        /*  94 */     this.lastYaw = mc.thePlayer.rotationYaw;
        /*  95 */     this.lastPitch = mc.thePlayer.rotationPitch;
        /*     */
        /*  97 */     this.lastY = mc.thePlayer.posY;
        /*     */
        /*  99 */     this.lastBarrier = null;
        /*     */
        /* 101 */     switch (this.mode.getMode()) {
            /*     */       case "NCP":
                /* 103 */         if (this.ncpMode.is("Old")) {
                    /* 104 */           if (mc.thePlayer.onGround) {
                        /* 105 */             this.speed = this.ncpSpeed.getValue();
                        /*     */
                        /* 107 */             if (this.damage.isEnabled())
                            /* 108 */               PlayerUtil.ncpDamage();
                        /*     */             break;
                        /*     */           }
                    /* 111 */           this.speed = 0.28D;
                    /*     */         }
                /*     */         break;
            /*     */
            /*     */       case "Velocity":
                /* 116 */         if (mc.thePlayer.onGround) {
                    /* 117 */           mc.thePlayer.jump();
                    /*     */         }
                /*     */         break;
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   public void onDisable() {
        /* 125 */     mc.thePlayer.capabilities.isFlying = false;
        /*     */
        /* 127 */     Acrimony.instance.getPacketBlinkHandler().stopBlinking();
        /*     */
        /* 129 */     switch (this.mode.getMode()) {
            /*     */       case "Vanilla":
                /* 131 */         if (this.vanillaMode.is("Motion")) {
                    /* 132 */           MovementUtil.strafe(0.0D);
                    /*     */         }
                /*     */         break;
            /*     */       case "NCP":
                /* 136 */         if (this.ncpMode.is("Old")) {
                    /* 137 */           MovementUtil.strafe(0.0D);
                    /*     */         }
                /*     */         break;
            /*     */       case "Velocity":
                /* 141 */         switch (this.velocityMode.getMode()) {
                    /*     */           case "Wait for hit":
                        /* 143 */             mc.thePlayer.motionX = this.lastMotionX * 0.91D;
                        /* 144 */             mc.thePlayer.motionY = this.lastMotionY;
                        /* 145 */             mc.thePlayer.motionZ = this.lastMotionZ * 0.91D;
                        /*     */             break;
                    /*     */           case "Bow":
                        /* 148 */             mc.thePlayer.rotationYaw = this.lastYaw;
                        /* 149 */             mc.thePlayer.rotationPitch = -90.0F;
                        /*     */
                        /* 151 */             mc.gameSettings.keyBindUseItem.pressed = false;
                        /*     */             break;
                    /*     */           case "Bow2":
                        /* 154 */             mc.thePlayer.motionX = this.lastMotionX * 0.91D;
                        /* 155 */             mc.thePlayer.motionY = this.lastMotionY;
                        /* 156 */             mc.thePlayer.motionZ = this.lastMotionZ * 0.91D;
                        /*     */
                        /* 158 */             mc.thePlayer.rotationPitch = -90.0F;
                        /*     */
                        /* 160 */             mc.gameSettings.keyBindUseItem.pressed = false;
                        /*     */             break;
                    /*     */         }
                /*     */         break;
            /*     */       case "Blocksmc":
                /* 165 */         MovementUtil.strafe(0.0D);
                /*     */         break;
            /*     */     }
        /*     */
        /* 169 */     if (this.lastBarrier != null) {
            /* 170 */       mc.theWorld.setBlockToAir(this.lastBarrier);
            /*     */     }
        /*     */
        /* 173 */     mc.timer.timerSpeed = 1.0F;
        /*     */   }
    /*     */
    /*     */   @Listener
    /*     */   public void onUpdate(UpdateEvent event) {
        /* 178 */     switch (this.mode.getMode()) {
            /*     */       case "Velocity":
                /* 180 */         switch (this.velocityMode.getMode()) {
                    /*     */           case "Bow":
                        /* 182 */             if (this.takingVelocity) {
                            /* 183 */               Acrimony.instance.getPacketBlinkHandler().stopBlinking();
                            /*     */
                            /* 185 */               mc.thePlayer.motionY = this.velocityY;
                            /*     */
                            /* 187 */               boolean sameXDir = ((this.lastMotionX > 0.01D && this.velocityX > 0.0D) || (this.lastMotionX < -0.01D && this.velocityX < 0.0D));
                            /* 188 */               boolean sameZDir = ((this.lastMotionZ > 0.01D && this.velocityZ > 0.0D) || (this.lastMotionZ < -0.01D && this.velocityZ < 0.0D));
                            /*     */
                            /* 190 */               if (sameXDir && sameZDir) {
                                /* 191 */                 mc.thePlayer.motionX = this.velocityX;
                                /* 192 */                 mc.thePlayer.motionZ = this.velocityZ;
                                /*     */               }
                            /*     */             }
                        /*     */             break;
                    /*     */         }
                /*     */         break;
            /*     */       case "Collision":
                /* 199 */         switch (this.collisionMode.getMode()) {
                    /*     */           case "Airwalk":
                        /* 201 */             mc.thePlayer.onGround = true;
                        /*     */             break;
                    /*     */           case "Airjump":
                        /* 204 */             if (mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown()) {
                            /* 205 */               mc.thePlayer.jump();
                            /*     */             }
                        /*     */
                        /* 208 */             if (mc.thePlayer.fallDistance > (mc.gameSettings.keyBindJump.isKeyDown() ? 0.0D : 0.7D)) {
                            /* 209 */               if (this.lastBarrier != null) {
                                /* 210 */                 mc.theWorld.setBlockToAir(this.lastBarrier);
                                /*     */               }
                            /*     */
                            /* 213 */               this.lastBarrier = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
                            /*     */
                            /* 215 */               mc.theWorld.setBlockState(this.lastBarrier, Blocks.barrier.getDefaultState());
                            /*     */             }
                        /*     */             break;
                    /*     */         }
                /*     */         break;
            /*     */       case "Test":
                /* 221 */         if (mc.thePlayer.onGround) {
                    /* 222 */           if (!mc.gameSettings.keyBindJump.isKeyDown())
                        /* 223 */             mc.thePlayer.jump();
                    /*     */           break;
                    /*     */         }
                /* 226 */         if (this.ticks >= 2 && this.ticks <= 8) {
                    /* 227 */           mc.thePlayer.motionY += 0.07D;
                    /*     */         }
                /*     */
                /* 230 */         this.ticks++;
                /*     */         break;
            /*     */     }
        /*     */   }
    /*     */   @Listener
    /*     */   public void onMove(MoveEvent event) {
        /*     */     BlockPos pos;
        /*     */     int i;
        /* 238 */     switch (this.mode.getMode()) {
            /*     */       case "Vanilla":
                /* 240 */         switch (this.vanillaMode.getMode()) {
                    /*     */           case "Motion":
                        /* 242 */             MovementUtil.strafe(event, this.vanillaSpeed.getValue());
                        /*     */
                        /* 244 */             if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            /* 245 */               event.setY(this.vanillaVerticalSpeed.getValue());
                            /* 246 */             } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                            /* 247 */               event.setY(-this.vanillaVerticalSpeed.getValue());
                            /*     */             } else {
                            /* 249 */               event.setY(0.0D);
                            /*     */             }
                        /*     */
                        /* 252 */             mc.thePlayer.motionY = 0.0D;
                        /*     */             break;
                    /*     */           case "Creative":
                        /* 255 */             mc.thePlayer.capabilities.isFlying = true;
                        /*     */             break;
                    /*     */         }
                /*     */         break;
            /*     */       case "Collision":
                /* 260 */         if (this.collisionMode.is("Airwalk")) {
                    /* 261 */           event.setY(mc.thePlayer.motionY = 0.0D);
                    /*     */         }
                /*     */         break;
            /*     */       case "NCP":
                /* 265 */         switch (this.ncpMode.getMode()) {
                    /*     */           case "Old":
                        /* 267 */             if (mc.thePlayer.onGround) {
                            /* 268 */               MovementUtil.jump(event);
                            /* 269 */               MovementUtil.strafe(event, 0.58D); break;
                            /*     */             }
                        /* 271 */             event.setY(mc.thePlayer.motionY = 1.0E-10D);
                        /*     */
                        /* 273 */             if (!MovementUtil.isMoving() || mc.thePlayer.isCollidedHorizontally || this.speed < 0.28D) {
                            /* 274 */               this.speed = 0.28D;
                            /*     */             }
                        /*     */
                        /* 277 */             MovementUtil.strafe(event, this.speed);
                        /*     */
                        /* 279 */             this.speed -= this.speed / 159.0D;
                        /*     */             break;
                    /*     */         }
                /*     */
                /*     */         break;
            /*     */       case "Velocity":
                /* 285 */         switch (this.velocityMode.getMode()) {
                    /*     */           case "Wait for hit":
                        /* 287 */             if (this.takingVelocity) {
                            /* 288 */               event.setY(mc.thePlayer.motionY = this.velocityY);
                            /*     */
                            /* 290 */               event.setX(mc.thePlayer.motionX = this.lastMotionX);
                            /* 291 */               event.setZ(mc.thePlayer.motionZ = this.lastMotionZ);
                            /*     */
                            /* 293 */               this.notMoving = false;
                            /*     */
                            /* 295 */               this.ticks = 0;
                            /*     */             }
                        /* 297 */             if (event.getY() < -0.3D && !this.notMoving) {
                            /* 298 */               this.lastMotionX = event.getX();
                            /* 299 */               this.lastMotionY = event.getY();
                            /* 300 */               this.lastMotionZ = event.getZ();
                            /*     */
                            /* 302 */               this.notMoving = true;
                            /*     */             }
                        /*     */
                        /* 305 */             if (this.notMoving) {
                            /* 306 */               event.setY(mc.thePlayer.motionY = 0.0D);
                            /* 307 */               MovementUtil.strafe(event, 0.0D);
                            /*     */             }
                        /*     */
                        /* 310 */             this.ticks++;
                        /*     */             break;
                    /*     */
                    /*     */           case "Bow":
                        /* 314 */             for (i = 8; i >= 0; i--) {
                            /* 315 */               ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                            /*     */
                            /* 317 */               if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBow) {
                                /* 318 */                 mc.thePlayer.inventory.currentItem = i;
                                /*     */
                                /*     */                 break;
                                /*     */               }
                            /*     */             }
                        /* 323 */             if (this.takingVelocity) {
                            /* 324 */               mc.timer.timerSpeed = 1.0F;
                            /*     */
                            /* 326 */               this.notMoving = false;
                            /*     */
                            /* 328 */               this.ticks = 0;
                            /* 329 */               this.counter = 0;
                            /*     */
                            /* 331 */               this.started = true;
                            /*     */             } else {
                            /* 333 */               if (this.ticks <= 3) {
                                /* 334 */                 if (this.started) {
                                    /* 335 */                   mc.timer.timerSpeed = 1.5F;
                                    /*     */                 }
                                /* 337 */                 mc.gameSettings.keyBindUseItem.pressed = true;
                                /*     */               } else {
                                /* 339 */                 mc.gameSettings.keyBindUseItem.pressed = false;
                                /*     */               }
                            /*     */
                            /* 342 */               this.ticks++;
                            /*     */             }
                        /*     */
                        /* 345 */             if (this.ticks >= 6) {
                            /* 346 */               mc.timer.timerSpeed = 0.03F;
                            /* 347 */             } else if (this.ticks == 5) {
                            /* 348 */               mc.timer.timerSpeed = 0.1F;
                            /*     */             }
                        /*     */
                        /* 351 */             if (!this.started || this.notMoving || this.takingVelocity || MovementUtil.getHorizontalMotion() > 0.07D);
                        /*     */             break;
                    /*     */
                    /*     */
                    /*     */           case "Bow2":
                        /* 356 */             for (i = 8; i >= 0; i--) {
                            /* 357 */               ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                            /*     */
                            /* 359 */               if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBow) {
                                /* 360 */                 mc.thePlayer.inventory.currentItem = i;
                                /*     */
                                /*     */                 break;
                                /*     */               }
                            /*     */             }
                        /* 365 */             if (this.takingVelocity) {
                            /* 366 */               event.setY(mc.thePlayer.motionY = this.velocityY);
                            /*     */
                            /* 368 */               boolean sameXDir = ((this.lastMotionX > 0.0D && this.velocityX > 0.0D) || (this.lastMotionX < 0.0D && this.velocityX < 0.0D));
                            /* 369 */               boolean sameZDir = ((this.lastMotionZ > 0.0D && this.velocityZ > 0.0D) || (this.lastMotionZ < 0.0D && this.velocityZ < 0.0D));
                            /*     */
                            /*     */
                            /* 372 */               event.setX(mc.thePlayer.motionX = this.velocityX);
                            /* 373 */               event.setZ(mc.thePlayer.motionZ = this.velocityZ);
                            /*     */
                            /* 375 */               event.setX(mc.thePlayer.motionX = this.lastMotionX);
                            /* 376 */               event.setZ(mc.thePlayer.motionZ = this.lastMotionZ);
                            /*     */
                            /*     */
                            /* 379 */               this.notMoving = false;
                            /*     */
                            /* 381 */               this.ticks = 0; break;
                            /*     */             }
                        /* 383 */             if (this.ticks >= 6 && !this.notMoving) {
                            /* 384 */               this.lastMotionX = event.getX();
                            /* 385 */               this.lastMotionY = event.getY();
                            /* 386 */               this.lastMotionZ = event.getZ();
                            /*     */
                            /* 388 */               this.notMoving = true;
                            /*     */             }
                        /*     */
                        /* 391 */             if (this.ticks >= 1 && this.ticks <= 6) {
                            /* 392 */               mc.gameSettings.keyBindUseItem.pressed = true;
                            /*     */             } else {
                            /* 394 */               mc.gameSettings.keyBindUseItem.pressed = false;
                            /*     */             }
                        /*     */
                        /* 397 */             if (this.notMoving) {
                            /* 398 */               event.setY(mc.thePlayer.motionY = 0.0D);
                            /* 399 */               MovementUtil.strafe(event, 0.0D);
                            /*     */             }
                        /*     */
                        /* 402 */             this.ticks++;
                        /*     */             break;
                    /*     */         }
                /*     */
                /*     */         break;
            /*     */       case "Blocksmc":
                /* 408 */         if (this.automated.isEnabled() &&
                        /* 409 */           ++this.counter < 6) {
                    /* 410 */           BlockPos blockPos; float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
                    /*     */
                    /*     */
                    /* 413 */           double x = 0.0D;
                    /* 414 */           double z = 0.0D;
                    /*     */
                    /* 416 */           EnumFacing facing = EnumFacing.UP;
                    /*     */
                    /* 418 */           if (yaw > 135.0F || yaw < -135.0F) {
                        /* 419 */             z = 1.0D;
                        /* 420 */             facing = EnumFacing.NORTH;
                        /* 421 */           } else if (yaw > -135.0F && yaw < -45.0F) {
                        /* 422 */             x = -1.0D;
                        /* 423 */             facing = EnumFacing.EAST;
                        /* 424 */           } else if (yaw > -45.0F && yaw < 45.0F) {
                        /* 425 */             z = -1.0D;
                        /* 426 */             facing = EnumFacing.SOUTH;
                        /* 427 */           } else if (yaw > 45.0F && yaw < 135.0F) {
                        /* 428 */             x = 1.0D;
                        /* 429 */             facing = EnumFacing.WEST;
                        /*     */           }
                    /*     */
                    /*     */
                    /*     */
                    /* 434 */           switch (this.counter) {
                        /*     */             case 1:
                            /* 436 */               blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ + z);
                            /*     */
                            /* 438 */               mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, EnumFacing.UP, WorldUtil.getVec3(blockPos, EnumFacing.DOWN, true));
                            /*     */               break;
                        /*     */             case 2:
                            /* 441 */               blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY, mc.thePlayer.posZ + z);
                            /*     */
                            /* 443 */               mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, EnumFacing.UP, WorldUtil.getVec3(blockPos, EnumFacing.DOWN, true));
                            /*     */               break;
                        /*     */             case 3:
                            /* 446 */               blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + 1.0D, mc.thePlayer.posZ + z);
                            /*     */
                            /* 448 */               mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, EnumFacing.UP, WorldUtil.getVec3(blockPos, EnumFacing.DOWN, true));
                            /*     */               break;
                        /*     */             case 5:
                            /* 451 */               blockPos = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + 2.0D, mc.thePlayer.posZ + z);
                            /*     */
                            /* 453 */               mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockPos, facing, WorldUtil.getVec3(blockPos, facing, true));
                            /*     */               break;
                        /*     */           }
                    /*     */
                    /* 457 */           PacketUtil.sendPacket((Packet)new C0APacketAnimation());
                    /*     */
                    /* 459 */           MovementUtil.strafe(event, 0.04D);
                    /*     */
                    /*     */           return;
                    /*     */         }
                /*     */
                /* 464 */         pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0D, mc.thePlayer.posZ);
                /*     */
                /* 466 */         if (mc.theWorld.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir) {
                    /* 467 */           this.started = true;
                    /*     */         }
                /*     */
                /* 470 */         Acrimony.instance.getPacketBlinkHandler().startBlinking();
                /*     */
                /* 472 */         if (this.started) {
                    /* 473 */           mc.timer.timerSpeed = 0.3F;
                    /*     */
                    /* 475 */           if (mc.thePlayer.onGround) {
                        /* 476 */             if (this.ticks > 0) {
                            /* 477 */               setEnabled(false);
                            /*     */
                            /*     */               return;
                            /*     */             }
                        /* 481 */             if (MovementUtil.isMoving()) {
                            /* 482 */               MovementUtil.jump(event);
                            /* 483 */               MovementUtil.strafe(event, 0.58D);
                            /*     */             }
                        /* 485 */           } else if (this.ticks == 1) {
                        /* 486 */             MovementUtil.strafe(event, 9.5D);
                        /*     */           }
                    /*     */
                    /* 489 */           this.ticks++; break;
                    /*     */         }
                /* 491 */         MovementUtil.strafe(event, 0.1D);
                /*     */         break;
            /*     */
            /*     */       case "Hypixel":
                /* 495 */         if (this.veloTicks > 0) {
                    /* 496 */           this.veloTicks--;
                    /*     */
                    /* 498 */           if (this.veloTicks == 0) {
                        /* 499 */             MovementUtil.strafe(event);
                        /*     */           }
                    /*     */         }
                /*     */         break;
            /*     */     }
        /*     */
        /* 505 */     this.takingVelocity = false;
        /* 506 */     this.ticksSinceVelocity++;
        /*     */   }
    /*     */
    /*     */   @Listener
    /*     */   public void onEntityAction(EntityActionEvent event) {
        /* 511 */     switch (this.mode.getMode()) {
            /*     */       case "Velocity":
                /* 513 */         if (this.velocityMode.is("Wait for hit")) {
                    /* 514 */           event.setSprinting(true); break;
                    /* 515 */         }  if (this.velocityMode.is("Airjump") &&
                        /* 516 */           !this.started) {
                    /* 517 */           event.setSprinting(false);
                    /*     */         }
                /*     */         break;
            /*     */
            /*     */       case "Blocksmc":
                /* 522 */         if (this.automated.isEnabled() && this.counter < 6) {
                    /* 523 */           event.setSprinting(false);
                    /*     */         }
                /*     */         break;
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @Listener
    /*     */   public void onMotion(MotionEvent event) {
        /* 531 */     switch (this.mode.getMode()) {
            /*     */       case "Velocity":
                /* 533 */         if (this.velocityMode.is("Bow") || this.velocityMode.is("Bow2")) {
                    /* 534 */           event.setPitch(-90.0F);
                    /*     */         }
                /*     */         break;
            /*     */       case "Collision":
                /* 538 */         if (this.collisionMode.is("Airwalk")) {
                    /* 539 */           event.setOnGround(true);
                    /*     */         }
                /*     */         break;
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @Listener
    /*     */   public void onVelocity(VelocityEvent event) {
        /* 547 */     if (this.mode.is("Hypixel")) {
            /* 548 */       if (MovementUtil.isMoving() && !mc.thePlayer.onGround) {
                /* 549 */         this.veloTicks = 2;
                /* 550 */         LogUtil.addChatMessage(String.valueOf(this.veloTicks));
                /*     */       } else {
                /* 552 */         event.setX(mc.thePlayer.motionX);
                /* 553 */         event.setZ(mc.thePlayer.motionZ);
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */
    /*     */   @Listener
    /*     */   public void onReceive(PacketReceiveEvent event) {
        /* 560 */     if (event.getPacket() instanceof S12PacketEntityVelocity) {
            /* 561 */       S12PacketEntityVelocity packet = (S12PacketEntityVelocity)event.getPacket();
            /*     */
            /* 563 */       if (mc.thePlayer.getEntityId() == packet.getEntityID()) {
                /* 564 */         this.takingVelocity = true;
                /*     */
                /* 566 */         this.velocityX = packet.getMotionX() / 8000.0D;
                /* 567 */         this.velocityY = packet.getMotionY() / 8000.0D;
                /* 568 */         this.velocityZ = packet.getMotionZ() / 8000.0D;
                /*     */
                /* 570 */         this.velocityDist = Math.hypot(this.velocityX, this.velocityZ);
                /*     */
                /* 572 */         this.ticksSinceVelocity = 0;
                /*     */
                /* 574 */         if (this.mode.is("Velocity")) {
                    /* 575 */           event.setCancelled(true);
                    /*     */         }
                /*     */       }
            /* 578 */     } else if (event.getPacket() instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook &&
                /* 579 */       this.mode.is("Velocity")) {
            /* 580 */       setEnabled(false);
            /*     */       return;
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */   @Listener
    /*     */   public void onSend(PacketSendEvent event) {
        /* 588 */     switch (this.mode.getMode()) {
            /*     */       case "Velocity":
                /* 590 */         if ((this.velocityMode.is("Wait for hit") || this.velocityMode.is("Bow2")) &&
                        /* 591 */           event.getPacket() instanceof net.minecraft.network.play.client.C03PacketPlayer && this.notMoving) {
                    /* 592 */           event.setCancelled(true);
                    /*     */         }
                /*     */         break;
            /*     */     }
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public String getSuffix() {
        /* 601 */     return this.mode.getMode();
        /*     */   }
    /*     */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\movement\Fly.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */