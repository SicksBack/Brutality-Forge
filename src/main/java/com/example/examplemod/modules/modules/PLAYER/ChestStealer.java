/*    */ package Acrimony.module.impl.player;
/*    */ 
/*    */ import Acrimony.Acrimony;
/*    */ import Acrimony.event.Listener;
/*    */ import Acrimony.event.impl.UpdateEvent;
/*    */ import Acrimony.module.Category;
/*    */ import Acrimony.module.Module;
/*    */ import Acrimony.setting.AbstractSetting;
/*    */ import Acrimony.setting.impl.BooleanSetting;
/*    */ import Acrimony.setting.impl.IntegerSetting;
/*    */ import net.minecraft.block.Block;
/*    */ import net.minecraft.entity.player.EntityPlayer;
/*    */ import net.minecraft.inventory.ContainerChest;
/*    */ import net.minecraft.item.ItemStack;
/*    */ import net.minecraft.util.BlockPos;
/*    */ 
/*    */ public class ChestStealer
/*    */   extends Module {
/* 19 */   private final IntegerSetting delay = new IntegerSetting("Delay", 1, 0, 10, 1);
/* 20 */   private final BooleanSetting filter = new BooleanSetting("Filter", true);
/* 21 */   public static final BooleanSetting silent = new BooleanSetting("Silent", false);
/* 22 */   private final BooleanSetting autoClose = new BooleanSetting("Autoclose", true);
/* 23 */   private final BooleanSetting guiDetect = new BooleanSetting("Gui detect", true);
/*    */   
/*    */   private int counter;
/*    */   
/*    */   private InventoryManager invManager;
/*    */   
/*    */   public ChestStealer() {
/* 30 */     super("Chest Stealer", Category.PLAYER);
/* 31 */     addSettings(new AbstractSetting[] { (AbstractSetting)this.delay, (AbstractSetting)this.filter, (AbstractSetting)this.autoClose, (AbstractSetting)this.guiDetect, (AbstractSetting)silent });
/*    */   }
/*    */ 
/*    */   
/*    */   public void onClientStarted() {
/* 36 */     this.invManager = (InventoryManager)Acrimony.instance.getModuleManager().getModule(InventoryManager.class);
/*    */   }
/*    */   
/*    */   @Listener
/*    */   public void onUpdate(UpdateEvent event) {
/* 41 */     if (mc.thePlayer.openContainer != null && mc.thePlayer.openContainer instanceof ContainerChest && (!isGUI() || !this.guiDetect.isEnabled())) {
/* 42 */       ContainerChest container = (ContainerChest)mc.thePlayer.openContainer;
/*    */       
/* 44 */       for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
/* 45 */         ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);
/* 46 */         if (stack != null && !isUseless(stack) && 
/* 47 */           ++this.counter > this.delay.getValue()) {
/* 48 */           mc.playerController.windowClick(container.windowId, i, 1, 1, (EntityPlayer)mc.thePlayer);
/* 49 */           this.counter = 0;
/*    */           
/*    */           return;
/*    */         } 
/*    */       } 
/*    */       
/* 55 */       if (this.autoClose.isEnabled() && isChestEmpty(container)) {
/* 56 */         mc.thePlayer.closeScreen();
/*    */       }
/*    */     } 
/*    */   }
/*    */   
/*    */   private boolean isChestEmpty(ContainerChest container) {
/* 62 */     for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
/* 63 */       ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);
/*    */       
/* 65 */       if (stack != null && !isUseless(stack)) {
/* 66 */         return false;
/*    */       }
/*    */     } 
/*    */     
/* 70 */     return true;
/*    */   }
/*    */   
/*    */   private boolean isUseless(ItemStack stack) {
/* 74 */     if (!this.filter.isEnabled()) {
/* 75 */       return false;
/*    */     }
/*    */     
/* 78 */     return this.invManager.isUseless(stack);
/*    */   }
/*    */   
/*    */   private boolean isGUI() {
/* 82 */     for (double x = mc.thePlayer.posX - 5.0D; x <= mc.thePlayer.posX + 5.0D; x++) {
/* 83 */       for (double y = mc.thePlayer.posY - 5.0D; y <= mc.thePlayer.posY + 5.0D; y++) {
/* 84 */         double z; for (z = mc.thePlayer.posZ - 5.0D; z <= mc.thePlayer.posZ + 5.0D; z++) {
/*    */           
/* 86 */           BlockPos pos = new BlockPos(x, y, z);
/* 87 */           Block block = mc.theWorld.getBlockState(pos).getBlock();
/*    */           
/* 89 */           if (block instanceof net.minecraft.block.BlockChest || block instanceof net.minecraft.block.BlockEnderChest) {
/* 90 */             return false;
/*    */           }
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 96 */     return true;
/*    */   }
/*    */ }


/* Location:              C:\Users\ASUS\Downloads\Acrimony v0.1.1\Acrimony v0.1.1\Acrimony v0.1.1.jar!\Acrimony\module\impl\player\ChestStealer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */