package org.brutality.module.impl.player;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.lwjgl.input.Mouse;

import java.text.NumberFormat;

public class RequirementChecker extends Module {
    private static final Logger logger = LogManager.getLogger("RequirementChecker");
    private int tickCount = 0;
    private final Minecraft mc = Minecraft.getMinecraft();
    private double gainedGold = 0;
    private double neededGold = 0;

    private int textX = 10;
    private int textY = 10;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    public RequirementChecker() {
        super("Requirement Checker", "Display your current gold requirement.", Category.PLAYER);
    }

    @Override
    public void onEnable() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld != null && !mc.isSingleplayer() && event.phase == TickEvent.Phase.END) {
            tickCount++;
            if (tickCount % 100 == 0) {
                sendGoldRequirementRequest();
            }
        }
    }

    private void sendGoldRequirementRequest() {
        if (mc.theWorld != null && mc.getCurrentServerData() != null) {
            String playerName = mc.thePlayer.getName();
            mc.thePlayer.sendChatMessage("/goldreq " + playerName);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.thePlayer != null && mc.theWorld != null && mc.getCurrentServerData() != null) {
            FontRenderer fontRenderer = mc.fontRendererObj;
            NumberFormat numberFormat = NumberFormat.getNumberInstance();

            String formattedGainedGold = numberFormat.format(gainedGold);
            String formattedNeededGold = numberFormat.format(neededGold);

            String displayText = String.format("§aGold Requirement: §6%s§7/§6%sg", formattedGainedGold, formattedNeededGold);

            fontRenderer.drawStringWithShadow(displayText, textX, textY, 0xFFFFFF);
        }
    }


    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        if (mc.theWorld != null && mc.getCurrentServerData() != null) {
            String message = event.message.getUnformattedText();
            try {
                if (message.contains(mc.thePlayer.getName() + ":")) {
                    String part = message.split(mc.thePlayer.getName() + ":")[1].trim();
                    String[] parts = part.split("/");

                    if (parts.length == 2) {
                        gainedGold = Double.parseDouble(parts[0].replaceAll("[^0-9.]", ""));
                        neededGold = Double.parseDouble(parts[1].replaceAll("[^0-9.]", ""));
                        event.setCanceled(true);
                    }
                }
            } catch (Exception e) {
                logger.error(message, e);
            }
        }
    }

    @SubscribeEvent
    public void onMouseClick(GuiScreenEvent.MouseInputEvent.Pre event) {
        if (mc.currentScreen instanceof GuiChat) {
            int mouseX = Mouse.getEventX() * new ScaledResolution(mc).getScaledWidth() / mc.displayWidth;
            int mouseY = new ScaledResolution(mc).getScaledHeight() - Mouse.getEventY() * new ScaledResolution(mc).getScaledHeight() / mc.displayHeight - 1;
            boolean mousePressed = Mouse.isButtonDown(0);

            if (mousePressed) {
                if (!dragging && isMouseOverText(mouseX, mouseY)) {
                    dragging = true;
                    dragOffsetX = mouseX - textX;
                    dragOffsetY = mouseY - textY;
                } else if (dragging) {
                    textX = mouseX - dragOffsetX;
                    textY = mouseY - dragOffsetY;
                }
            } else {
                dragging = false;
            }
        }
    }

    private boolean isMouseOverText(int mouseX, int mouseY) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        int textWidth = fontRenderer.getStringWidth(String.format("Requirement: %.2f/%.2f", gainedGold, neededGold));
        int textHeight = fontRenderer.FONT_HEIGHT;

        return mouseX >= textX && mouseX <= textX + textWidth && mouseY >= textY && mouseY <= textY + textHeight;
    }
}
