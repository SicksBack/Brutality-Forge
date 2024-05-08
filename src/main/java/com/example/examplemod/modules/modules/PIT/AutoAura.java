/*    */ package dev.huys.modules.pit;
/*    */ 
/*    */ import dev.huys.events.Event;
/*    */ import dev.huys.modules.Module;
/*    */ import dev.huys.settings.NumberSetting;
/*    */ import dev.huys.settings.Setting;
/*    */ import dev.huys.utils.Timer;
/*    */ import dev.huys.utils.Wrapper;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.world.World;
/*    */ 
/*    */ public class AutoAura
/*    */   extends Module
/*    */ {
/* 17 */   public NumberSetting health = new NumberSetting("Health", 10.0D, 1.0D, 10.0D, 1.0D);
/* 18 */   public NumberSetting delay = new NumberSetting("Delay", 1000.0D, 75.0D, 2000.0D, 25.0D); private Timer timer; private Timer resettimer; private int currentSlot;
/* 19 */   public NumberSetting swapdelay = new NumberSetting("Swap Delay", 300.0D, 0.0D, 1000.0D, 25.0D); private boolean useGhead; private int gheadSlot;
/*    */   
/*    */   public AutoAura() {
/* 22 */     super("AutoAura", "None", 0, Module.Category.PLAYER, Boolean.valueOf(false));
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 30 */     this.timer = new Timer();
/* 31 */     this.resettimer = new Timer();
/* 32 */     this.currentSlot = 0;
/* 33 */     this.useGhead = false;
/*    */     addSettings(new Setting[] { (Setting)this.health, (Setting)this.delay, (Setting)this.swapdelay });
/*    */   } public void onDisable() {}
/*    */   public void onEvent(Event e) {
/* 37 */     if (e instanceof dev.huys.events.listeners.EventUpdate) {
/* 38 */       if (this.mc.currentScreen != null)
/*    */         return; 
/* 40 */       if ((this.mc.thePlayer.getHealth() / 2.0F) <= this.health.getValue()) {
/* 41 */         if (!this.useGhead && this.timer.hasTimeElapsed((long)this.delay.getValue(), true)) {
/* 42 */           this.gheadSlot = findGheadSlot();
/* 43 */           if (this.gheadSlot != -1) {
/* 44 */             this.currentSlot = this.mc.thePlayer.inventory.currentItem;
/* 45 */             this.mc.thePlayer.inventory.currentItem = this.gheadSlot;
/* 46 */             this.useGhead = true;
/*    */           } 
/* 48 */         } else if (this.useGhead && 
/* 49 */           this.timer.hasTimeElapsed((long)(this.swapdelay.getValue() + Math.random() * 30.0D), true) && 
/* 50 */           this.mc.thePlayer.inventory.currentItem == this.gheadSlot) {
/* 51 */           ItemStack itemStack = this.mc.thePlayer.inventory.getStackInSlot(this.gheadSlot);
/* 52 */           if (itemStack != null && itemStack.stackSize > 0) {
/*    */             
/* 54 */             this.mc.playerController.sendUseItem((EntityPlayer)Wrapper.getPlayer(), (World)Wrapper.getWorld(), 
/* 55 */                 Wrapper.getPlayer().getCurrentEquippedItem());
/*    */ 
/*    */             
/* 58 */             this.mc.thePlayer.inventory.currentItem = this.currentSlot;
/* 59 */             this.useGhead = false;
/*    */           } 
/*    */         } 
/*    */       }
/*    */ 
/*    */       
/* 65 */       if (this.resettimer.hasTimeElapsed(5000L, true)) {
/* 66 */         int currentSlot = 0;
/* 67 */         boolean bool = false;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private int findGheadSlot() {
/* 75 */     for (int i = 0; i < 9; i++) {
/* 76 */       ItemStack itemStack = this.mc.thePlayer.inventory.mainInventory[i];
/* 77 */       if (itemStack != null && itemStack.getItem() == Items.slime_ball) {
/* 78 */         return i;
/*    */       }
/*    */     } 
/* 81 */     return -1;
/*    */   }
/*    */ }


/* Location:              C:\Users\ASUS\OneDrive\Desktop\clients\paid\scary\Scary.jar!\dev\huys\modules\pit\AutoAura.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */