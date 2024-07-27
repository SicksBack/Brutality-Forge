package org.brutality.module.impl.weasel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.lwjgl.opengl.GL11;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class StaffDetector extends Module {

    private final BooleanSetting autoLeave = new BooleanSetting("Auto Leave", this, true);
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean staffAlert = false;
    private long displayTime = 0;
    private String alertMessage = "";
    private Set<String> staffNames;

    public StaffDetector() {
        super("StaffDetector", "Detects when staff join or leave the game", Category.WEASEL);
        addSettings(autoLeave);
        createFiles();
        loadStaffNames();
    }

    private void createFiles() {
        File dir = new File(mc.mcDataDir, "brutality/staff/weasel");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, "staff.txt");
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Unoriginal_Guy"); // Example staff name
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadStaffNames() {
        staffNames = new HashSet<>();
        File file = new File(mc.mcDataDir, "brutality/staff/weasel/staff.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    staffNames.add(line.trim());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        IChatComponent chatComponent = event.message;
        String message = chatComponent.getUnformattedText();

        for (String staffName : staffNames) {
            if (message.contains(staffName + " joined the game")) {
                staffAlert = true;
                displayTime = System.currentTimeMillis();
                alertMessage = "STAFF JOINED!";
                playAlertSound();
                if (autoLeave.isEnabled()) {
                    mc.theWorld.sendQuittingDisconnectingPacket();
                }
                return;
            } else if (message.contains(staffName + " left the game")) {
                mc.thePlayer.sendChatMessage("/msg " + staffName + " a");
                return;
            } else if (message.contains("You cannot message this player") && alertMessage.equals("STAFF VANISHED!")) {
                staffAlert = true;
                displayTime = System.currentTimeMillis();
                alertMessage = "STAFF VANISHED!";
                playAlertSound();
                if (autoLeave.isEnabled()) {
                    mc.theWorld.sendQuittingDisconnectingPacket();
                }
                return;
            } else if (message.contains("There is no player online whose name starts with '" + staffName)) {
                staffAlert = true;
                displayTime = System.currentTimeMillis();
                alertMessage = "STAFF LEFT!";
                playAlertSound();
                return;
            }
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
        if (staffAlert && System.currentTimeMillis() - displayTime < 5000) {
            renderAlert(alertMessage);
        }
    }

    private void renderAlert(String text) {
        ScaledResolution sr = new ScaledResolution(mc);
        int width = sr.getScaledWidth();
        int height = sr.getScaledHeight();
        int x = width / 2 - mc.fontRendererObj.getStringWidth(text) / 2;
        int y = height / 2 - mc.fontRendererObj.FONT_HEIGHT / 2;

        GL11.glPushMatrix();
        GL11.glScalef(2.0F, 2.0F, 2.0F);  // Scale text to make it bigger
        mc.fontRendererObj.drawStringWithShadow(text, (x / 2), (y / 2), 0xFFAA0000); // Dark red color
        GL11.glPopMatrix();
    }

    private void playAlertSound() {
        // Play a sound when the alert is triggered
        mc.thePlayer.playSound("random.orb", 1.0F, 1.0F);
    }
}
