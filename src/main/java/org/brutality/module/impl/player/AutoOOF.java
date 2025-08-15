package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;

public class AutoOOF extends Module {

    public SimpleModeSetting uberstreakSetting = new SimpleModeSetting("Uberstreak", this, "Uberstreak", new String[]{"Uberstreak"});

    public AutoOOF() {
        super("AutoOOF", "Automatically OOFs when in Uberstreak with 400+ streak", Category.WEASEL);
        addSettings(uberstreakSetting);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1); // 1 is for sidebar

        if (objective != null) {
            for (Score score : scoreboard.getSortedScores(objective)) {
                String scoreName = score.getPlayerName();
                int scoreValue = score.getScorePoints();

                if (scoreName.startsWith(EnumChatFormatting.GRAY + "Status: Uberstreak")) {
                    // Uberstreak status found
                    for (Score streakScore : scoreboard.getSortedScores(objective)) {
                        String streakName = streakScore.getPlayerName();

                        if (streakName.startsWith(EnumChatFormatting.GRAY + "Streak:")) {
                            int streakValue = Integer.parseInt(streakName.split(" ")[1]);

                            if (streakValue >= 400) {
                                mc.thePlayer.sendChatMessage("/oof");
                                mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }
}
