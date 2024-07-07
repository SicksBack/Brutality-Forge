package org.brutality.module;

import org.brutality.api.EventBus;
import org.brutality.module.impl.move.SprintModule;
import org.brutality.module.impl.render.ArrayListModule;
import org.brutality.module.impl.render.ClickGuiModule;
@@ -21,6 +22,8 @@ public void init() {
        new SprintModule();
        new TestModule();
        new HUDModule();
        new ArrayListModule();
        new ClickGuiModule();
        new KillauraModule();
        EventBus.getInstance().register(this);

        }

public void updateSettings(Setting setting) {
