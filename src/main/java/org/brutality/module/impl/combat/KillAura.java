package org.brutality.module.impl.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.brutality.events.Event;
import org.brutality.events.listeners.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EntityUtils;

import net.minecraft.entity.EntityLivingBase;

public class KillAura extends Module {

    private NumberSetting attackRange = new NumberSetting("Attack Range", this, 4.0, 1.0, 6.0, 1);
    private NumberSetting swingRange = new NumberSetting("Swing Range", this, 4.0, 1.0, 6.0, 1);
    private BooleanSetting targetPlayers = new BooleanSetting("Target Players", this, true);
    private BooleanSetting targetMobs = new BooleanSetting("Target Mobs", this, true);

    public KillAura() {
        super("KillAura", "Automatically attacks entities around you", Category.COMBAT);
        addSettings(attackRange, swingRange, targetPlayers, targetMobs);
    }


    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            onUpdate((EventUpdate) event);
        }
    }

    public void onUpdate(EventUpdate event) {
        List<EntityLivingBase> targets = mc.theWorld.loadedEntityList.stream()
                .filter(EntityUtils::isValidTarget)
                .filter(entity -> entity.getDistanceToEntity(mc.thePlayer) <= attackRange.getValue())
                .map(entity -> (EntityLivingBase) entity)
                .sorted(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer)))
                .collect(Collectors.toList());

        if (!targets.isEmpty()) {
            EntityLivingBase target = targets.get(0);

            if (mc.thePlayer.getDistanceToEntity(target) <= swingRange.getValue()) {
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, target);
            }
        }
    }
}
