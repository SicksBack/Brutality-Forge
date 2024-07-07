package org.brutality.settings;

import lombok.Getter;
import lombok.Setter;
import org.brutality.module.Module;
import org.brutality.settings.impl.Mode;
import org.brutality.utils.interfaces.*;

@Getter
@Setter
public class Setting implements SM, MM {

    private String name;
    private Module parent;
    private boolean visible;

    public Setting(String name, Module parent) {
        this.name = name;
        this.parent = parent;
        this.visible = true;
        sm.add(this);
    }

    public Setting(String name, Mode<?> parent) {
        this.name = name;
        this.visible = true;
        /*
           IF YOUR CLIENT CRASHES HERE, YOU FORGOT TO INITIALIZE THE SETTINGS IN *setup* METHOD INSTEAD OF THE init
         */
        parent.getModeSettingParent().getSettings().get(parent).add(this);
    }
}
