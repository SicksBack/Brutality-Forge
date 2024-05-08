/*     */ package Acrimony.module.impl.movement;
/*     */ 
/*     */ import Acrimony.Acrimony;
/*     */ import Acrimony.event.Listener;
/*     */ import Acrimony.event.impl.EntityActionEvent;
/*     */ import Acrimony.event.impl.TickEvent;
/*     */ import Acrimony.event.impl.UpdateEvent;
/*     */ import Acrimony.module.Category;
/*     */ import Acrimony.module.Module;
/*     */ import Acrimony.setting.AbstractSetting;
/*     */ import Acrimony.setting.impl.BooleanSetting;
/*     */ import Acrimony.setting.impl.ModeSetting;
/*     */ import net.minecraft.client.settings.GameSettings;
/*     */ import net.minecraft.client.settings.KeyBinding;
/*     */ import org.lwjgl.input.Keyboard;
/*     */ 
/*     */ 
/*     */ public class InventoryMove
/*     */   extends Module
/*     */ {
/*  21 */   private final ModeSetting noSprint = new ModeSetting("No sprint", "Disabled", new String[] { "Disabled", "Enabled", "Spoof" });
/*  22 */   private final BooleanSetting blink = new BooleanSetting("Blink", false);
/*     */   
/*     */   private boolean hadInventoryOpened;
/*     */   
/*     */   private boolean blinking;
/*     */   
/*     */   public InventoryMove() {
/*  29 */     super("Inventory Move", Category.MOVEMENT);
/*  30 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.noSprint, (AbstractSetting)this.blink });
/*     */   }
/*     */ 
/*     */   
/*     */   public void onDisable() {
/*  35 */     if (this.blinking) {
/*  36 */       Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*  37 */       this.blinking = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener(3)
/*     */   public void onTick(TickEvent event) {
/*  43 */     if (isInventoryOpened()) {
/*  44 */       allowMove();
/*     */       
/*  46 */       if (this.noSprint.is("Enabled")) {
/*  47 */         mc.gameSettings.keyBindSprint.pressed = false;
/*  48 */         mc.thePlayer.setSprinting(false);
/*     */       } 
/*     */       
/*  51 */       if (this.blink.isEnabled()) {
/*  52 */         Acrimony.instance.getPacketBlinkHandler().startBlinking();
/*  53 */         this.blinking = true;
/*     */       } 
/*     */     } else {
/*  56 */       if (this.blinking) {
/*  57 */         Acrimony.instance.getPacketBlinkHandler().stopBlinking();
/*  58 */         this.blinking = false;
/*     */       } 
/*     */       
/*  61 */       if (this.hadInventoryOpened) {
/*  62 */         allowMove();
/*  63 */         this.hadInventoryOpened = false;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener(3)
/*     */   public void onUpdate(UpdateEvent event) {
/*  70 */     if (isInventoryOpened()) {
/*  71 */       allowMove();
/*     */       
/*  73 */       if (this.noSprint.is("Enabled")) {
/*  74 */         mc.gameSettings.keyBindSprint.pressed = false;
/*  75 */         mc.thePlayer.setSprinting(false);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   @Listener(3)
/*     */   public void onEntityAction(EntityActionEvent event) {
/*  82 */     if (isInventoryOpened()) {
/*  83 */       allowMove();
/*     */       
/*  85 */       if (this.noSprint.is("Spoof")) {
/*  86 */         event.setSprinting(false);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean isInventoryOpened() {
/*  92 */     return (mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiInventory || mc.currentScreen instanceof net.minecraft.client.gui.inventory.GuiChest);
/*     */   }
/*     */   
/*     */   private void allowMove() {
/*  96 */     GameSettings settings = mc.gameSettings;
/*  97 */     KeyBinding[] keys = { settings.keyBindForward, settings.keyBindBack, settings.keyBindLeft, settings.keyBindRight, settings.keyBindJump };
/*     */     
/*  99 */     for (KeyBinding key : keys) {
/* 100 */       key.pressed = Keyboard.isKeyDown(key.getKeyCode());
/*     */     }
/*     */     
/* 103 */     this.hadInventoryOpened = true;
/*     */   }
/*     */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\movement\InventoryMove.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */