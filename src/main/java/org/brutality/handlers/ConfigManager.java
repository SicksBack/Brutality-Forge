package org.brutality.handlers;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import org.brutality.BrutalityClient;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.*;

import java.util.ArrayList;

import java.awt.*;
import java.io.*;
import java.util.Map;

public class ConfigManager {
    private final File configDir;
    private final File configFile;
    private final Gson gson;

    public ConfigManager() {
        this.configDir = new File(Minecraft.getMinecraft().mcDataDir, "BrutalityClient");
        this.configFile = new File(configDir, "config.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        if (!configDir.exists()) {
            configDir.mkdirs();
        }
    }

    public void saveConfig() {
        try {
            JsonObject config = new JsonObject();
            JsonObject modules = new JsonObject();

            for (Module module : BrutalityClient.getInstance().getModuleManager().getModules()) {
                JsonObject moduleObj = new JsonObject();

                // Save module state and keybind
                moduleObj.addProperty("enabled", module.isToggled());
                if (module.getKey() != null) {
                    moduleObj.addProperty("keybind", module.getKey().getKeyCode());
                }

                // Save settings
                JsonObject settings = new JsonObject();
                for (Setting setting : module.getSettings()) {
                    saveSetting(settings, setting);
                }
                moduleObj.add("settings", settings);
                modules.add(module.getName(), moduleObj);
            }

            config.add("modules", modules);

            FileWriter writer = new FileWriter(configFile);
            gson.toJson(config, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveSetting(JsonObject parent, Setting setting) {
        if (setting instanceof BooleanSetting) {
            parent.addProperty(setting.getName(), ((BooleanSetting) setting).isEnabled());
        } else if (setting instanceof NumberSetting) {
            parent.addProperty(setting.getName(), ((NumberSetting) setting).getValue());
        } else if (setting instanceof SimpleModeSetting) {
            parent.addProperty(setting.getName(), ((SimpleModeSetting) setting).getSelected());
        } else if (setting instanceof ModeSetting) {
            ModeSetting modeSetting = (ModeSetting) setting;
            JsonObject modeObj = new JsonObject();
            modeObj.addProperty("selected", modeSetting.getSelected().getName());

            // Save sub-settings for each mode
            JsonObject modes = new JsonObject();
            for (Map.Entry<Mode<?>, ArrayList<Setting>> entry : modeSetting.getSettings().entrySet()) {
                JsonObject modeSettings = new JsonObject();
                for (Setting subSetting : entry.getValue()) {
                    saveSetting(modeSettings, subSetting);
                }
                modes.add(entry.getKey().getName(), modeSettings);
            }
            modeObj.add("modes", modes);
            parent.add(setting.getName(), modeObj);
        } else if (setting instanceof ColorSetting) {
            ColorSetting colorSetting = (ColorSetting) setting;
            Color color = colorSetting.getColor();
            parent.addProperty(setting.getName(), String.format("%02X%02X%02X%02X",
                    color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
        } else if (setting instanceof InputSetting) {
            parent.addProperty(setting.getName(), ((InputSetting) setting).getContent());
        } else if (setting instanceof BindSetting) {
            parent.addProperty(setting.getName(), ((BindSetting) setting).getKeyCode());
        } else if (setting instanceof MultiBooleanSetting) {
            JsonObject multiObj = new JsonObject();
            for (BooleanSetting boolSetting : ((MultiBooleanSetting) setting).getSettings()) {
                multiObj.addProperty(boolSetting.getName(), boolSetting.isEnabled());
            }
            parent.add(setting.getName(), multiObj);
        } else if (setting instanceof ExtendableSetting) {
            ExtendableSetting extSetting = (ExtendableSetting) setting;
            JsonObject extObj = new JsonObject();
            extObj.addProperty("expanded", extSetting.isExpanded());

            JsonObject subSettings = new JsonObject();
            for (Setting subSetting : extSetting.getSubSettings()) {
                saveSetting(subSettings, subSetting);
            }
            extObj.add("subSettings", subSettings);
            parent.add(setting.getName(), extObj);
        }
    }

    public void loadConfig() {
        if (!configFile.exists()) {
            return;
        }

        try {
            FileReader reader = new FileReader(configFile);
            JsonObject config = gson.fromJson(reader, JsonObject.class);
            reader.close();

            if (config.has("modules")) {
                JsonObject modules = config.getAsJsonObject("modules");

                for (Module module : BrutalityClient.getInstance().getModuleManager().getModules()) {
                    if (modules.has(module.getName())) {
                        JsonObject moduleObj = modules.getAsJsonObject(module.getName());

                        // Load module state
                        if (moduleObj.has("enabled")) {
                            boolean enabled = moduleObj.get("enabled").getAsBoolean();
                            if (enabled != module.isToggled()) {
                                module.toggle();
                            }
                        }

                        // Load keybind
                        if (moduleObj.has("keybind")) {
                            int keyCode = moduleObj.get("keybind").getAsInt();
                            module.setKey(keyCode);
                        }

                        // Load settings
                        if (moduleObj.has("settings")) {
                            JsonObject settings = moduleObj.getAsJsonObject("settings");
                            for (Setting setting : module.getSettings()) {
                                loadSetting(settings, setting);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSetting(JsonObject parent, Setting setting) {
        if (!parent.has(setting.getName())) {
            return;
        }

        try {
            if (setting instanceof BooleanSetting) {
                boolean value = parent.get(setting.getName()).getAsBoolean();
                ((BooleanSetting) setting).setEnabled(value);
            } else if (setting instanceof NumberSetting) {
                double value = parent.get(setting.getName()).getAsDouble();
                ((NumberSetting) setting).setValue(value);
            } else if (setting instanceof SimpleModeSetting) {
                String value = parent.get(setting.getName()).getAsString();
                ((SimpleModeSetting) setting).setSelected(value);
            } else if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                JsonObject modeObj = parent.getAsJsonObject(setting.getName());

                if (modeObj.has("selected")) {
                    String selectedMode = modeObj.get("selected").getAsString();
                    modeSetting.setSelected(modeSetting.getByName(selectedMode));
                }

                if (modeObj.has("modes")) {
                    JsonObject modes = modeObj.getAsJsonObject("modes");
                    for (Map.Entry<Mode<?>, ArrayList<Setting>> entry : modeSetting.getSettings().entrySet()) {
                        if (modes.has(entry.getKey().getName())) {
                            JsonObject modeSettings = modes.getAsJsonObject(entry.getKey().getName());
                            for (Setting subSetting : entry.getValue()) {
                                loadSetting(modeSettings, subSetting);
                            }
                        }
                    }
                }
            } else if (setting instanceof ColorSetting) {
                String colorHex = parent.get(setting.getName()).getAsString();
                try {
                    long colorValue = Long.parseLong(colorHex, 16);
                    Color color = new Color((int) colorValue, true);
                    ((ColorSetting) setting).setColor(color);
                } catch (NumberFormatException e) {
                    // Invalid color format, skip
                }
            } else if (setting instanceof InputSetting) {
                String value = parent.get(setting.getName()).getAsString();
                ((InputSetting) setting).setContent(value);
            } else if (setting instanceof BindSetting) {
                int keyCode = parent.get(setting.getName()).getAsInt();
                ((BindSetting) setting).setKeyCode(keyCode);
            } else if (setting instanceof MultiBooleanSetting) {
                JsonObject multiObj = parent.getAsJsonObject(setting.getName());
                for (BooleanSetting boolSetting : ((MultiBooleanSetting) setting).getSettings()) {
                    if (multiObj.has(boolSetting.getName())) {
                        boolSetting.setEnabled(multiObj.get(boolSetting.getName()).getAsBoolean());
                    }
                }
            } else if (setting instanceof ExtendableSetting) {
                ExtendableSetting extSetting = (ExtendableSetting) setting;
                JsonObject extObj = parent.getAsJsonObject(setting.getName());

                if (extObj.has("expanded")) {
                    extSetting.setExpanded(extObj.get("expanded").getAsBoolean());
                }

                if (extObj.has("subSettings")) {
                    JsonObject subSettings = extObj.getAsJsonObject("subSettings");
                    for (Setting subSetting : extSetting.getSubSettings()) {
                        loadSetting(subSettings, subSetting);
                    }
                }
            }
        } catch (Exception e) {
            // Skip invalid settings
        }
    }
}