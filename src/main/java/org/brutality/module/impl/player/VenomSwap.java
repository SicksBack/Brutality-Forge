package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.events.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Wrapper;

public class VenomSwap extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final NumberSetting delay = new NumberSetting("Delay", this, 100, 1, 500, 0);
    private final BooleanSetting includeBoots = new BooleanSetting("Include Boots", this, true);

    // State management
    private boolean shouldSwap = false;
    private SwapState currentState = SwapState.IDLE;
    private int tickDelay = 0;
    private int currentDelayTicks = 0;

    private enum SwapState {
        IDLE,
        OPENING_INVENTORY,
        SWAPPING_LEGGINGS,
        SWAPPING_BOOTS,
        CLOSING_INVENTORY
    }

    public VenomSwap() {
        super("VenomSwap", "apple needs this", Category.PLAYER);
        this.addSettings(delay, includeBoots);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        resetState();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetState();
    }

    private void resetState() {
        shouldSwap = false;
        currentState = SwapState.IDLE;
        tickDelay = 0;
        currentDelayTicks = 0;
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (mc.thePlayer == null || mc.theWorld == null || !this.isToggled()) {
            return;
        }

        // Check if chat contains "POISONED!"
        if (event.message.getUnformattedText().contains("POISONED!")) {
            // Check if diamond leggings are in hotbar and we're not already swapping
            if (hasDiamondLeggingsInHotbar() && currentState == SwapState.IDLE) {
                shouldSwap = true;
                currentState = SwapState.OPENING_INVENTORY;
                tickDelay = (int) (delay.getValue() / 50); // Convert ms to ticks (50ms per tick)
                currentDelayTicks = 0;
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null || !this.isToggled()) {
            return;
        }

        if (!shouldSwap || currentState == SwapState.IDLE) {
            return;
        }

        // Handle delay
        if (currentDelayTicks < tickDelay) {
            currentDelayTicks++;
            return;
        }

        switch (currentState) {
            case OPENING_INVENTORY:
                openInventory();
                break;
            case SWAPPING_LEGGINGS:
                swapLeggings();
                break;
            case SWAPPING_BOOTS:
                swapBoots();
                break;
            case CLOSING_INVENTORY:
                closeInventory();
                break;
        }
    }


    private boolean hasDiamondLeggingsInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.armorType == 2 && itemStack.getDisplayName().toLowerCase().contains("diamond")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isWearingDiamondBoots() {
        ItemStack boots = mc.thePlayer.inventory.armorInventory[0]; // Boots slot
        if (boots != null && boots.getItem() instanceof ItemArmor) {
            return boots.getDisplayName().toLowerCase().contains("diamond");
        }
        return false;
    }

    private void openInventory() {
        if (mc.currentScreen != null) {
            resetState();
            return;
        }

        mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
        currentState = SwapState.SWAPPING_LEGGINGS;
        currentDelayTicks = 0;
    }

    private void swapLeggings() {
        if (!(mc.currentScreen instanceof GuiInventory)) {
            resetState();
            return;
        }

        int pantsSlot = findDiamondLeggingsSlotInHotbar();
        if (pantsSlot != -1) {
            mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
            sendSuccessMessage("Diamond Leggings");
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 7, pantsSlot, 2, mc.thePlayer);

            if (includeBoots.isEnabled() && !isWearingDiamondBoots()) {
                currentState = SwapState.SWAPPING_BOOTS;
                currentDelayTicks = 0;
            } else {
                currentState = SwapState.CLOSING_INVENTORY;
                currentDelayTicks = 0;
            }
        } else {
            mc.thePlayer.playSound("mob.villager.no", 1.0f, 0.7f);
            sendFailureMessage("No Diamond Leggings Found");
            currentState = SwapState.CLOSING_INVENTORY;
            currentDelayTicks = 0;
        }
    }

    private void swapBoots() {
        if (!(mc.currentScreen instanceof GuiInventory)) {
            resetState();
            return;
        }

        int bootsSlot = findDiamondBootsSlotInHotbar();
        if (bootsSlot != -1) {
            mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
            sendSuccessMessage("Diamond Boots");
            mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 8, bootsSlot, 2, mc.thePlayer);
        } else {
            mc.thePlayer.playSound("mob.villager.no", 1.0f, 0.7f);
            sendFailureMessage("No Diamond Boots Found");
        }

        currentState = SwapState.CLOSING_INVENTORY;
        currentDelayTicks = 0;
    }

    private int findDiamondLeggingsSlotInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.armorType == 2 && itemStack.getDisplayName().toLowerCase().contains("diamond")) {
                    return i;
                }
            }
        }
        return -1;
    }

    private int findDiamondBootsSlotInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.armorType == 3 && itemStack.getDisplayName().toLowerCase().contains("diamond")) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void closeInventory() {
        mc.thePlayer.closeScreen();
        resetState();
    }

    private void sendSuccessMessage(String item) {
        String message = Wrapper.Colors.dark_gray + "["
                + Wrapper.Colors.dark_red + "B"
                + Wrapper.Colors.dark_gray + "] "
                + Wrapper.Colors.dark_gray + "- "
                + Wrapper.Colors.green + "Successfully Swapped " + item
                + Wrapper.Colors.white + ".";
        Wrapper.addChatMessage(message);
    }

    private void sendFailureMessage(String item) {
        String message = Wrapper.Colors.dark_gray + "["
                + Wrapper.Colors.dark_red + "B"
                + Wrapper.Colors.dark_gray + "] "
                + Wrapper.Colors.dark_gray + "- "
                + Wrapper.Colors.red + item
                + Wrapper.Colors.white + ".";
        Wrapper.addChatMessage(message);
    }
}