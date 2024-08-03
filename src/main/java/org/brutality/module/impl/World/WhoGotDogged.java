package org.brutality.module.impl.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.brutality.module.Category;
import org.brutality.module.Module;
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

    public WhoGotDogged() {
        super("WhoGotDogged", "Checks who left the lobby on ban message.", Category.WORLD);
        this.teams = new ArrayList<>();
        this.oldTeams = new ArrayList<>();
        this.prev = new ArrayList<>();
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
        String msg;
        if ((msg = event.message.getUnformattedText()).contains("A player has been removed from your game")) {
            this.banCooldown.reset();
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
}
