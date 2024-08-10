package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {
    public BindCommand() {
        super("bind", "b");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            sendMessage("Usage: .bind <module> <key>");
            return;
        }

        String moduleName = args[0];
        String keyName = args[1];

        Module module = ModuleManager.getModuleByName(moduleName);
        if (module == null) {
            sendMessage("Module not found: " + moduleName);
            return;
        }

        int key = Keyboard.getKeyIndex(keyName.toUpperCase());
        if (key == Keyboard.KEY_NONE) {
            sendMessage("Invalid key: " + keyName);
            return;
        }

        module.setKey(key);
        sendMessage("Bound " + moduleName + " to " + keyName);
    }
}
