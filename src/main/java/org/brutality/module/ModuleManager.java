package org.brutality.module;

import org.brutality.module.impl.misc.*;
import org.brutality.module.impl.misc.PodSwap;
import org.brutality.module.impl.movement.*;
import org.brutality.module.impl.render.*;
import lombok.Getter;
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


    public void init() {
        // Initialize all modules here
        new HUD();
        new Interface();
        new ClickGuiModule();
        new KeepSprint();
        new NoSlow();
        new TelebowTimer();
        new Speed();
        new LongJump();
        new Fly();
        new Chams();
        new NameTags();
        new VenomedTimer();
        new WhoGotDogged();
        new Health();
        new ChestESP();
        new MeteoriteTracker();
        new Sprint();
        new Events();
        new Focus();
        new Gamble();
        new HarryGrinder();
        new SharkDamage();
        new PrestigeList();
        new VenomList();
        new PotionEffectsHUD();
        new TargetHUD();
        new SafeWalk();
        new StreakingInfo();
        new Cps();
        new Fps();
        new Notifications();
        new AuraTimer();
        new EggTimer();
        new FeastTimer();
        new PullbowTimer();
        new BanTracker();
        new SprintDebug();
        new ChatCopy();
        new PodSwap();
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
