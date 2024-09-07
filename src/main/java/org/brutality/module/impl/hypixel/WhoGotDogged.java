package org.brutality.module.impl.hypixel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.TimeHelper;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class WhoGotDogged extends Module {
    private final ArrayList<String> teams;
    private final ArrayList<String> oldTeams;
    private final List<String> prev;
    private final TimeHelper banCooldown = new TimeHelper();
    private final TimeHelper autoLeaveCooldown = new TimeHelper(); // Cooldown for auto-leaving

    // New setting to control auto-leave feature
    private final BooleanSetting autoLeave = new BooleanSetting("Auto Leave", this, true);

    public WhoGotDogged() {
        super("WhoGotDogged", "Checks who left the lobby on ban message.", Category.HYPIXEL);
        this.teams = new ArrayList<>();
        this.oldTeams = new ArrayList<>();
        this.prev = new ArrayList<>();
        this.addSettings(autoLeave); // Add the setting to the module
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null || event.phase != TickEvent.Phase.START) {
            return;
        }
        this.getDifference();
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        String msg = event.message.getUnformattedText();
        if (msg.contains("A player has been removed from your game")) {
            this.banCooldown.reset();
            this.autoLeaveCooldown.reset(); // Reset auto-leave cooldown when a ban message is received

            if (autoLeave.isEnabled()) {
                mc.theWorld.sendQuittingDisconnectingPacket(); // Disconnect from the game
            }
        }
    }

    private void getDifference() {
        Collection<NetworkPlayerInfo> currentPlayers = mc.getNetHandler().getPlayerInfoMap();
        if (this.prev != null) {
            for (String playerName : this.prev) {
                boolean stillInGame = false;
                for (NetworkPlayerInfo playerInfo : currentPlayers) {
                    if (playerName.equals(playerInfo.getGameProfile().getName())) {
                        stillInGame = true;
                        break;
                    }
                }
                if (!stillInGame && !this.banCooldown.hasReached(1500L)) {
                    this.logToChat(playerName);
                    if (autoLeave.isEnabled() && autoLeaveCooldown.hasReached(5000L)) { // Check if cooldown has passed
                        this.autoLeaveGame(); // Leave the game if auto-leave is enabled and cooldown has passed
                        this.autoLeaveCooldown.reset(); // Reset auto-leave cooldown after leaving
                    }
                }
            }
        }
        this.prev.clear();
        for (NetworkPlayerInfo playerInfo : currentPlayers) {
            this.prev.add(playerInfo.getGameProfile().getName());
        }
    }

    private void logToChat(String playerName) {
        String message = EnumChatFormatting.DARK_GRAY + "[" + EnumChatFormatting.DARK_RED + "B" + EnumChatFormatting.DARK_GRAY + "] - "
                + EnumChatFormatting.GOLD + playerName
                + EnumChatFormatting.DARK_RED + " Got Banned.";
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }

    private void autoLeaveGame() {
        // Logic to leave the game
        if (mc.thePlayer != null) {
            // Use Minecraft's method to disconnect from the server
            mc.theWorld.sendQuittingDisconnectingPacket();
        }
    }
}
