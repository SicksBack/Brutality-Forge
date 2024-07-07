package org.brutality.module.impl.render;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Module;
import org.brutality.module.Category;
import org.brutality.settings.impl.*;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TestModule extends Module {
    public final BooleanSetting booleanSetting = new BooleanSetting("BooleanSetting", this, false);

    public final ColorSetting colorSetting = new ColorSetting("ColorSetting", this, new Color(255, 255, 255));

    public final InputSetting inputSetting = new InputSetting("InputSetting", this, "text");

    public final BooleanSetting booleanSubSetting1 = new BooleanSetting("Boolean1", this, false);
    public final BooleanSetting booleanSubSetting2 = new BooleanSetting("Boolean2", this, false);
    public final BooleanSetting booleanSubSetting3 = new BooleanSetting("Boolean3", this, false);
    public final MultiBooleanSetting multiBooleanSetting = new MultiBooleanSetting("MultiBooleanSetting", this, booleanSubSetting1, booleanSubSetting2, booleanSubSetting3);

    public final NumberSetting numberInt = new NumberSetting("NumberSetting (Integer)", this, 6, 0, 10, 0);
    public final NumberSetting numberFloat = new NumberSetting("NumberSetting (Float)", this, 0, 0, 10, 2);

    public final SimpleModeSetting simpleMode = new SimpleModeSetting("SimpleModeSetting", this, "German", new String[]{"German", "English", "Spanish"});

    public final ExtendableSetting extendableSetting = new ExtendableSetting("This is extendable!", this, numberInt, numberFloat, simpleMode);


    public TestModule() {
        super("Test module", "Test module to show usage", Category.RENDER);
        // Set keybinding
        this.setKey(Keyboard.KEY_B);
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }

    /**
     * Demonstrates the usage of Setting classes
     */
    public void usageOfSettings() {
        // Just a boolean
        System.out.println(booleanSetting.isEnabled());

        // This returns a color! If you need a integer (i hex representation of said color), do colorSetting.getColor().getRGB()
        System.out.println(colorSetting.getColor());
        System.out.println(colorSetting.getColor().getRGB());

        // Returns a String
        System.out.println(inputSetting.getContent());

        //
        System.out.println(booleanSubSetting1.isEnabled());
        System.out.println(booleanSubSetting2.isEnabled());
        System.out.println(booleanSubSetting3.isEnabled());

        // NumberSettings are just doubles, you need to cast them to get the desired type
        System.out.println((int) numberInt.getValue());
        System.out.println((float) numberFloat.getValue());

        // SimpleModeSettings are just strings but in a selection
        System.out.println(simpleMode.getSelected());
        System.out.println(simpleMode.is("English")); // You can check for a mode using this

        // Settings inside a ExtendableSetting stay the same - It's just for better looks
    }
}
