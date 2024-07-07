package org.brutality.ui.clickGui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.*;
import org.brutality.module.*;
import org.brutality.module.impl.render.ClickGuiModule;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.*;
import org.brutality.ui.font.CustomFontRenderer;
import org.brutality.ui.font.FontManager;
import org.brutality.utils.interfaces.*;
import org.brutality.utils.math.MathUtil;
import org.brutality.utils.render.GuiUtil;
import org.brutality.utils.render.RenderUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ClickGui extends GuiScreen implements MM, SM {
    public final ArrayList<ColorPicker> colorPickers = new ArrayList<>();
    public float x = 0;
    public float y = 0;
    public float width = 600.0F;
    public float height = 400.0F;
    public float moveX;
    public float moveY;
    public boolean moving = false;
    public Category currentCategory = Category.COMBAT;
    public Module selectedMod;
    public boolean changingSize;
    public float scrollAmountSettings = 0.0F;
    public float scollAmountModules = 0.0F;
    public NumberSetting hoveredSetting = null;
    public ClickGuiModule clickGuiModule;
    public InputSetting selectedInputSetting = null;
    public static final ArrayList<Character> allowedChars;

    static {
        allowedChars = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            allowedChars.add(c);
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            allowedChars.add(c);
        }
        for (char c = '0'; c <= '9'; c++) {
            allowedChars.add(c);
        }
        allowedChars.add('_');
        allowedChars.add('-');
        allowedChars.add('.');
        allowedChars.add(':');
        allowedChars.add(',');
        allowedChars.add(';');
        allowedChars.add('#');
        allowedChars.add('$');
        allowedChars.add(' ');
        allowedChars.add('!');
    }

    public ColorSetting selectedPicker;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        hoveredSetting = null;
        x = Math.max(0, x);
        y = Math.max(0, y);
        if (!Mouse.isButtonDown(0)) {
            this.moving = false;
        }

        if (this.clickGuiModule == null) {
            this.clickGuiModule = mm.getModuleByClass(ClickGuiModule.class);
        }

        if (this.changingSize) {
            float deltaY = (float)mouseY - (this.y + this.height);
            this.height = Math.max(250.0F, this.height + deltaY);
            float deltaX = (float)mouseX - (this.x + this.width);
            this.width = Math.max(450.0F, this.width + deltaX);
        }

        if (this.moving) {
            this.x = (float)mouseX - this.moveX;
            this.y = (float)mouseY - this.moveY;
        }

        CustomFontRenderer esp = FontManager.esp21;
        RenderUtil.drawRoundedRect(this.x, this.y, this.width, this.height, 7.0F, new Color(25, 25, 25, 180).getRGB());
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(this.x, this.y, this.width, 17.0);
        RenderUtil.drawRoundedRect(this.x, this.y, this.width, 25.0F, 7.0F, new Color(34, 34, 34).getRGB());
        drawWindowButtons(mouseX, mouseY);
        RenderUtil.endScissorBox();
        esp.drawString("CLICKGUI", this.x + 5.0F, this.y + 5.0F, new Color(200, 200, 200).getRGB());
        RenderUtil.drawRect(this.x + 110.0F, this.y + 0.5F, 2.0F, this.height, new Color(34, 34, 34).getRGB());
        RenderUtil.drawRect(this.x + 110.0F, this.y + 50.0F, this.width - 110.5F, 2.0F, new Color(34, 34, 34).getRGB());
        CustomFontRenderer consolas18 = FontManager.consolas18;
        CustomFontRenderer consolas15 = FontManager.consolas15;
        CustomFontRenderer small = FontManager.consolas17;
        float categoryXPos = this.x + 130.0F;

        for(Category category : Category.values()) {
            consolas18.drawString(category.name().toUpperCase(), categoryXPos, this.y + 30.0F, new Color(200, 200, 200).getRGB());
            if (category == this.currentCategory) {
                RenderUtil.drawRect(categoryXPos, this.y + 30.0F + consolas18.getFontHeight(), (float)consolas18.getStringWidth(category.name().toUpperCase()) - 0.5F, 1.0F, new Color(200, 200, 200).getRGB());
            }

            categoryXPos += (float)(consolas18.getStringWidth(category.name()) + 10);
        }

        int dWheel = Mouse.getDWheel();
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(this.x, this.y + 16.5F, 110.0, this.height - 16.5F);
        if (this.currentCategory != null) {
            if (GuiUtil.isHovered((float)mouseX, (float)mouseY, this.x, this.y + 16.5F, 110.0F, this.height - 16.5F)) {
                this.scollAmountModules = Math.min(0.0F, this.scollAmountModules + (float)dWheel / 10.0F);
            }

            float modY = this.y + 25.0F + this.scollAmountModules;

            for(Module module : mm.getModulesByCategory(currentCategory)) {
                consolas18.drawString((selectedMod == module ? "> " : "") + module.getName(), this.x + 10.0F, modY, module.isToggled() ? clickGuiModule.setting.getColor().getRGB() : new Color(200, 200, 200).getRGB());
                modY += consolas18.getFontHeight() + 5;
            }
        }

        RenderUtil.drawScissorBox(this.x + 112.0F, this.y + 52.5F, this.width - 111.5F, this.height - 51.5F);
        if (this.selectedMod != null) {
            float modY = this.y + 60.0F;
            if (GuiUtil.isHovered((float)mouseX, (float)mouseY, this.x + 111.5F, this.y + 52.5F, this.width - 111.5F, this.height - 51.5F)) {
                this.scrollAmountSettings = Math.min(0.0F, this.scrollAmountSettings + (float)dWheel / 10.0F);
            }
            modY += this.scrollAmountSettings;

            esp.drawString(this.selectedMod.getName(), this.x + 120.0F, modY, clickGuiModule.setting.getColor().getRGB());
            consolas15.drawString(this.selectedMod.getDescription(), this.x + 125.0F + esp.getStringWidth(this.selectedMod.getName()), modY, Color.gray.getRGB());
            modY += esp.getFontHeight() + 3;
            this.colorPickers.clear();
            for(Setting setting : sm.getValuesByMod(this.selectedMod)) {
                modY = renderSettings((float) mouseX, (float) mouseY, setting, consolas18, this.x, modY, small);
            }
            this.colorPickers.forEach(ColorPicker::draw);
        }

        RenderUtil.endScissorBox();
    }

    private float renderSettings(float mouseX, float mouseY, Setting setting, CustomFontRenderer consolas18, float x, float modY, CustomFontRenderer small) {
        if (setting.isVisible()) {
            if(setting instanceof ExtendableSetting) {
                final ExtendableSetting expandableValue = (ExtendableSetting) setting;
                final String name = expandableValue.getName() + " " + (expandableValue.isExpanded() ? ">" : "<");

                consolas18.drawString(name, x + 120.0F, modY, new Color(186,163,95,255).getRGB());
                modY += consolas18.getFontHeight() + 3;
                if (expandableValue.isExpanded()) {
                    for (Setting subSetting : expandableValue.getSubSettings()) {
                        modY = renderSettings(mouseX, mouseY, subSetting, consolas18, x + 10, modY, small);
                    }
                }
            } else if (setting instanceof BooleanSetting) {
                BooleanSetting checkBoxValue = (BooleanSetting) setting;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                consolas18.drawString(checkBoxValue.isEnabled() ? "true" : "false", x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": "), modY, checkBoxValue.isEnabled() ? new Color(0, 180, 0).getRGB() : new Color(180, 0, 0).getRGB());
                modY += consolas18.getFontHeight() + 2;
            } else if (setting instanceof NumberSetting) {
                NumberSetting sliderValue = (NumberSetting) setting;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                RenderUtil.drawBorder(x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": "), modY + 1f, x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ") + 120.0F, modY + 11.0F, 1.0F, new Color(34, 34, 34).getRGB(), true);
                float sliderX = x + 121.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");
                float sliderY = modY + 2.0F;
                float sliderWidth = 118.0F;
                float sliderHeight = 8.0F;
                float length = (float)MathHelper.floor_double((sliderValue.getValue() - sliderValue.getMinValue()) / (sliderValue.getMaxValue() - sliderValue.getMinValue()) * sliderWidth);
                RenderUtil.drawRect(sliderX, sliderY, length, sliderHeight, clickGuiModule.setting.getColor().getRGB());
                small.drawTotalCenteredString(String.valueOf(sliderValue.getValue()), sliderX + sliderWidth / 2.0F, sliderY + sliderHeight / 2.0F, new Color(200, 200, 200).getRGB());
                if (GuiUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                    hoveredSetting = sliderValue;
                }
                if (Mouse.isButtonDown(0) && GuiUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                    double min1 = sliderValue.getMinValue();
                    double max1 = sliderValue.getMaxValue();
                    double newValue = MathUtil.round((double) (mouseX - sliderX) * (max1 - min1) / (double) (sliderWidth - 1.0F) + min1, sliderValue.getDecimalPlaces());
                    sliderValue.setValue(newValue);
                }
                modY += consolas18.getFontHeight() + 2;
            } else if (setting instanceof SimpleModeSetting) {
                SimpleModeSetting stringBoxValue = (SimpleModeSetting) setting;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");

                for(String string : stringBoxValue.getOptions()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(string)) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");
                        modY += consolas18.getFontHeight() + 2;
                    }

                    consolas18.drawString(
                            string,
                            modeX,
                            modY,
                            (stringBoxValue.getSelected() != null && stringBoxValue.getSelected().equals(string)) ? clickGuiModule.setting.getColor().getRGB() : new Color(200, 200, 200).getRGB());
                    modeX += (float) consolas18.getStringWidth(string);
                    if (!stringBoxValue.getOptions()[stringBoxValue.getOptions().length - 1].equals(string)) {
                        consolas18.drawString(", ", modeX, modY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                modY += consolas18.getFontHeight() + 2;
            } else if (setting instanceof ModeSetting) {
                ModeSetting stringBoxValue = (ModeSetting) setting;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");

                for(String string : stringBoxValue.getNames()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(string)) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");
                        modY += consolas18.getFontHeight() + 2;
                    }

                    consolas18.drawString(
                            string,
                            modeX,
                            modY,
                            (stringBoxValue.getSelected() != null && stringBoxValue.getSelected().getName().equals(string)) ? clickGuiModule.setting.getColor().getRGB() : new Color(200, 200, 200).getRGB());
                    modeX += (float) consolas18.getStringWidth(string);
                    if (!stringBoxValue.getOptions()[stringBoxValue.getOptions().length - 1].getName().equals(string)) {
                        consolas18.drawString(", ", modeX, modY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                modY += consolas18.getFontHeight() + 2;

                for (Setting subSetting : stringBoxValue.getSettings().get(stringBoxValue.getSelected())) {
                    modY = renderSettings(mouseX, mouseY, subSetting, consolas18, x, modY, small);
                }
            } else if (setting instanceof MultiBooleanSetting) {
                MultiBooleanSetting stringBoxValue = (MultiBooleanSetting) setting;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");

                for(BooleanSetting bset : stringBoxValue.getSettings()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(bset.getName())) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ");
                        modY += consolas18.getFontHeight() + 2;
                    }

                    consolas18.drawString(
                            bset.getName(),
                            modeX,
                            modY,
                            bset.isEnabled() ? clickGuiModule.setting.getColor().getRGB() : new Color(200, 200, 200).getRGB());
                    modeX += (float) consolas18.getStringWidth(bset.getName());
                    if (!stringBoxValue.getSettings()[stringBoxValue.getSettings().length - 1].equals(bset)) {
                        consolas18.drawString(", ", modeX, modY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                modY += consolas18.getFontHeight() + 2;
            } else if (setting instanceof InputSetting) {
                RenderUtil.drawBorder(x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": "), modY, x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ") + 120.0F, modY + 10.0F, 1.0F, new Color(34, 34, 34).getRGB(), true);
                float inputX = x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ") + 1.0F;
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY, new Color(200, 200, 200).getRGB());
                small.drawString(((InputSetting) setting).getContent() + (selectedInputSetting == setting ? "_" : ""), inputX, modY, new Color(200, 200, 200).getRGB());
                modY += consolas18.getFontHeight() + 2;
            } else if (setting instanceof ColorSetting) {
                consolas18.drawString(setting.getName() + ": ", x + 120.0F, modY + 10, new Color(200, 200, 200).getRGB());
                modY += 60;
                RenderUtil.drawBorder(x + 120.0F, modY, x + 120.0F + small.getStringWidth("FFFFFFFF_") + 1.0F, modY + 10F, 1.0F, new Color(34, 34, 34).getRGB(), true);
                small.drawString(((ColorSetting) setting).getPicker().getCurrentColorHexInputString() + (selectedPicker == setting ? "_" : ""), x + 120.0F + 1.0F, modY, new Color(200, 200, 200).getRGB());
                ColorPicker picker = ((ColorSetting) setting).getPicker();
                picker.x = (int) Math.max(x + 120.0F + small.getStringWidth("FFFFFFFF_") + 70F, (int) (x + 120.0F + (float) consolas18.getStringWidth(setting.getName() + ": ") + 51.0F));
                picker.y = (int) modY;
                this.colorPickers.add(picker);
                modY += 60;
            }
        } return modY;
    }

    public void drawWindowButtons(float mx, float my) {
        int closeButtonColor = GuiUtil.isHovered(mx, my, this.x + width - 25, this.y, 25, 17.0F) ? new Color(255, 0, 0).getRGB() : new Color(34, 34, 34).getRGB();
        RenderUtil.drawRoundedRect(this.x + width - 25, this.y, 25, 25.0F, 7.0F, closeButtonColor);
        RenderUtil.drawRect(this.x + width - 25, this.y, 15F, 25.0F, closeButtonColor);
        CustomFontRenderer consolas18 = FontManager.arial18;
        consolas18.drawString("x", this.x + width - 15, this.y + 2, -1);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.colorPickers.forEach(colorPicker -> colorPicker.click(mouseX, mouseY, clickedMouseButton));
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.moving = false;
        this.changingSize = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char key, int code) throws IOException {
        if (code == 1) {
            this.mc.displayGuiScreen(null);
        } else if(selectedInputSetting != null) {
            if (code == Keyboard.KEY_BACK && !selectedInputSetting.getContent().isEmpty()) {
                selectedInputSetting.setContent(selectedInputSetting.getContent().substring(0, selectedInputSetting.getContent().length() - 1));
            } else {
                if(allowedChars.contains(key)) {
                    selectedInputSetting.setContent(selectedInputSetting.getContent() + key);
                }
            }
        } else if(selectedPicker != null) {
            if (code == Keyboard.KEY_BACK && !selectedPicker.getPicker().getCurrentColorHexInputString().isEmpty()) {
                selectedPicker.getPicker().setCurrentColorHexInputString(selectedPicker.getPicker().getCurrentColorHexInputString().substring(0, selectedPicker.getPicker().getCurrentColorHexInputString().length() - 1));
            } else {
                if(allowedChars.contains(key) && selectedPicker.getPicker().getCurrentColorHexInputString().length() <= 7) {
                    selectedPicker.getPicker().setCurrentColorHexInputString(selectedPicker.getPicker().getCurrentColorHexInputString() + key);
                }
            }
            selectedPicker.getPicker().setColor(selectedPicker.getPicker().stringToColor(selectedPicker.getPicker().getCurrentColorHexInputString()));
        } else if(hoveredSetting != null) {
            // JESUS CHRIST WTF DID I JUST CREATE
            double stepSize = 1.0 / Math.pow(10, hoveredSetting.getDecimalPlaces());
            hoveredSetting.setValue(MathUtil.round(Math.max(hoveredSetting.getMinValue(), Math.min(hoveredSetting.getMaxValue(), code == Keyboard.KEY_RIGHT ? hoveredSetting.getValue() + stepSize : code == Keyboard.KEY_LEFT ? hoveredSetting.getValue() - stepSize : hoveredSetting.getValue())), hoveredSetting.getDecimalPlaces()));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isHovered(mouseX, mouseY, this.x + width - 25, this.y, 25, 17.0F) && mouseButton == 0) {
            mc.displayGuiScreen(null);
            return;
        }

        CustomFontRenderer consolas18 = FontManager.consolas18;
        float categoryX = this.x + 110.0F + 20.0F;

        if (GuiUtil.isHovered((float)mouseX, (float)mouseY, this.x, this.y, this.width, 17.0F) && mouseButton == 0) {
            this.moving = true;
            this.moveX = (float)mouseX - this.x;
            this.moveY = (float)mouseY - this.y;
        }

        if (GuiUtil.isHovered((float)mouseX, (float)mouseY, this.x + this.width - 8.0F, this.y + this.height - 8.0F, 16.0F, 16.0F)) {
            this.changingSize = true;
        }

        for(Category category : Category.values()) {
            if (GuiUtil.isHovered((float)mouseX, (float)mouseY, categoryX, this.y + 30.0F, (float)consolas18.getStringWidth(category.name().toUpperCase()) - 0.5F, 1 + consolas18.getFontHeight())) {
                this.currentCategory = category;
                this.scrollAmountSettings = 0.0F;
                this.scollAmountModules = 0.0F;
            }

            categoryX += (float)(consolas18.getStringWidth(category.name()) + 10);
        }

        if (this.currentCategory != null) {
            float moduleY = this.y + 25.0F + this.scollAmountModules;

            for(Module module : mm.getModulesByCategory(this.currentCategory)) {
                if (GuiUtil.isHovered((float) mouseX, (float) mouseY, this.x + 10.0F, moduleY - 4.0F, (float) consolas18.getStringWidth(module.getName()) - 0.5F, consolas18.getFontHeight() + 4)) {
                    switch (mouseButton) {
                        case 0:
                            module.toggle();
                            break;
                        case 1:
                            this.selectedMod = module;
                    }
                }

                moduleY += consolas18.getFontHeight() + 5;
            }
        }

        CustomFontRenderer esp = FontManager.esp21;
        if (this.selectedMod != null) {
            float moduleY = this.y + 60.0F + this.scrollAmountSettings;
            esp.drawString(this.selectedMod.getName(), this.x + 120.0F, moduleY, -1);
            moduleY += esp.getFontHeight() + 3;
            selectedInputSetting = null;
            selectedPicker = null;
            for(Setting value : sm.getValuesByMod(this.selectedMod)) {
                moduleY = handleMouseInteraction((float) mouseX, (float) mouseY, value, this.x, moduleY, consolas18);
            }
            this.colorPickers.forEach(colorPicker -> colorPicker.click(mouseX, mouseY, mouseButton));
        }
    }

    private float handleMouseInteraction(float mouseX, float mouseY, Setting value, float x, float moduleY, CustomFontRenderer consolas18) {
        if (value.isVisible()) {
            if(value instanceof ExtendableSetting) {
                final ExtendableSetting expandableValue = (ExtendableSetting) value;
                final String name = expandableValue.getName() + " " + (expandableValue.isExpanded() ? "<" : ">");

                if (GuiUtil.isHovered(mouseX, mouseY, x + 120.0F, moduleY - 2.0F, (float) consolas18.getStringWidth(name), consolas18.getFontHeight())) {
                    expandableValue.setExpanded(!expandableValue.isExpanded());
                }

                moduleY += consolas18.getFontHeight() + 3;
                if (expandableValue.isExpanded()) {
                    for (Setting subSetting : expandableValue.getSubSettings()) {
                        moduleY = handleMouseInteraction(mouseX, mouseY, subSetting, x + 10, moduleY, consolas18);
                    }
                }

            } else if (value instanceof BooleanSetting) {
                BooleanSetting checkBoxValue = (BooleanSetting) value;
                if (GuiUtil.isHovered(mouseX, mouseY, x + 120.0F, moduleY - 2.0F, (float) consolas18.getStringWidth(checkBoxValue.getName() + ": " + (checkBoxValue.isEnabled() ? "true" : "false")), 10.0F)) {
                    checkBoxValue.setEnabled(!checkBoxValue.isEnabled());
                }

                moduleY += consolas18.getFontHeight() + 2;
            } else if (value instanceof NumberSetting) {
                moduleY += consolas18.getFontHeight() + 2;
            } else if (value instanceof InputSetting) {
                if (GuiUtil.isHovered(mouseX, mouseY, x + 120.0F, moduleY - 2.0F, 200, 10.0F)) {
                    selectedInputSetting = (InputSetting) value;
                }
                moduleY += consolas18.getFontHeight() + 2;
            } else if (value instanceof SimpleModeSetting) {
                SimpleModeSetting stringBoxValue = (SimpleModeSetting) value;
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");

                for(String string : stringBoxValue.getOptions()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(string)) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");
                        moduleY += consolas18.getFontHeight() + 2;
                    }

                    if (GuiUtil.isHovered(mouseX, mouseY, modeX, moduleY - 2.0F, (float) consolas18.getStringWidth(string), consolas18.getFontHeight())) {
                        stringBoxValue.setSelected(string);
                    }

                    modeX += (float) consolas18.getStringWidth(string);
                    if (!stringBoxValue.getOptions()[stringBoxValue.getOptions().length - 1].equals(string)) {
                        consolas18.drawString(", ", modeX, moduleY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                moduleY += consolas18.getFontHeight() + 2;
            } else if (value instanceof ModeSetting) {
                ModeSetting stringBoxValue = (ModeSetting) value;
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");

                for(String string : stringBoxValue.getNames()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(string)) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");
                        moduleY += consolas18.getFontHeight() + 2;
                    }

                    if (GuiUtil.isHovered(mouseX, mouseY, modeX, moduleY - 2.0F, (float) consolas18.getStringWidth(string), consolas18.getFontHeight())) {
                        stringBoxValue.setSelected(stringBoxValue.getByName(string));
                    }

                    modeX += (float) consolas18.getStringWidth(string);
                    if (!stringBoxValue.getOptions()[stringBoxValue.getOptions().length - 1].getName().equals(string)) {
                        consolas18.drawString(", ", modeX, moduleY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                moduleY += consolas18.getFontHeight() + 2;

                for (Setting subSetting : stringBoxValue.getSettings().get(stringBoxValue.getSelected())) {
                    moduleY = handleMouseInteraction(mouseX, mouseY, subSetting, x, moduleY, consolas18);
                }
            } else if (value instanceof MultiBooleanSetting) {
                MultiBooleanSetting multiBooleanSetting = (MultiBooleanSetting) value;
                float modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");

                for(BooleanSetting bset : ((MultiBooleanSetting) value).getSettings()) {
                    if (modeX >= x + this.width - consolas18.getStringWidth(bset.getName())) {
                        modeX = x + 120.0F + (float) consolas18.getStringWidth(value.getName() + ": ");
                        moduleY += consolas18.getFontHeight() + 2;
                    }

                    if (GuiUtil.isHovered(mouseX, mouseY, modeX, moduleY - 2.0F, (float) consolas18.getStringWidth(bset.getName()), consolas18.getFontHeight())) {
                        bset.setEnabled(!bset.isEnabled());
                    }

                    modeX += (float) consolas18.getStringWidth(bset.getName());
                    if (!multiBooleanSetting.getSettings()[multiBooleanSetting.getSettings().length - 1].equals(bset)) {
                        consolas18.drawString(", ", modeX, moduleY, new Color(200, 200, 200).getRGB());
                        modeX += (float) consolas18.getStringWidth(", ");
                    }
                }

                moduleY += consolas18.getFontHeight() + 2;
            } else if (value instanceof ColorSetting) {
                moduleY += 60;
                if (GuiUtil.isHovered(mouseX, mouseY, x + 120.0F, moduleY - 2.0F, 200, 10.0F)) {
                    selectedPicker = (ColorSetting) value;
                }
                moduleY += 60;
            }
        }
        return moduleY;
    }
}
