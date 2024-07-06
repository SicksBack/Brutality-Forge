package org.brutality.ui.font;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.utils.interfaces.MC;

import java.awt.*;
import java.io.IOException;

@Getter
public class FontManager implements MC {
    public static int lastHeight = 0;
    public static int lastWidth = 0;

    //Arial
    public static CustomFontRenderer arial16;
    public static CustomFontRenderer arial18;
    public static CustomFontRenderer arial24;
    public static CustomFontRenderer arial36;
    public static CustomFontRenderer arial48;
    public static CustomFontRenderer arial64;

    //ESP
    public static CustomFontRenderer esp21;

    //Consolas
    public static CustomFontRenderer consolas15;
    public static CustomFontRenderer consolas17;
    public static CustomFontRenderer consolas18;
    public static CustomFontRenderer consolas21;

    // Product Sans
    public static CustomFontRenderer productSans18;
    public static CustomFontRenderer productSans20;
    public static CustomFontRenderer productSans26;
    public static CustomFontRenderer productSans31;

    // Comfortaa Light
    public static CustomFontRenderer comfortaaLight64;

    // Skidma
    public static CustomFontRenderer sigma18;
    public static CustomFontRenderer sigma22;
    public static CustomFontRenderer sigma52;



    public void init() {
        try {
            MinecraftForge.EVENT_BUS.register(this);

            arial16 = new CustomFontRenderer(createFontFromFile("arial.ttf", 16));
            arial18 = new CustomFontRenderer(createFontFromFile("arial.ttf", 18));
            arial24 = new CustomFontRenderer(createFontFromFile("arial.ttf", 24));
            arial36 = new CustomFontRenderer(createFontFromFile("arial.ttf", 36));
            arial48 = new CustomFontRenderer(createFontFromFile("arial.ttf", 48));
            arial64 = new CustomFontRenderer(createFontFromFile("arial.ttf", 64));
            esp21 = new CustomFontRenderer(createFontFromFile("esp.ttf", 21));
            consolas15 = new CustomFontRenderer(createFontFromFile("consolas.ttf", 15));
            consolas17 = new CustomFontRenderer(createFontFromFile("consolas.ttf", 17));
            consolas18 = new CustomFontRenderer(createFontFromFile("consolas.ttf", 18));
            consolas21 = new CustomFontRenderer(createFontFromFile("consolas.ttf", 21));
            productSans18 = new CustomFontRenderer(createFontFromFile("productsans.ttf", 18));
            productSans20 = new CustomFontRenderer(createFontFromFile("productsans.ttf", 20));
            productSans26 = new CustomFontRenderer(createFontFromFile("productsans.ttf", 26));
            productSans31 = new CustomFontRenderer(createFontFromFile("productsans.ttf", 31));
            comfortaaLight64 = new CustomFontRenderer(createFontFromFile("comfortaalight.ttf", 64));
            sigma18 = new CustomFontRenderer(createFontFromFile("skidma.otf", 18));
            sigma22 = new CustomFontRenderer(createFontFromFile("skidma.otf", 22));
            sigma52 = new CustomFontRenderer(createFontFromFile("skidma.otf", 52));
            lastHeight = mc.displayHeight;
            lastWidth = mc.displayWidth;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent event) {
        if (lastHeight != mc.displayHeight || lastWidth != mc.displayWidth) {
            System.out.println("Detected Resolution mismatch! Updating fonts to match resolution!");
            init();
        }
    }

    private static Font createFontFromFile(String fileName, float size, int type) throws IOException, FontFormatException {
        ResourceLocation resourceLocation = new ResourceLocation("brutality/font/" + fileName);
        return Font.createFont(type, Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream()).deriveFont(size);
    }

    private static Font createFontFromFile(String fileName, float size) throws IOException, FontFormatException {
        ResourceLocation resourceLocation = new ResourceLocation("brutality/font/" + fileName);
        return Font.createFont(Font.PLAIN, Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream()).deriveFont(size);
    }
}
