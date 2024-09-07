package org.brutality.commands.impl;

import net.minecraft.entity.player.EntityPlayer;
import org.brutality.commands.Command;
import org.brutality.utils.KOSManager;
import org.brutality.utils.Wrapper;

public class KOSCommand extends Command {
    public KOSCommand() {
        super("kos", "Manage your KOS list", "kos add <player> | kos remove <player> | kos clear", "k");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage(".kos add <player>");
            Wrapper.addChatMessage(".kos remove <player>");
            Wrapper.addChatMessage(".kos clear");
            return;
        }

        String subCommand = args[0].toLowerCase();
        if (args.length >= 2) {
            String playerName = args[1];
            EntityPlayer player = Wrapper.getWorld().getPlayerEntityByName(playerName);

            if (player == null) {
                Wrapper.addChatMessage("Player not found: " + playerName);
                return;
            }

            if (subCommand.equals("add")) {
                if (KOSManager.isKOS(player)) {
                    Wrapper.addChatMessage(playerName + " is already in the KOS list");
                } else {
                    KOSManager.addKOS(player.getName());
                    Wrapper.addChatMessage("Added " + playerName + " to KOS list");
                }
            } else if (subCommand.equals("remove")) {
                if (KOSManager.isKOS(player)) {
                    KOSManager.removeKOS(player.getName());
                    Wrapper.addChatMessage("Removed " + playerName + " from KOS list");
                } else {
                    Wrapper.addChatMessage(playerName + " is not in the KOS list");
                }
            } else {
                Wrapper.addChatMessage("Invalid sub-command. Correct usage:");
                Wrapper.addChatMessage(".kos add <player>");
                Wrapper.addChatMessage(".kos remove <player>");
                Wrapper.addChatMessage(".kos clear");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                KOSManager.clearKOS();  // Ensure this method is implemented in KOSManager
                Wrapper.addChatMessage("Cleared all KOS");
            } else {
                Wrapper.addChatMessage("Invalid usage. Correct usage:");
                Wrapper.addChatMessage(".kos add <player>");
                Wrapper.addChatMessage(".kos remove <player>");
                Wrapper.addChatMessage(".kos clear");
            }
        } else {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage(".kos add <player>");
            Wrapper.addChatMessage(".kos remove <player>");
            Wrapper.addChatMessage(".kos clear");
        }
    }
}
