/*    */ package dev.huys.modules.pit;
/*    */ 
/*    */ import dev.huys.events.Event;
/*    */ import dev.huys.modules.Module;
/*    */ import dev.huys.settings.ModeSetting;
/*    */ import dev.huys.settings.NumberSetting;
/*    */ import dev.huys.settings.Setting;
/*    */ import dev.huys.utils.Timer;
/*    */ import dev.huys.utils.Wrapper;
/*    */ import net.minecraft.client.Minecraft;
/*    */ import net.minecraft.client.entity.EntityPlayerSP;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.init.Items;
/*    */ import net.minecraft.item.Item;
/*    */ 
/*    */ 
/*    */ public class SpadeSwap
/*    */   extends Module
/*    */ {
/* 20 */   public NumberSetting delay = new NumberSetting("Delay", 200.0D, 1.0D, 200.0D, 5.0D);
/* 21 */   public static ModeSetting item = new ModeSetting("Item", "Diamond Sword", new String[] { "Diamond Sword", "Axe", "Mystic Sword" });
/*    */   
/* 23 */   public static String itemm = item.getMode();
/*    */   
/*    */   public SpadeSwap() {
/* 26 */     super("SpadeSwap", "None", 0, Module.Category.PIT, Boolean.valueOf(false));
/* 27 */     addSettings(new Setting[] { (Setting)this.delay, (Setting)item });
/*    */   }
/*    */ 
/*    */   
/*    */   Item sword;
/*    */ 
/*    */   
/*    */   public void onDisable() {}
/*    */ 
/*    */   
/*    */   public void onEnable() {}
/*    */ 
/*    */   
/* 40 */   private static final Timer timer = new Timer();
/*    */   
/*    */   public void onEvent(Event e) {
/* 43 */     if (e instanceof dev.huys.events.listeners.EventUpdate) {
/* 44 */       String modee = item.getMode();
/* 45 */       setTag(item.getMode());
/* 46 */       if (e.isPre()) {
/* 47 */         if (item.getMode() == "Axe") {
/* 48 */           this.sword = Items.diamond_axe;
/*    */         }
/* 50 */         if (item.getMode() == "Diamond Sword") {
/* 51 */           this.sword = Items.diamond_sword;
/*    */         }
/* 53 */         if (item.getMode() == "Mystic Sword") {
/* 54 */           this.sword = Items.golden_sword;
/*    */         }
/* 56 */         if (Wrapper.hasItem((EntityPlayer)this.mc.thePlayer, this.sword) && Wrapper.hasItem((EntityPlayer)this.mc.thePlayer, Items.diamond_shovel)) {
/* 57 */           if (this.mc.currentScreen != null)
/*    */             return; 
/* 59 */           if (timer.hasTimeElapsed((long)this.delay.getValue(), true)) {
/* 60 */             Minecraft mc = Minecraft.getMinecraft();
/* 61 */             EntityPlayerSP player = mc.thePlayer;
/* 62 */             if (player == null)
/*    */               return; 
/* 64 */             int swordSlot = Wrapper.findItem((EntityPlayer)player, this.sword);
/* 65 */             int spade = Wrapper.findItem((EntityPlayer)player, Items.diamond_shovel);
/* 66 */             if (player.inventory.currentItem == swordSlot) {
/* 67 */               if (spade < 9)
/* 68 */                 player.inventory.currentItem = spade; 
/* 69 */             } else if (player.inventory.currentItem == spade && 
/* 70 */               swordSlot < 9) {
/* 71 */               player.inventory.currentItem = swordSlot;
/*    */             } 
/* 73 */             timer.reset();
/*    */           } 
/*    */         } else {
/* 76 */           toggle();
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\ASUS\OneDrive\Desktop\clients\paid\scary\Scary.jar!\dev\huys\modules\pit\SpadeSwap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */