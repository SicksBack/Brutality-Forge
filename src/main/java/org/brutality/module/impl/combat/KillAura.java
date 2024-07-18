package org.brutality.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.events.SendPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.RotationUtils;
import org.brutality.utils.Utils;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class KillAura extends Module {
    public static EntityLivingBase target;
    private final NumberSetting aps = new NumberSetting("APS", this, 10, 1, 20, 1);
    private final NumberSetting attackRange = new NumberSetting("Range (attack)", this, 3, 3, 6, 1);
    private final NumberSetting swingRange = new NumberSetting("Range (swing)", this, 3.3, 3, 8, 1);
    private final NumberSetting blockRange = new NumberSetting("Range (block)", this, 6, 3, 12, 1);
    private final BooleanSetting targetInvis = new BooleanSetting("Target invis", this, true);
    private final BooleanSetting disableInInventory = new BooleanSetting("Disable in inventory", this, true);
    private final BooleanSetting disableWhileBlocking = new BooleanSetting("Disable while blocking", this, false);
    private final BooleanSetting hitThroughBlocks = new BooleanSetting("Hit through blocks", this, true);
    private final BooleanSetting ignoreTeammates = new BooleanSetting("Ignore teammates", this, true);
    private final BooleanSetting weaponOnly = new BooleanSetting("Weapon only", this, false);
    private final String[] sortModes = {"Health", "Hurttime", "Distance", "Yaw"};
    private final NumberSetting sortMode = new NumberSetting("Sort mode", this, 0, 0, sortModes.length - 1, 1);

    private Minecraft mc = Minecraft.getMinecraft();
    private List<EntityLivingBase> availableTargets = new ArrayList<>();
    public AtomicBoolean block = new AtomicBoolean();
    private long lastSwitched = System.currentTimeMillis();
    private boolean switchTargets;
    private byte entityIndex;
    private Random rand = new Random();
    private boolean attack;
    private boolean blocking;
    public boolean blinking;
    private ConcurrentLinkedQueue<Packet<?>> blinkedPackets = new ConcurrentLinkedQueue<>();
    private long i, j, k, l;
    private double m;
    private boolean n;

    public KillAura() {
        super("KillAura", "Automatically attacks entities for you", Category.COMBAT);
        this.addSettings(aps, attackRange, swingRange, blockRange, targetInvis, disableInInventory, disableWhileBlocking, hitThroughBlocks, ignoreTeammates, weaponOnly, sortMode);
    }


    public void onEnable() {
        this.rand = new Random();
    }


    public void onDisable() {
        this.resetVariables();
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent ev) {
        if (!Utils.nullCheck() && ev.phase == TickEvent.Phase.START && this.canAttack()) {
            this.attack = true;
        }
    }

    @SubscribeEvent
    public void onPreUpdate(TickEvent.PlayerTickEvent e) {
        if (!this.basicCondition() || !this.settingCondition()) {
            this.resetVariables();
            return;
        }
        this.block();
        if (this.block.get() && Utils.holdingSword()) {
            this.setBlockState(this.block.get(), false, false);
            if (this.attack) {
                this.attack = false;
                this.switchTargets = true;
                Utils.attackEntity(target, true, false);
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
            }
        }
        if (target == null) {
            return;
        }
        if (this.attack) {
            this.attack = false;
            this.switchTargets = true;
            Utils.attackEntity(target, true, false);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPreMotion(TickEvent.PlayerTickEvent e) {
        if (!this.basicCondition() || !this.settingCondition()) {
            this.resetVariables();
            return;
        }
        this.setTarget();
        if (target != null) {
            float[] rotations = RotationUtils.getRotations(target);
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
        }
    }

    @SubscribeEvent
    public void onPostMotion(TickEvent.PlayerTickEvent e) {
        if (this.block.get() && Utils.holdingSword()) {
            mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onSendPacket(SendPacketEvent e) {
        if (!Utils.nullCheck() || !this.blinking) {
            return;
        }
        Packet<?> packet = e.getPacket();
        if (packet.getClass().getSimpleName().startsWith("S")) {
            return;
        }
        this.blinkedPackets.add(packet);
        e.setCanceled(true);
    }

    @SubscribeEvent
    public void onMouse(MouseEvent mouseEvent) {
        if (mouseEvent.button == 0 && mouseEvent.buttonstate) {
            if (target != null) {
                mouseEvent.setCanceled(true);
            }
        }
    }


    public String getInfo() {
        return sortModes[(int) sortMode.getValue()];
    }

    private void resetVariables() {
        target = null;
        this.availableTargets.clear();
        this.block.set(false);
        this.attack = false;
        this.i = 0L;
        this.j = 0L;
        this.resetBlinkState(true);
    }

    private void block() {
        if (!this.block.get() && !this.blocking) {
            return;
        }
        if (!Utils.holdingSword()) {
            this.block.set(false);
        }
        this.setBlockState(this.block.get(), true, true);
    }

    private void setBlockState(boolean state, boolean sendBlock, boolean sendUnBlock) {
        if (Utils.holdingSword()) {
            if (sendBlock && !this.blocking && state) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
            } else if (sendUnBlock && this.blocking && !state) {
                this.unBlock();
            }
        }
        this.blocking = state;
    }

    private void setTarget() {
        this.availableTargets.clear();
        this.block.set(false);
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase && entity != mc.thePlayer && entity instanceof EntityPlayer && mc.thePlayer.getDistanceToEntity(entity) <= attackRange.getValue() && (targetInvis.isEnabled() || !entity.isInvisible()) && mc.thePlayer.canEntityBeSeen(entity)) {
                this.availableTargets.add((EntityLivingBase) entity);
            }
        }
        if (!this.availableTargets.isEmpty()) {
            Comparator<EntityLivingBase> comparator = Comparator.comparingDouble(EntityLivingBase::getHealth);
            Collections.sort(this.availableTargets, comparator);
            target = this.availableTargets.get(0);
        } else {
            target = null;
        }
    }

    private boolean basicCondition() {
        return !Utils.nullCheck() && !mc.thePlayer.isDead && (mc.currentScreen == null || !disableInInventory.isEnabled());
    }

    private boolean settingCondition() {
        return (Utils.holdingWeapon() || !weaponOnly.isEnabled()) && (!mc.thePlayer.isUsingItem() || !disableWhileBlocking.isEnabled());
    }

    private boolean canAttack() {
        if (this.j > 0L && this.i > 0L) {
            if (System.currentTimeMillis() > this.j) {
                this.gd();
                return true;
            }
            if (System.currentTimeMillis() > this.i) {
                return false;
            }
        } else {
            this.gd();
        }
        return false;
    }

    public void gd() {
        double c = this.aps.getValue() + 0.4 * this.rand.nextDouble();
        long d = (int) Math.round(1000.0 / c);
        if (System.currentTimeMillis() > this.k) {
            if (!this.n && this.rand.nextInt(100) >= 85) {
                this.n = true;
                this.m = 1.1 + this.rand.nextDouble() * 0.15;
            } else {
                this.n = false;
            }
            this.k = System.currentTimeMillis() + 500L + this.rand.nextInt(1500);
        }
        if (this.n) {
            d = (long) (d * this.m);
        }
        if (System.currentTimeMillis() > this.l) {
            if (this.rand.nextInt(100) >= 80) {
                d += 50L + this.rand.nextInt(100);
            }
            this.l = System.currentTimeMillis() + 500L + this.rand.nextInt(1500);
        }
        this.j = System.currentTimeMillis() + d;
        this.i = System.currentTimeMillis() + d / 2L - this.rand.nextInt(10);
    }

    private void unBlock() {
        if (!Utils.holdingSword()) {
            return;
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
    }

    public void resetBlinkState(boolean unblock) {
        this.releasePackets();
        this.blocking = false;
        if (unblock) {
            this.unBlock();
        }
    }

    private void releasePackets() {
        for (Packet<?> packet : this.blinkedPackets) {
            mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
        this.blinkedPackets.clear();
        this.blinking = false;
    }
}
