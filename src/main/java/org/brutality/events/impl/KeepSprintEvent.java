package org.brutality.events.impl;

import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.entity.EntityLivingBase;

public class KeepSprintEvent extends LivingEvent.LivingUpdateEvent {
    public KeepSprintEvent(EntityLivingBase entity) {
        super(entity);
    }
}
