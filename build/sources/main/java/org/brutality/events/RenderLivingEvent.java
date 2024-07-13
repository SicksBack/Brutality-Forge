package org.brutality.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderLivingEvent extends Event {

    private final Entity entity;
    private final double x;
    private final double y;
    private final double z;

    public RenderLivingEvent(Entity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Entity getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Cancelable
    public static class Pre extends RenderLivingEvent {
        public Pre(Entity entity, double x, double y, double z) {
            super(entity, x, y, z);
        }
    }

    public static class Post extends RenderLivingEvent {
        public Post(Entity entity, double x, double y, double z) {
            super(entity, x, y, z);
        }
    }
}
