package org.brutality.module.impl.combat;

import lombok.Getter;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;

@Getter
public class Criticals extends Module {
    private final SimpleModeSetting modeSetting;

    public Criticals() {
        super("Criticals", "Deal Crit Damage automatically", Category.COMBAT);
        this.modeSetting = new SimpleModeSetting("Mode", this, "Jump", new String[]{"Jump"});
    }

    // todo: use modeSetting.is("Jump") if it's enabled automatically jump on attack.
    @SubscribeEvent
    public void onAttack(AttackEntityEvent event) {
        if (modeSetting.is("Jump") && mc.thePlayer != null && mc.thePlayer.onGround) {
            mc.thePlayer.jump();
        }
    }
}
