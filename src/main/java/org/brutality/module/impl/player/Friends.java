package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.FriendManager;
import org.lwjgl.input.Keyboard;

public class Friends extends Module {
    public static boolean friendsEnabled = true; // Static variable to manage friend system state

    public Friends() {
        super("Friends", "Toggle the friends system.", Category.PLAYER);
        setKey(Keyboard.KEY_R);
    }

    @Override
    public void onEnable() {
        friendsEnabled = true; // Enable the friends system
    }

    @Override
    public void onDisable() {
        friendsEnabled = false; // Disable the friends system
    }
}
