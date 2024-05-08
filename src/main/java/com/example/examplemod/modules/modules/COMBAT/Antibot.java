/*    */ package com.example.examplemod.modules.modules.COMBAT;
/*    */ 
/*    */ import Acrimony.Acrimony;
/*    */ import Acrimony.module.Category;
/*    */ import Acrimony.module.Module;
/*    */ import Acrimony.setting.AbstractSetting;
/*    */ import Acrimony.setting.impl.BooleanSetting;
/*    */ import Acrimony.setting.impl.IntegerSetting;
/*    */ import Acrimony.util.misc.LogUtil;
/*    */ import net.minecraft.entity.EntityLivingBase;
/*    */ 
/*    */ public class Antibot extends Module {
/* 13 */   private final IntegerSetting ticksExisted = new IntegerSetting("Ticks existed", 30, 0, 100, 5);
/* 14 */   public final BooleanSetting advancedDetection = new BooleanSetting("Advanced detection", true);
/*    */   
/* 16 */   public final BooleanSetting debug = new BooleanSetting("Debug", false);
/*    */   
/*    */   private Killaura killauraModule;
/*    */   
/*    */   public Antibot() {
/* 21 */     super("Antibot", Category.COMBAT);
/* 22 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.ticksExisted, (AbstractSetting)this.advancedDetection, (AbstractSetting)this.debug });
/*    */   }
/*    */ 
/*    */   
/*    */   public void onClientStarted() {
/* 27 */     this.killauraModule = (Killaura)Acrimony.instance.getModuleManager().getModule(Killaura.class);
/*    */   }
/*    */   
/*    */   public boolean canAttack(EntityLivingBase entity, Module module) {
/* 31 */     if (!isEnabled()) return true;
/*    */     
/* 33 */     if (entity.ticksExisted < this.ticksExisted.getValue()) {
/* 34 */       if (this.debug.isEnabled() && module == this.killauraModule) {
/* 35 */         LogUtil.addChatMessage("Ticks existed antibot : prevented from hitting : " + entity.ticksExisted);
/*    */       }
/*    */       
/* 38 */       return false;
/*    */     } 
/*    */     
/* 41 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\combat\Antibot.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */