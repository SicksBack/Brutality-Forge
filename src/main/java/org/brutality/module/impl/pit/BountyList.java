package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.lwjgl.input.Mouse;

import java.util.HashMap;
import java.util.Map;

public class BountyList extends Module {

    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 40.0, 0.0, 1200.0, 1);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 40.0, 0.0, 1200.0, 1);

    private int posX = 40;
    private int posY = 40;
    private boolean dragging = false;
    private int dragX = 0;
    private int dragY = 0;

    private final Map<String, String> bountyPlayers = new HashMap<>();

    public BountyList() {
        super("BountyList", "Detects and lists players with bounties", Category.PIT);
        this.addSettings(xPosSetting, yPosSetting);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.theWorld != null) {
            bountyPlayers.clear();
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                String playerName = StringUtils.stripControlCodes(player.getDisplayName().getFormattedText());
                if (playerName.matches(".*\\d+g.*")) {
                    String location = getPlayerLocation(player);
                    double distance = player.getDistanceToEntity(mc.thePlayer);
                    bountyPlayers.put(playerName, location + String.format(" (%.1fm)", distance));
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        if (dragging) {
            posX = (Mouse.getX() / 2) - dragX;
            posY = (mc.displayHeight - Mouse.getY()) / 2 - dragY;
        }

        if (Mouse.isButtonDown(0)) {
            if (!dragging && isMouseOverText(Mouse.getX() / 2, (mc.displayHeight - Mouse.getY()) / 2, "Bounty List:", mc)) {
                dragging = true;
                dragX = Mouse.getX() / 2 - posX;
                dragY = (mc.displayHeight - Mouse.getY()) / 2 - posY;
            }
        } else {
            dragging = false;
        }

        float y = posY;
        mc.fontRendererObj.drawStringWithShadow("Bounty List:", posX, y, 0xFFD700);
        y += mc.fontRendererObj.FONT_HEIGHT + 2;

        for (Map.Entry<String, String> entry : bountyPlayers.entrySet()) {
            String text = entry.getKey() + " " + entry.getValue();
            mc.fontRendererObj.drawStringWithShadow(text, posX, y, 0xFFFF00);
            y += mc.fontRendererObj.FONT_HEIGHT + 2;
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY, String text, Minecraft mc) {
        int textWidth = mc.fontRendererObj.getStringWidth(text);
        int textHeight = mc.fontRendererObj.FONT_HEIGHT;
        return mouseX >= posX && mouseX <= posX + textWidth && mouseY >= posY && mouseY <= posY + textHeight;
    }

    private String getPlayerLocation(EntityPlayer player) {
        double x = player.posX;
        double z = player.posZ;

        if (player.posY > 95) {
            return "In Spawn";
        } else if (x > -60 && x < 60 && z > -60 && z < 60) {
            return "In Mid";
        } else {
            return "OutSkirts";
        }
    }
}
