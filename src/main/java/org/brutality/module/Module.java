package org.brutality.module;

import org.brutality.api.EventBus;
import org.brutality.notifications.NotificationManager;
import lombok.Getter;
import lombok.Setter;

@@ -45,20 +46,25 @@ public void toggle() {
        } else {
        this.onDisable();
        }

        if (toggled) {
        EventBus.getInstance().register(this);
        } else {
        EventBus.getInstance().unregister(this);
        }

        }

// Registers the module onto the event bus
public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println("Enabled: " + this.name);
        NotificationManager.sendNotification("Enabled " + this.name);
        }

// Unregisters the module from the event bus
public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        System.out.println("Disabled: " + this.name);
        NotificationManager.sendNotification("Disabled " + this.name);
        }

public void updateSettings(Setting s) {}
