/*     */ package Acrimony.module.impl.movement;
/*     */ import Acrimony.Acrimony;
/*     */ import Acrimony.event.Listener;
/*     */ import Acrimony.event.impl.ItemRenderEvent;
/*     */ import Acrimony.event.impl.PacketSendEvent;
/*     */ import Acrimony.event.impl.PostMotionEvent;
/*     */ import Acrimony.event.impl.SlowdownEvent;
/*     */ import Acrimony.event.impl.UpdateEvent;
/*     */ import Acrimony.module.Category;
/*     */ import Acrimony.module.Module;
/*     */ import Acrimony.module.impl.combat.Killaura;
/*     */ import Acrimony.setting.AbstractSetting;
/*     */ import Acrimony.setting.impl.BooleanSetting;
/*     */ import Acrimony.setting.impl.DoubleSetting;
/*     */ import Acrimony.setting.impl.IntegerSetting;
/*     */ import Acrimony.setting.impl.ModeSetting;
/*     */ import Acrimony.util.network.PacketUtil;
/*     */ import java.util.Objects;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.client.C07PacketPlayerDigging;
/*     */ import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
/*     */ import net.minecraft.network.play.client.C09PacketHeldItemChange;
/*     */ import net.minecraft.util.BlockPos;
/*     */ import net.minecraft.util.EnumFacing;
/*     */ import org.lwjgl.input.Mouse;
/*     */ 
/*     */ public class Noslow extends Module {
/*  28 */   private final ModeSetting swordMethod = new ModeSetting("Sword method", "Vanilla", new String[] { "Vanilla", "Watchdog", "NCP", "AAC4", "AAC5", "Spoof", "Spoof2", "Blink", "None" });
/*  29 */   private final ModeSetting consumableMethod = new ModeSetting("Eating method", "Vanilla", new String[] { "Vanilla", "Hypixel", "AAC4", "AAC5", "None" });
/*     */   
/*  31 */   private final DoubleSetting forward = new DoubleSetting("Forward", 1.0D, 0.2D, 1.0D, 0.05D);
/*  32 */   private final DoubleSetting strafe = new DoubleSetting("Strafe", 1.0D, 0.2D, 1.0D, 0.05D);
/*     */   
/*  34 */   private final IntegerSetting blinkTicks = new IntegerSetting("Blink ticks", () -> Boolean.valueOf(this.swordMethod.is("Blink")), 5, 2, 10, 1);
/*     */   
/*  36 */   public final BooleanSetting allowSprinting = new BooleanSetting("Allow sprinting", true);
/*     */   
/*     */   private Killaura killauraModule;
/*     */   
/*     */   private boolean lastUsingItem;
/*     */   
/*     */   private int ticks;
/*     */   
/*     */   private int lastSlot;
/*     */   private boolean wasEating;
/*     */   
/*     */   public Noslow() {
/*  48 */     super("Noslow", Category.MOVEMENT);
/*  49 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.swordMethod, (AbstractSetting)this.consumableMethod, (AbstractSetting)this.forward, (AbstractSetting)this.strafe, (AbstractSetting)this.blinkTicks, (AbstractSetting)this.allowSprinting });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onEnable() {
/*  54 */     this.lastUsingItem = this.wasEating = false;
/*  55 */     this.lastSlot = mc.thePlayer.inventory.currentItem;
/*     */     
/*  57 */     this.ticks = 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  62 */     Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*     */   }
/*     */ 
/*     */   
/*     */   public void onClientStarted() {
/*  67 */     this.killauraModule = (Killaura)Acrimony.instance.getModuleManager().getModule(Killaura.class);
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onUpdate(UpdateEvent event) {
/*  72 */     if (isUsingItem()) {
/*  73 */       if (isBlocking()) {
/*  74 */         int slot; switch (this.swordMethod.getMode()) {
/*     */           case "NCP":
/*  76 */             PacketUtil.releaseUseItem(true);
/*     */             break;
/*     */           case "AAC4":
/*  79 */             if (mc.thePlayer.ticksExisted % 2 == 0) {
/*  80 */               PacketUtil.releaseUseItem(true);
/*     */             }
/*     */             break;
/*     */           case "AAC5":
/*  84 */             if (this.lastUsingItem) {
/*  85 */               PacketUtil.sendBlocking(true, false);
/*     */             }
/*     */             break;
/*     */           case "Spoof":
/*  89 */             slot = mc.thePlayer.inventory.currentItem;
/*     */             
/*  91 */             PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange((slot < 8) ? (slot + 1) : 0));
/*  92 */             PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(slot));
/*     */             
/*  94 */             if (this.lastUsingItem) {
/*  95 */               PacketUtil.sendBlocking(true, false);
/*     */             }
/*     */             break;
/*     */           case "Spoof2":
/*  99 */             PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
/*     */             break;
/*     */           case "Watchdog":
/* 102 */             PacketUtil.sendBlocking(true, false);
/* 103 */             if (mc.thePlayer.isUsingItem() && !mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 3 == 0) {
/* 104 */               PacketUtil.sendPacketNoEvent((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0F, 0.0F, 0.0F));
/*     */             }
/*     */             break;
/*     */         } 
/*     */       } else {
/* 109 */         switch (this.consumableMethod.getMode()) {
/*     */           case "AAC4":
/* 111 */             if (this.lastUsingItem) {
/* 112 */               int slot = mc.thePlayer.inventory.currentItem;
/*     */               
/* 114 */               PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange((slot < 8) ? (slot + 1) : 0));
/* 115 */               PacketUtil.sendPacket((Packet)new C09PacketHeldItemChange(slot));
/*     */             } 
/*     */             break;
/*     */           case "AAC5":
/* 119 */             if (this.lastUsingItem) {
/* 120 */               PacketUtil.sendBlocking(true, false);
/*     */             }
/*     */             break;
/*     */         } 
/*     */       
/*     */       } 
/*     */     }
/* 127 */     if (this.swordMethod.is("Blink")) {
/* 128 */       if (isHoldingSword() && pressingUseItem()) {
/* 129 */         if (this.ticks == 1) {
/* 130 */           Acrimony.instance.getPacketBlinkHandler().releasePackets();
/* 131 */           Acrimony.instance.getPacketBlinkHandler().startBlinking();
/*     */         } 
/*     */         
/* 134 */         if (this.ticks > 0 && this.ticks < this.blinkTicks.getValue()) {
/* 135 */           mc.gameSettings.keyBindUseItem.pressed = false;
/*     */         }
/*     */         
/* 138 */         if (this.ticks == this.blinkTicks.getValue()) {
/* 139 */           Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*     */           
/* 141 */           mc.gameSettings.keyBindUseItem.pressed = true;
/*     */           
/* 143 */           this.ticks = 0;
/*     */         } 
/*     */         
/* 146 */         this.ticks++;
/*     */       } else {
/* 148 */         Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*     */         
/* 150 */         this.ticks = 0;
/*     */       } 
/*     */     }
/*     */     
/* 154 */     if (this.consumableMethod.is("Hypixel") && 
/* 155 */       mc.thePlayer.isUsingItem() && !mc.thePlayer.isBlocking() && mc.thePlayer.ticksExisted % 3 == 0) {
/* 156 */       PacketUtil.sendPacketNoEvent((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), EnumFacing.UP.getIndex(), null, 0.0F, 0.0F, 0.0F));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   @Listener
/*     */   public void onPostMotion(PostMotionEvent event) {
/* 163 */     boolean usingItem = mc.thePlayer.isUsingItem();
/*     */     
/* 165 */     if (usingItem) {
/* 166 */       if (isBlocking()) {
/* 167 */         switch (this.swordMethod.getMode()) {
/*     */           case "NCP":
/* 169 */             if (isBlocking()) {
/* 170 */               PacketUtil.sendBlocking(true, false);
/*     */             }
/*     */             break;
/*     */           case "AAC4":
/* 174 */             if (mc.thePlayer.ticksExisted % 2 == 0) {
/* 175 */               PacketUtil.sendBlocking(true, false);
/*     */             }
/*     */             break;
/*     */         } 
/*     */       } else {
/* 180 */         Objects.requireNonNull(this.consumableMethod.getMode()); this.consumableMethod.getMode();
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 186 */     this.lastUsingItem = usingItem;
/*     */     
/* 188 */     this.wasEating = (usingItem && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemPotion));
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onSlowdown(SlowdownEvent event) {
/* 193 */     if ((!isBlocking() || !this.swordMethod.is("None")) && (isBlocking() || !this.consumableMethod.is("None"))) {
/*     */ 
/*     */       
/* 196 */       event.setForward((float)this.forward.getValue());
/* 197 */       event.setStrafe((float)this.strafe.getValue());
/* 198 */       event.setAllowedSprinting(this.allowSprinting.isEnabled());
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener
/*     */   public void onSend(PacketSendEvent event) {
/* 204 */     if (event.getPacket() instanceof C07PacketPlayerDigging) {
/* 205 */       C07PacketPlayerDigging packet = (C07PacketPlayerDigging)event.getPacket();
/*     */       
/* 207 */       if (packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM && 
/* 208 */         isHoldingSword() && this.swordMethod.is("Spoof")) {
/* 209 */         event.setCancelled(true);
/*     */         
/* 211 */         int slot = mc.thePlayer.inventory.currentItem;
/*     */         
/* 213 */         PacketUtil.sendPacketFinal((Packet)new C09PacketHeldItemChange((slot < 8) ? (slot + 1) : 0));
/* 214 */         PacketUtil.sendPacketFinal((Packet)new C09PacketHeldItemChange(slot));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Listener
/*     */   public void onItemRender(ItemRenderEvent event) {
/* 222 */     if (isHoldingSword() && pressingUseItem() && this.swordMethod.is("Blink")) {
/* 223 */       event.setRenderBlocking(true);
/*     */     }
/*     */     
/* 226 */     if (this.consumableMethod.is("Hypixel") && this.ticks > 1) {
/* 227 */       event.setRenderBlocking(true);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isBlocking() {
/* 232 */     return (mc.thePlayer.isUsingItem() && isHoldingSword());
/*     */   }
/*     */   
/*     */   public boolean isUsingItem() {
/* 236 */     return (mc.thePlayer.isUsingItem() && (!this.killauraModule.isEnabled() || this.killauraModule.getTarget() == null || this.killauraModule.autoblock.is("None")));
/*     */   }
/*     */   
/*     */   public boolean isHoldingSword() {
/* 240 */     return (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof net.minecraft.item.ItemSword);
/*     */   }
/*     */   
/*     */   public boolean pressingUseItem() {
/* 244 */     return (!(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory) && !(mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiChest) && Mouse.isButtonDown(1));
/*     */   }
/*     */   
/*     */   public String getSuffix() {
/* 248 */     return this.swordMethod.getMode() + "," + this.consumableMethod.getMode();
/*     */   }
/*     */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\movement\Noslow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */