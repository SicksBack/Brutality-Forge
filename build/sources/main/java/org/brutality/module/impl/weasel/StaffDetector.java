package org.brutality.module.impl.weasel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

public class StaffDetector extends Module {

    private final BooleanSetting autoLeave = new BooleanSetting("Auto Leave", this, true);
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean staffJoined = false;
    private boolean staffVanished = false;

    public StaffDetector() {
        super("StaffDetector", "Detects when staff join or leave the game", Category.WEASEL);
        addSettings(autoLeave);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        IChatComponent chatComponent = event.message;
        String message = chatComponent.getUnformattedText();

        if (message.contains("Unoriginal_Guy joined the game")) {
            staffJoined = true;
            if (autoLeave.isEnabled()) {
                mc.theWorld.sendQuittingDisconnectingPacket();
            }
        } else if (message.contains("Unoriginal_Guy left the game")) {
            mc.thePlayer.sendChatMessage("/msg Unoriginal_Guy a");
        } else if (message.contains("You cannot message this player")) {
            staffVanished = true;
            if (autoLeave.isEnabled()) {
                mc.theWorld.sendQuittingDisconnectingPacket();
            }
        } else if (message.contains("There is no player online whose name starts with 'Unoriginal_Guy'")) {
            staffJoined = false;
            staffVanished = false;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (staffJoined) {
            renderAlert("STAFF JOINED!");
        } else if (staffVanished) {
            renderAlert("STAFF VANISHED!");
        }
    }

    private void renderAlert(String text) {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        int x = width / 2 - mc.fontRendererObj.getStringWidth(text) / 2;
        int y = height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2;
        mc.fontRendererObj.drawStringWithShadow(text, x, y, 0xFFFF0000);
    }
}
