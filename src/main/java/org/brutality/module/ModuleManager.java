package org.brutality.module;

import org.brutality.module.impl.combat.AntiBot;
import org.brutality.module.impl.combat.*;
import org.brutality.module.impl.misc.*;
import org.brutality.module.impl.movement.*;
import org.brutality.module.impl.player.*;
import org.brutality.module.impl.render.*;
import lombok.Getter;
import org.brutality.module.impl.render.SharkDamage;
import org.brutality.module.impl.player.AutoOOF;
import org.brutality.module.impl.player.RequirementChecker;
import org.brutality.module.impl.player.StaffDetector;
import org.brutality.module.impl.player.WeaselGrinder;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ButtonSetting;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class ModuleManager extends ArrayList<Module> {

    // Method to get the singleton instance
    @Getter
    private static final ModuleManager instance = new ModuleManager();  // Singleton instance

    public static ButtonSetting reach;

    public void init() {
        // Initialize all modules here
        new RequirementChecker();
        new HUD();
        new Interface();
        new Criticals();
        new ClickGuiModule();
        new PitSwap();
        new GambleSwapper();
        new KeepSprint();
        new Velocity();  // Ensure this class exists
        new NoSlow();    // Ensure this class exists
        new TelebowTimer();
        new Speed();
        new LongJump();
        new Fly();
        new AntiBot();
        new Blink();
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
        new NoHurtCam();
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
        new PantSwapper();
        new VenomList();
        new PotionEffectsHUD();
        new TargetHUD();
        new SafeWalk();
        new AutoHeal();
        new Reach();
        new KillAura();
        new StreakingInfo();
        new Cps();
        new Fps();
        new Notifications();
        new AuraTimer();
        new EggTimer();
        new FeastTimer();
        new PullbowTimer();
        new AutoClicker();
        new MiddleClickFriends();
        new HemorrhageTimer();
        new AutoMath();
        new LowLifeWarning();
        new BanTracker();
    }

    public void updateSettings(Setting setting) {
        forEach(module -> module.updateSettings(setting));
    }

    public <V extends Module> V getModuleByClass(Class<V> clazz) {
        Module module = stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        return module == null ? null : clazz.cast(module);
    }

    public Module getModuleByName(String name) {
        Optional<Module> module = stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst();
        return module.orElse(null);
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        return stream().filter(module -> module.getCategory().equals(category)).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Module> getModules() {
        return new ArrayList<>(this);
    }
}
