package org.brutality.command.impl;

import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.Wrapper;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("Bind", "Binds a module to a key", "bind <name> <key> | clear", "b");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            Wrapper.addChatMessage(Wrapper.Colors.red + "[B] - " + Wrapper.Colors.white + "Usage: " + this.getSyntax());
            return;
        }

        if (args.length == 2) {
            String moduleName = args[0];
            String keyName = args[1];
            boolean foundModule = false;

            // Iterate over the modules
            for (Module module : ModuleManager.getInstance().getModules()) {
                if (module.getName().equalsIgnoreCase(moduleName)) {
                    int keyCode = Keyboard.getKeyIndex(keyName.toUpperCase());
                    module.setKey(keyCode); // Set the key binding

                    Wrapper.addChatMessage(String.format(
                            Wrapper.Colors.green + "[B] - " + Wrapper.Colors.white + "Bound %s to %s",
                            module.getName(), Keyboard.getKeyName(keyCode))
                    );
                    foundModule = true;
                    break;
                }
            }

            if (!foundModule) {
                Wrapper.addChatMessage(Wrapper.Colors.red + "[B] - " + Wrapper.Colors.white + "Could not find module");
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            Wrapper.addChatMessage(Wrapper.Colors.red + "[B] - " + Wrapper.Colors.white + "Cleared all module binds");
            for (Module module : ModuleManager.getInstance().getModules()) {
                module.setKey(Keyboard.KEY_NONE); // Clear key binding
            }
        }
    }
}
