/*     */ package com.example.examplemod.modules.modules.COMBAT;
/*     */
/*     */ import dev.huys.events.Event;
/*     */ import dev.huys.events.listeners.EventPacketRecieve;
/*     */ import dev.huys.modules.Module;
/*     */ import dev.huys.settings.BooleanSetting;
/*     */ import dev.huys.settings.ModeSetting;
/*     */ import dev.huys.settings.NumberSetting;
/*     */ import dev.huys.settings.Setting;
/*     */ import dev.huys.utils.Colors;
/*     */ import dev.huys.utils.Wrapper;
/*     */ import net.minecraft.network.Packet;
/*     */ import net.minecraft.network.play.server.S12PacketEntityVelocity;
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
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class Velocity
        /*     */   extends Module
        /*     */ {
    /*  39 */   public NumberSetting horizontal = new NumberSetting("Horizontal", 0.0D, 0.0D, 100.0D, 1.0D);
    /*  40 */   public NumberSetting vertical = new NumberSetting("Vertical", 100.0D, 0.0D, 100.0D, 1.0D);
    /*  41 */   public static ModeSetting mode = new ModeSetting("Mode", "Normal", new String[] { "Normal", "Hypixel" });
    /*  42 */   public BooleanSetting bug = new BooleanSetting("Debug", false);
    /*     */
    /*     */   public Velocity() {
        /*  45 */     super("Velocity", "None", 0, Module.Category.COMBAT, Boolean.valueOf(false));
        /*  46 */     addSettings(new Setting[] { (Setting)this.horizontal, (Setting)this.vertical, (Setting)mode, (Setting)this.bug });
        /*     */   }
    /*     */
    /*     */
    /*     */
    /*     */   public void onEnable() {}
    /*     */
    /*     */
    /*     */   public void onDisable() {}
    /*     */
    /*     */
    /*     */   public void onEvent(Event e) {
        /*  58 */     if (mode.getMode().equalsIgnoreCase("Normal")) {
            /*  59 */       setTag(String.valueOf(this.horizontal.getValue()) + "%, " + this.vertical.getValue() + "%");
            /*     */     } else {
            /*  61 */       setTag(mode.getMode());
            /*     */     }
        /*  63 */     if (e instanceof EventPacketRecieve &&
                /*  64 */       e.isPre() && !e.isCancelled()) {
            /*  65 */       (EventPacketRecieve)e; Packet packet = EventPacketRecieve.getPacket();
            /*  66 */       if (packet instanceof S12PacketEntityVelocity &&
                    /*  67 */         ((S12PacketEntityVelocity)packet).entityID == this.mc.thePlayer.getEntityId()) {
                /*  68 */         S12PacketEntityVelocity velocityPacket = (S12PacketEntityVelocity)packet;
                /*  69 */         double verticalPerc = this.vertical.getValue();
                /*  70 */         double horizontalPerc = this.horizontal.getValue();
                /*     */
                /*  82 */         if (this.bug.isEnabled()) {
                    /*  83 */           Wrapper.addChatMessage(String.valueOf(Colors.red) + "Sexxed on tick: " + this.mc.thePlayer.ticksExisted);
                    /*     */         }
                /*     */
                /*  86 */         if (mode.getMode().equals("Hypixel")) {
                    /*  87 */           this.mc.thePlayer.motionY = velocityPacket.motionY / 8000.0D;
                    /*  88 */           e.setCancelled(true);
                    /*     */         }
                /*     */
                /* 105 */         if (mode.getMode().equals("Normal")) {
                    /* 106 */           if (verticalPerc == 0.0D && horizontalPerc == 0.0D) {
                        /* 107 */             e.setCancelled(true);
                        /*     */             return;
                        /*     */           }
                    /* 110 */           velocityPacket.motionX = (int)(velocityPacket.motionX * this.horizontal.getValue() / 100.0D);
                    /* 111 */           velocityPacket.motionY = (int)(velocityPacket.motionY * this.vertical.getValue() / 100.0D);
                    /* 112 */           velocityPacket.motionZ = (int)(velocityPacket.motionZ * this.horizontal.getValue() / 100.0D);
                    /*     */         }
                /*     */       }
            /*     */     }
        /*     */   }
    /*     */ }
