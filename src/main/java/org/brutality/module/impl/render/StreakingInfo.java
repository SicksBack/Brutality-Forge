package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;
import org.lwjgl.input.Mouse;

public class StreakingInfo extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtils streakTimer = new TimerUtils();
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;
    private boolean timerPaused = false;

    private int currentKills = 0;
    private int currentAssists = 0;
    private float streakXP = 0.0f;
    private float streakGold = 0.0f;

    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting boxOpacity = new NumberSetting("Box Opacity", this, 33.3, 0.0, 100.0, 1);

    private static final double boxWidth = 88.5;
    private static final double boxHeight = 96.2;

    public StreakingInfo() {
        super("StreakingInfo", "Displays streak information.", Category.RENDER);
        this.addSettings(xPos, yPos, boxOpacity);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        streakTimer.reset();
        timerPaused = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        float posX = (float) xPos.getValue();
        float posY = (float) yPos.getValue();
        int opacity = (int) (boxOpacity.getValue() * 2.55); // Convert percentage to 0-255 range
        int backgroundColor = (opacity << 24) | 0x000000; // Black color with opacity

        mc.ingameGUI.drawRect((int) posX, (int) posY, (int) posX + (int) boxWidth, (int) posY + (int) boxHeight, backgroundColor);

        if (mc.currentScreen instanceof GuiChat) {
            if (dragging) {
                this.xPos.setValue(Mouse.getX() / 2 - dragX);
                this.yPos.setValue((mc.displayHeight - Mouse.getY()) / 2 - dragY);
            }

            if (Mouse.isButtonDown(0)) {
                if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, fontRenderer)) {
                    dragging = true;
                    dragX = Mouse.getX() / 2 - (int) posX;
                    dragY = (mc.displayHeight - Mouse.getY()) / 2 - (int) posY;
                }
            } else {
                dragging = false;
            }
        }

        String statusColor = timerPaused ? "§c" : "§a"; // Red for "Last", Green for "Active"
        String status = timerPaused ? "Last" : "Active"; // Determine the status based on timerPaused

        String formattedTime = formatTime(streakTimer.getElapsedTime() / 1000);

        // Displaying streak information in multiple lines
        fontRenderer.drawStringWithShadow("§cS§at§dr§9e§6a§e§3k §7[" + statusColor + status + "§7]", posX + 5, posY + 5, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("§aKills§f: §a" + currentKills, posX + 5, posY + 20, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("§cAssists§f: §c" + currentAssists, posX + 5, posY + 35, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("§bXP§f: §f" + String.format("%.1f", streakXP), posX + 5, posY + 50, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("§6Gold§f: §6" + String.format("%.1f", streakGold), posX + 5, posY + 65, 0xFFFFFF);
        fontRenderer.drawStringWithShadow("Time: " + formattedTime, posX + 5, posY + 80, 0xFFFFFF);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.contains("DEATH!")) {
            timerPaused = true;
            resetStreakInfo();
        } else if (message.contains("KILL!")) {
            currentKills++;
            timerPaused = false;
        } else if (message.contains("ASSIST!")) {
            currentAssists++;
        } else {
            processXPAndGold(message);
        }
    }

    private void resetStreakInfo() {
        streakTimer.reset();
        currentKills = 0;
        currentAssists = 0;
        streakXP = 0.0f;
        streakGold = 0.0f;
    }

    private void processXPAndGold(String message) {
        try {
            if (message.contains("XP")) {
                String xpPart = message.split("\\+")[1].split("XP")[0].trim();
                float xp = Float.parseFloat(xpPart);
                streakXP += xp;
            }

            if (message.contains("g")) {
                String goldPart = message.split("\\+")[2].split("g")[0].trim();
                float gold = Float.parseFloat(goldPart);
                streakGold += gold;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String formatTime(long seconds) {
        long hrs = seconds / 3600;
        long mins = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder timeBuilder = new StringBuilder();
        if (hrs > 0) {
            timeBuilder.append(hrs).append("hr ");
        }
        if (mins > 0) {
            timeBuilder.append(mins).append("m ");
        }
        timeBuilder.append(secs).append("s");

        return timeBuilder.toString().trim();
    }

    private boolean isMouseOverText(int mouseX, int mouseY, FontRenderer fontRenderer) {
        int textWidth = fontRenderer.getStringWidth("Kills: " + currentKills + " Assists: " + currentAssists + " XP: " + streakXP + " Gold: " + streakGold + " Time: " + formatTime(streakTimer.getElapsedTime() / 1000));
        int textHeight = fontRenderer.FONT_HEIGHT;
        return mouseX >= this.xPos.getValue() && mouseX <= this.xPos.getValue() + textWidth
                && mouseY >= this.yPos.getValue() && mouseY <= this.yPos.getValue() + textHeight;
    }
}
