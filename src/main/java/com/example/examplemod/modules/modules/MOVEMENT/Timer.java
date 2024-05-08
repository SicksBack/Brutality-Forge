/*    */ package Acrimony.module.impl.player;
/*    */ 
/*    */ import Acrimony.event.Listener;
/*    */ import Acrimony.event.impl.PostMotionEvent;
/*    */ import Acrimony.module.Category;
/*    */ import Acrimony.module.Module;
/*    */ import Acrimony.setting.AbstractSetting;
/*    */ import Acrimony.setting.impl.DoubleSetting;
/*    */ 
/*    */ public class Timer extends Module {
/* 11 */   private final DoubleSetting speed = new DoubleSetting("Speed", 1.1D, 0.1D, 5.0D, 0.1D);
/*    */   
/*    */   public Timer() {
/* 14 */     super("Timer", Category.PLAYER);
/* 15 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.speed });
/*    */   }
/*    */ 
/*    */   
/*    */   public void onDisable() {
/* 20 */     mc.timer.timerSpeed = 1.0F;
/*    */   }
/*    */   
/*    */   @Listener
/*    */   public void onPostMotion(PostMotionEvent event) {
/* 25 */     mc.timer.timerSpeed = (float)this.speed.getValue();
/*    */   }
/*    */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\player\Timer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */