package org.brutality.module.impl.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.SimpleModeSetting;

import java.util.ArrayList;
import java.util.List;

public class CustomChat extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<ChatLine> slidingChatLines = new ArrayList<>();
    private final int slideDuration = 20; // Duration of the slide animation in ticks

    public final SimpleModeSetting modeSetting = new SimpleModeSetting("Mode", this, "PrimeCheats", new String[]{"PrimeCheats"});

    public CustomChat() {
        super("CustomChat", "Shows sliding chat messages", Category.RENDER);
        addSettings(modeSetting);
    }

    @SubscribeEvent
    public void onChatMessage(ClientChatReceivedEvent event) {
        if (!modeSetting.getValue().equals("PrimeCheats")) {
            return;
        }
        IChatComponent message = event.message;
        slidingChatLines.add(new ChatLine(message, slideDuration));
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent.Text event) {
        if (!modeSetting.getValue().equals("PrimeCheats")) {
            return;
        }

        GuiNewChat chatGUI = mc.ingameGUI.getChatGUI();
        List<String> sentMessages = chatGUI.getSentMessages();

        int y = 0;
        for (int i = 0; i < sentMessages.size(); i++) {
            IChatComponent line = chatGUI.getChatComponent(i, mc.ingameGUI.getUpdateCounter());
            if (line != null) {
                drawChatLine(line, y, i);
                y += 9; // Chat line height
            }
        }
    }

    private void drawChatLine(IChatComponent line, int y, int index) {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        String text = line.getFormattedText();
        int x = screenWidth - mc.fontRendererObj.getStringWidth(text) - 2;

        float alpha = Math.min(1.0f, (index + 1) / (float) slideDuration);
        int color = (int) (255 * alpha) << 24 | 0xFFFFFF;

        mc.fontRendererObj.drawString(text, x, y, color);
    }

    private static class ChatLine {
        private final IChatComponent message;
        private int remainingTicks;

        public ChatLine(IChatComponent message, int duration) {
            this.message = message;
            this.remainingTicks = duration;
        }

        public boolean tick() {
            remainingTicks--;
            return remainingTicks > 0;
        }

        public IChatComponent getMessage() {
            return message;
        }
    }
}
