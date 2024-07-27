package org.brutality.module.impl.weasel;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.pathfinding.PathFinder;

public class AutoGrinder extends Module {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public AutoGrinder() {
        super("AutoGrinder", "Automatically grinds levels and prestiges", Category.WEASEL);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.contains("PIT LEVEL UP! [119] ⇢ [120]")) {
            mc.thePlayer.sendChatMessage("/play pit");
        } else if (message.contains("PRESTIGE! [120] SickToTheMOON")) {
            PathFinder.navigateTo(new BlockPos(-21, 44, 31));
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (mc.currentScreen instanceof GuiChest) {
            GuiChest chest = (GuiChest) mc.currentScreen;
            ContainerChest container = (ContainerChest) chest.inventorySlots;
            String chestName = container.getLowerChestInventory().getDisplayName().getUnformattedText();
            if (chestName.equals("Prestige")) {
                for (int i = 0; i < container.getLowerChestInventory().getSizeInventory(); i++) {
                    ItemStack stack = container.getLowerChestInventory().getStackInSlot(i);
                    if (stack != null && stack.getDisplayName().contains("Diamond")) {
                        mc.playerController.windowClick(container.windowId, i, 0, 0, mc.thePlayer);
                        mc.thePlayer.addChatMessage(new ChatComponentText("Clicked diamond."));
                    } else if (stack != null && stack.getDisplayName().contains("stained_hardened_clay")) {
                        mc.playerController.windowClick(container.windowId, i, 0, 0, mc.thePlayer);
                        mc.thePlayer.addChatMessage(new ChatComponentText("Clicked stained_hardened_clay."));
                    }
                }
            }
        }
    }
}
