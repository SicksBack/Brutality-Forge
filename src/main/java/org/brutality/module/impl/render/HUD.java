package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HUD extends Module {
    public final ColorSetting colorSetting = new ColorSetting("Color", this, Color.WHITE);
    public final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    public final BooleanSetting myauColorSetting = new BooleanSetting("Default PrimeCheats Color", this, true);
    public final SimpleModeSetting fontSetting = new SimpleModeSetting("Font", this, "Smooth", new String[]{"Minecraft", "Smooth"});
    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Mode", this, "PrimeCheats", new String[]{"PrimeCheats", "Myau"});
    public final SimpleModeSetting sortingSetting = new SimpleModeSetting("Sorting", this, "a-z", new String[]{"a-z", "category", "length"});

    private int posX = 10, posY = 10;
    private int dragX, dragY;
    private boolean dragging = false;
    private CustomFontRenderer smoothFontRenderer;
    private CustomFontRenderer bigFontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();

    private final List<Color> colors = Arrays.asList(Color.GREEN, Color.YELLOW, Color.RED, new Color(255, 105, 180), Color.CYAN);
    private List<Color> randomColors;

    public HUD() {
        super("HUD", "Shows a list of toggled modules", Category.RENDER);
        setKey(Keyboard.KEY_P);
        smoothFontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
        bigFontRenderer = new CustomFontRenderer(new Font("Arial", Font.BOLD, 24), true, true);
        addSettings(colorSetting, backgroundSetting, myauColorSetting, fontSetting, modeSetting, sortingSetting);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (modeSetting.getValue().equals("PrimeCheats")) {
            posX = sr.getScaledWidth() - 100;
            posY = 10;
            renderPrimeCheatsMode();
        } else if (modeSetting.getValue().equals("Myau")) {
            posX = sr.getScaledWidth() - 100;
            posY = 10;
            renderMyauMode();
        }

        // Handle dragging
        if (mc.currentScreen != null) {
            handleDragging();
        }
    }

    private void renderPrimeCheatsMode() {
        int color = colorSetting.getColor().getRGB();
        int boxWidth = 99;
        int boxHeight = 10;

        List<Module> toggledModules = getSortedModules();

        int y = posY + boxHeight + 2; // Start rendering modules below the box

        GL11.glPushMatrix();
        GL11.glTranslated(3, 0, 0); // Adjust position to the right

        if (randomColors == null || randomColors.size() != toggledModules.size()) {
            randomColors = generateRandomColors(toggledModules.size());
        }

        // Render the grey box
        Gui.drawRect(posX, posY, posX + boxWidth, posY + boxHeight, new Color(51, 51, 51, 255).getRGB()); // Grey color with full opacity

        // Render the "HUD" text
        mc.fontRendererObj.drawStringWithShadow("HUD", posX + (boxWidth / 2) - (mc.fontRendererObj.getStringWidth("HUD") / 2), posY + (boxHeight / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2), 0xFFFFFF); // White color

        if (backgroundSetting.isEnabled()) {
            Gui.drawRect(posX, posY + boxHeight + 2, posX + boxWidth, y + (toggledModules.size() + 1) * mc.fontRendererObj.FONT_HEIGHT, new Color(0, 0, 0, 51).getRGB()); // Black low opacity background (20% opacity)
        }

        for (int i = 0; i < toggledModules.size(); i++) {
            Module m = toggledModules.get(i);
            color = myauColorSetting.isEnabled() ? colorSetting.getColor().getRGB() : randomColors.get(i).getRGB();
            if (fontSetting.getValue().equalsIgnoreCase("Smooth")) {
                smoothFontRenderer.drawStringWithShadow(m.getName(), posX + 5, y, color);
            } else {
                mc.fontRendererObj.drawStringWithShadow(m.getName(), posX + 5, y, color);
            }
            y += mc.fontRendererObj.FONT_HEIGHT;
        }

        y += mc.fontRendererObj.FONT_HEIGHT; // Extra space at the bottom

        GL11.glPopMatrix();
    }

    private void renderMyauMode() {
        int color = colorSetting.getColor().getRGB();

        List<Module> toggledModules = getSortedModules();

        int y = posY;

        for (Module m : toggledModules) {
            if (fontSetting.getValue().equalsIgnoreCase("Smooth")) {
                smoothFontRenderer.drawStringWithShadow(m.getName(), posX + 2, y, color);
            } else {
                mc.fontRendererObj.drawStringWithShadow(m.getName(), posX + 2, y, color);
            }
            y += mc.fontRendererObj.FONT_HEIGHT;

            if (backgroundSetting.isEnabled()) {
                Gui.drawRect(posX, y - mc.fontRendererObj.FONT_HEIGHT, posX + mc.fontRendererObj.getStringWidth(m.getName()) + 4, y, new Color(0, 0, 0, 51).getRGB()); // Black low opacity background (20% opacity)
            }
        }
    }

    private List<Module> getSortedModules() {
        if (sortingSetting.getValue().equalsIgnoreCase("a-z")) {
            return mm.stream()
                    .filter(Module::isToggled)
                    .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
                    .collect(Collectors.toList());
        } else if (sortingSetting.getValue().equalsIgnoreCase("category")) {
            return mm.stream()
                    .filter(Module::isToggled)
                    .sorted((o1, o2) -> o1.getCategory().compareTo(o2.getCategory()))
                    .collect(Collectors.toList());
        } else {
            return mm.stream()
                    .filter(Module::isToggled)
                    .sorted((o1, o2) -> Integer.compare(o2.getName().length(), o1.getName().length()))
                    .collect(Collectors.toList());
        }
    }

    private List<Color> generateRandomColors(int size) {
        List<Color> randomColors = colors.stream().collect(Collectors.toList());
        Collections.shuffle(randomColors);
        while (randomColors.size() < size) {
            randomColors.addAll(colors);
        }
        return randomColors.subList(0, size);
    }

    private void handleDragging() {
        if (mc.currentScreen != null) { // Check if the current screen is a GUI
            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOver()) {
                    dragging = true;
                    dragX = Mouse.getX() / 2 - posX;
                    dragY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - posY;
                }
                if (dragging) {
                    posX = Mouse.getX() / 2 - dragX;
                    posY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2 - dragY;
                }
            } else {
                dragging = false;
            }
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= posX && mouseX <= posX + 100 && mouseY >= posY && mouseY <= posY + 20;
    }
}
