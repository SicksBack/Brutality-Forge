package org.brutality.mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.8.9")
public class MixinLoader implements IFMLLoadingPlugin {

    // Constructor to initialize the Mixin system
    public MixinLoader() {
        MixinBootstrap.init();
        // Add your mixin configuration
        Mixins.addConfiguration("mixins.brutality.json");
        // Set the environment side to client
        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];  // No ASM transformers needed for now
    }

    @Override
    public String getModContainerClass() {
        return null;  // No ModContainer class needed
    }

    @Override
    public String getSetupClass() {
        return null;  // No setup class needed
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // No specific data injection needed
    }

    @Override
    public String getAccessTransformerClass() {
        return null;  // No access transformer needed
    }
}