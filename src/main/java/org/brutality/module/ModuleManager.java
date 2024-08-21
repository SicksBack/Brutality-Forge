package org.brutality.module;

import org.brutality.module.impl.World.*;
import org.brutality.module.impl.combat.*;
import org.brutality.module.impl.hypixel.WhoGotDogged;
import org.brutality.module.impl.movement.*;
import org.brutality.module.impl.pit.*;
import org.brutality.module.impl.player.*;
import org.brutality.module.impl.render.*;
import org.brutality.module.impl.harrys.*;
import lombok.Getter;
import org.brutality.module.impl.render.SharkDamage;
import org.brutality.module.impl.weasel.AutoOOF;
import org.brutality.module.impl.weasel.RequirementChecker;
import org.brutality.module.impl.weasel.StaffDetector;
import org.brutality.module.impl.weasel.WeaselGrinder;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ButtonSetting;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class ModuleManager extends ArrayList<Module> {
    public static ButtonSetting reach;

    public void init() {
        new RequirementChecker();
        new HUD();
        new Interface();
        new Criticals();
        new ClickGuiModule();// Register the KillAura module
        new PitSwap();
        new GambleSwapper();
        new KeepSprint();
        new Velocity();
        new NoSlow();
        new TelebowTimer();
        new Freecam();
        new Scaffold();
        new Speed();
        new LongJump();
        new Fly();
        new AntiBot();
        new Blink();
        new FakeLag();
        new FastPlace();
        new Chams();
        new NameTags();
        new KOS();
        new Friends();
        new VenomedTimer();
        new WhoGotDogged();
        new AutoAura();
        new AutoEgg();
        new AutoSteak();
        new SogeSwap();
        new AutoSpawn();
        new CakeAura();
        new SlotSwap();
        new Health();
        new ChestESP();
        new SewerESP();
        new MeteoriteTracker();
        new Sprint();
        new DarkNotifications();
        new DarkList();
        new NoHurtCam();
        new BountyList();
        new Events();
        new Focus();
        new Gamble();
        new HarryGrinder();
        new SharkDamage();
        new WeaselGrinder();
        new StaffDetector();
        new AutoOOF();
        new MindAssaultDamage();
        new PrestigeList();
        new DlegSwap();
        new PantSwapper();
        new VenomList();
        new PotionEffectsHUD();
        new TargetHUD();
        new SafeWalk();
        new AutoHeal();
        new Reach();
        new KillAura();
        new SetList();
    }

    public void updateSettings(Setting setting) {
        forEach(module -> module.updateSettings(setting));
    }

    public <V extends Module> V getModuleByClass(Class<V> clazz) {
        Module module = stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        return module == null ? null : clazz.cast(module);
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        return stream().filter(module -> module.getCategory().equals(category)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Module> getModules() {
        return new ArrayList<>(this);
    }
}
