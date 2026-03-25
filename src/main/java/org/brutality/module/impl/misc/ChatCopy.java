package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatCopy extends Module {

    private final BooleanSetting timestamp = new BooleanSetting("Timestamp", this, true);
    private final BooleanSetting stripColor = new BooleanSetting("Strip Colors", this, true);

    private File logFile;
    private final SimpleDateFormat dateFmt  = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFmt  = new SimpleDateFormat("HH:mm:ss");

    public ChatCopy() {
        super("ChatCopy", "s", Category.MISC);
        addSettings(timestamp, stripColor);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onEnable() {
        setupFile();
        chat(EnumChatFormatting.GREEN + "ChatCopy ON — logging to brutality/chat.txt");
        super.onEnable();
    }

    @Override
    public void onDisable() {
        chat(EnumChatFormatting.RED + "ChatCopy OFF");
        super.onDisable();
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private void setupFile() {
        try {
            // .minecraft/brutality/
            File dir = new File(Minecraft.getMinecraft().mcDataDir, "brutality");
            if (!dir.exists()) dir.mkdirs();

            logFile = new File(dir, "chat.txt");

            // Write a session header so logs from different sessions are separated
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
                bw.write("──────────────────────────────────────────────");
                bw.newLine();
                bw.write("Session started: " + dateFmt.format(new Date())
                        + " " + timeFmt.format(new Date()));
                bw.newLine();
                bw.write("──────────────────────────────────────────────");
                bw.newLine();
            }
        } catch (IOException e) {
            chat(EnumChatFormatting.RED + "ChatCopy: failed to create log file — " + e.getMessage());
            logFile = null;
        }
    }

    // ── Chat listener ─────────────────────────────────────────────────────────

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (!isToggled()) return;
        if (logFile == null) return;

        // type 0 = normal chat, type 1 = system message, type 2 = action bar
        // log all of them — action bar spam can be filtered via Strip Colors setting
        String raw = event.message.getFormattedText();

        // Strip Minecraft color/format codes (§ followed by any char)
        String clean = stripColor.isEnabled()
                ? raw.replaceAll("§[0-9a-fk-or]", "")
                : raw;

        // Trim trailing whitespace left by stripped codes
        clean = clean.trim();

        if (clean.isEmpty()) return;

        String line = timestamp.isEnabled()
                ? "[" + timeFmt.format(new Date()) + "] " + clean
                : clean;

        writeToFile(line);
    }

    // ── File writer ───────────────────────────────────────────────────────────

    private void writeToFile(String line) {
        if (logFile == null) return;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true))) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            // Silently fail after first error — don't spam chat
            logFile = null;
            chat(EnumChatFormatting.RED + "ChatCopy: write error, logging stopped — " + e.getMessage());
        }
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private static void chat(String msg) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null)
            mc.thePlayer.addChatMessage(new ChatComponentText(msg));
    }
}