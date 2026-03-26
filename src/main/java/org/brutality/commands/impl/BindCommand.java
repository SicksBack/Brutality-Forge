package org.brutality.commands.impl;

import net.minecraft.client.Minecraft;
import org.brutality.BrutalityClient;
import org.brutality.commands.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.impl.BindSetting;
import org.brutality.utils.Wrapper;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public BindCommand() {
        super("bind", "Bind modules to keys", "bind <module> <key> | bind clear <module>", "b");
    }

    @Override
    public void onCommand(String[] args, String command) {
        ModuleManager moduleManager = BrutalityClient.getInstance().getModuleManager();

        if (args.length == 0) {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage("/bind <module> <key>");
            Wrapper.addChatMessage("/bind clear <module>");
            Wrapper.addChatMessage("/bind list");
            return;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("list")) {
            Wrapper.addChatMessage("--- Module Binds ---");
            for (Module module : moduleManager.getModules()) {
                if (module.getKey() != null && module.getKey().getKeyCode() != Keyboard.KEY_NONE) {
                    Wrapper.addChatMessage(module.getName() + ": " + Keyboard.getKeyName(module.getKey().getKeyCode()));
                }
            }
            Wrapper.addChatMessage("--------------------");
            return;
        }

        if (subCommand.equals("clear") && args.length >= 2) {
            String moduleName = args[1];
            Module module = moduleManager.getModuleByName(moduleName);

            if (module == null) {
                Wrapper.addChatMessage("Module not found: " + moduleName);
                return;
            }

            module.setKey(Keyboard.KEY_NONE);
            Wrapper.addChatMessage("Cleared bind for: " + moduleName);

            // Save config
            BrutalityClient.getInstance().getConfigManager().saveConfig();
            return;
        }

        if (args.length >= 2) {
            String moduleName = args[0];
            String keyName = args[1];

            Module module = moduleManager.getModuleByName(moduleName);
            if (module == null) {
                Wrapper.addChatMessage("Module not found: " + moduleName);
                return;
            }

            int keyCode;
            if (keyName.equalsIgnoreCase("none") || keyName.equalsIgnoreCase("clear")) {
                keyCode = Keyboard.KEY_NONE;
            } else {
                keyCode = Keyboard.getKeyIndex(keyName.toUpperCase());
                if (keyCode == Keyboard.KEY_NONE) {
                    Wrapper.addChatMessage("Invalid key: " + keyName);
                    Wrapper.addChatMessage("Use key names like: R, SHIFT, G, F1, etc.");
                    return;
                }
            }

            module.setKey(keyCode);
            String keyDisplayName = keyCode == Keyboard.KEY_NONE ? "NONE" : Keyboard.getKeyName(keyCode);
            Wrapper.addChatMessage("Bound " + moduleName + " to: " + keyDisplayName);

            // Save config
            BrutalityClient.getInstance().getConfigManager().saveConfig();
        } else {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage("/bind <module> <key>");
            Wrapper.addChatMessage("/bind clear <module>");
            Wrapper.addChatMessage("/bind list");
        }
    }
}
