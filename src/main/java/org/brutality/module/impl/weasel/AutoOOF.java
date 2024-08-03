package org.brutality.module.impl.weasel;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AutoOOF extends Module {
    public SimpleModeSetting megaSetting = new SimpleModeSetting("Uberstreak", this, "Uberstreak", new String[]{
            "Uberstreak", "Overdrive"
    });
    private int ticks = 0;

    public AutoOOF() {
        super("AutoOOF", "Die automatically after certain kills amount", Category.WEASEL);
        addSettings(megaSetting);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            ticks++;
            if (ticks % 40 == 0) {
                runModule(mc);
            }
        }
    }

    private void runModule(Minecraft mc) {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective sidebarObjective = scoreboard.getObjectiveInDisplaySlot(1);

        if (sidebarObjective == null) {
            return;
        }

        Collection<Score> scores = scoreboard.getSortedScores(sidebarObjective);
        List<Score> filteredScores = scores.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (filteredScores.size() > 15) {
            scores = filteredScores.subList(filteredScores.size() - 15, filteredScores.size());
        } else {
            scores = filteredScores;
        }

        Collections.reverse((List<?>) scores);

        String streakLine = null;
        String statusLine = null;

        for (Score score : scores) {
            ScorePlayerTeam team = scoreboard.getPlayersTeam(score.getPlayerName());
            String scoreboardLine = removeColorCodes(ScorePlayerTeam.formatPlayerName(team, score.getPlayerName()).trim());
            if (scoreboardLine.contains("Streak")) {
                streakLine = scoreboardLine;
            } else if (scoreboardLine.contains("Status")) {
                statusLine = scoreboardLine;
            }
        }

        if (streakLine != null && statusLine != null) {
            String currentMega = statusLine.split("Status: ")[1];
            String currentStreak = streakLine.split("Streak: ")[1];

            if (megaSetting.is("Overdrive") && currentMega.equals("Overdrive")) {
                mc.thePlayer.sendChatMessage("/oof");
            } else if (megaSetting.is("Uberstreak") && currentMega.equals("Uberstreak") && currentStreak.equals("400")) {
                mc.thePlayer.sendChatMessage("/oof");
            }
        }
    }

    private String removeColorCodes(String input) {
        return input.replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
    }
}
