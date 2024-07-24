package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.SimpleModeSetting;
import org.brutality.ui.font.CustomFontRenderer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HUD extends Module {
    public final ColorSetting colorSetting = new ColorSetting("Color", this, Color.WHITE);
    public final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    public final BooleanSetting watermarkSetting = new BooleanSetting("Watermark", this, true); // Watermark setting
    public final NumberSetting spacingSetting = new NumberSetting("Spacing", this, 1, 1, 10, 1);
    public final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.5, 3.0, 1); // New scale setting
    public final SimpleModeSetting fontSetting = new SimpleModeSetting("Font", this, "Smooth", new String[]{"Minecraft", "Smooth"});
    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Mode", this, "VapeV4", new String[]{"VapeV4", "PrimeCheats"});

    private int posX = 10, posY = 10;
    private int dragX, dragY;
    private boolean dragging = false;
    private CustomFontRenderer smoothFontRenderer;
    private CustomFontRenderer bigFontRenderer;
    private Minecraft mc = Minecraft.getMinecraft();
    private final ResourceLocation vapeV4Image = new ResourceLocation("brutality/font/textures/vape.png");
    private final ResourceLocation boxImage = new ResourceLocation("brutality/font/textures/box.png");

    private final List<Color> colors = Arrays.asList(Color.GREEN, Color.YELLOW, Color.RED, new Color(255, 105, 180), Color.CYAN);
    private Color randomColor; // To hold the random color

    public HUD() {
        super("HUD", "Shows a list of toggled modules", Category.RENDER);
        setKey(Keyboard.KEY_P);
        smoothFontRenderer = new CustomFontRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
        bigFontRenderer = new CustomFontRenderer(new Font("Arial", Font.BOLD, 24), true, true);
        addSettings(colorSetting, backgroundSetting, watermarkSetting, spacingSetting, scaleSetting, fontSetting, modeSetting);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        // Handle dragging
        if (mc.currentScreen != null) {
            handleDragging();
        }

        if (modeSetting.getValue().equals("VapeV4")) {
            renderVapeV4Mode();
        } else if (modeSetting.getValue().equals("PrimeCheats")) {
            renderPrimeCheatsMode();
        }
    }

    private void renderVapeV4Mode() {
        int color = colorSetting.getColor().getRGB();
        int spacing = (int) spacingSetting.getValue(); // Retrieve spacing value
        double scale = scaleSetting.getValue(); // Retrieve scale value

        // Render the "VapeV4" image if watermark is enabled
        if (watermarkSetting.isEnabled()) {
            drawImage(vapeV4Image, posX, posY);
        }

        int offsetY = posY + 20; // Adjusted to accommodate image height
        int y = offsetY;

        List<Module> toggledModules = mm.stream()
                .filter(Module::isToggled)
                .sorted((o1, o2) -> Integer.compare(smoothFontRenderer.getStringWidth(o2.getName()), smoothFontRenderer.getStringWidth(o1.getName())))
                .collect(Collectors.toList());

        if (backgroundSetting.isEnabled()) {
            Gui.drawRect(posX, offsetY, posX + 100, offsetY + toggledModules.size() * (int)smoothFontRenderer.getFontHeight(), new Color(0, 0, 0, 120).getRGB());
        }

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);

        if (fontSetting.getValue().equalsIgnoreCase("Smooth")) {
            for (Module m : toggledModules) {
                smoothFontRenderer.drawStringWithShadow(m.getName(), (posX + 2) / (float) scale, y / (float) scale, color);
                y += (smoothFontRenderer.getFontHeight() + spacing) * scale;
            }
        } else {
            for (Module m : toggledModules) {
                mc.fontRendererObj.drawStringWithShadow(m.getName(), (posX + 2) / (float) scale, y / (float) scale, color);
                y += (mc.fontRendererObj.FONT_HEIGHT + spacing) * scale;
            }
        }

        GL11.glPopMatrix();
    }

    private void renderPrimeCheatsMode() {
        int spacing = 1;
        double scale = 1.0;
        int boxWidth = 99;
        int boxHeight = 10;

        // Render the grey box
        Gui.drawRect(posX, posY, posX + boxWidth, posY + boxHeight, new Color(51, 51, 51, 255).getRGB()); // Grey color with full opacity

        // Render the "HUD" text
        mc.fontRendererObj.drawStringWithShadow("HUD", posX + (boxWidth / 2) - (mc.fontRendererObj.getStringWidth("HUD") / 2), posY + (boxHeight / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2), 0xFFFFFF); // White color

        List<Module> toggledModules = mm.stream()
                .filter(Module::isToggled)
                .sorted((o1, o2) -> Integer.compare(mc.fontRendererObj.getStringWidth(o2.getName()), mc.fontRendererObj.getStringWidth(o1.getName())))
                .collect(Collectors.toList());

        int y = posY + boxHeight + 2; // Start rendering modules below the box

        GL11.glPushMatrix();
        GL11.glScaled(scale, scale, scale);

        if (randomColor == null) { // Assign random color once
            Random random = new Random();
            randomColor = colors.get(random.nextInt(colors.size())); // Pick a random color for all modules
        }

        int color = randomColor.getRGB();

        for (Module m : toggledModules) {
            if (fontSetting.getValue().equalsIgnoreCase("Smooth")) {
                smoothFontRenderer.drawStringWithShadow(m.getName(), (posX + 5) / (float) scale, y / (float) scale, color);
            } else {
                mc.fontRendererObj.drawStringWithShadow(m.getName(), (posX + 5) / (float) scale, y / (float) scale, color);
            }
            y += mc.fontRendererObj.FONT_HEIGHT + spacing;
        }

        GL11.glPopMatrix();
    }

    private void handleDragging() {
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
            randomColor = null; // Reset color when dragging stops to reassign a random color next time
        }
    }

    private boolean isMouseOver() {
        int mouseX = Mouse.getX() / 2;
        int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getY() / 2;
        return mouseX >= posX && mouseX <= posX + 100 && mouseY >= posY && mouseY <= posY + 20;
    }

    private void drawImage(ResourceLocation resource, int x, int y) {
        mc.getTextureManager().bindTexture(resource);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 100, 20, 100, 20); // Adjust width and height as necessary
        GlStateManager.disableBlend();
    }
}
