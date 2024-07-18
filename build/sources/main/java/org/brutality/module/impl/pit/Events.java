package org.brutality.module.impl.pit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.TimerUtils;

import java.util.ArrayList;
import java.util.List;

public class Events extends Module {
    private final NumberSetting yPos = new NumberSetting("Y Pos", this, 50.0, 0.0, 1200.0, 1);
    private final NumberSetting xPos = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1);
    private final TimerUtils timeRefresh = new TimerUtils();
    private final TimerUtils time = new TimerUtils();
    private List<String> eventList = new ArrayList<>();

    public Events() {
        super("Events", "Displays upcoming events.", Category.PIT);
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fr = mc.fontRendererObj;
        float posX = (float) this.xPos.getValue();
        float posY = (float) this.yPos.getValue();

        for (String eventString : eventList) {
            fr.drawStringWithShadow(eventString, posX, posY, 0xFFFFFF);
            posY += fr.FONT_HEIGHT;
        }
    }

    public void setEventList(List<String> eventList) {
        this.eventList = eventList;
    }
}
