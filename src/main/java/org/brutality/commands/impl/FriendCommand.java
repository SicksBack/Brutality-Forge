package org.brutality.commands.impl;

import net.minecraft.entity.player.EntityPlayer;
import org.brutality.commands.Command;
import org.brutality.utils.FriendManager;
import org.brutality.utils.Wrapper;

public class FriendCommand extends Command {
    public FriendCommand() {
        super("friend", "Manage your friends list", "friend add <player> | friend remove <player> | friend clear", "f");
    }

    @Override
    public void onCommand(String[] args, String command) {
        if (args.length == 0) {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage(".friend add <player>");
            Wrapper.addChatMessage(".friend remove <player>");
            Wrapper.addChatMessage(".friend clear");
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
                if (FriendManager.isFriend(player)) {
                    Wrapper.addChatMessage(playerName + " is already in the friends list");
                } else {
                    FriendManager.addFriend(player.getName());
                    Wrapper.addChatMessage("Added " + playerName + " to friends list");
                }
            } else if (subCommand.equals("remove")) {
                if (FriendManager.isFriend(player)) {
                    FriendManager.removeFriend(player.getName());
                    Wrapper.addChatMessage("Removed " + playerName + " from friends list");
                } else {
                    Wrapper.addChatMessage(playerName + " is not in the friends list");
                }
            } else {
                Wrapper.addChatMessage("Invalid sub-command. Correct usage:");
                Wrapper.addChatMessage(".friend add <player>");
                Wrapper.addChatMessage(".friend remove <player>");
                Wrapper.addChatMessage(".friend clear");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("clear")) {
                FriendManager.clearFriends(); // Ensure this method is implemented in FriendManager
                Wrapper.addChatMessage("Cleared all friends");
            } else {
                Wrapper.addChatMessage("Invalid usage. Correct usage:");
                Wrapper.addChatMessage(".friend add <player>");
                Wrapper.addChatMessage(".friend remove <player>");
                Wrapper.addChatMessage(".friend clear");
            }
        } else {
            Wrapper.addChatMessage("Invalid usage. Correct usage:");
            Wrapper.addChatMessage(".friend add <player>");
            Wrapper.addChatMessage(".friend remove <player>");
            Wrapper.addChatMessage(".friend clear");
        }
    }
}
