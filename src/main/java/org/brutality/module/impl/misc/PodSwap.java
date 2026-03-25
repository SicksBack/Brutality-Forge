package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Wrapper;

public class PodSwap extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();

    private final NumberSetting delay = new NumberSetting("Delay", this, 100, 0, 500, 10);
    private final NumberSetting healthTrigger = new NumberSetting("Health Trigger", this, 4, 2, 20, 1);
    private final BooleanSetting swapBackInAir = new BooleanSetting("Swap Back in Air", this, true);
    private final NumberSetting airTime = new NumberSetting("Air Time", this, 40, 10, 100, 10);
    private final BooleanSetting safeMode = new BooleanSetting("Safe Mode", this, true);

    private ItemStack originalLeggings = null;
    private boolean hasSwappedToPod = false;
    private boolean podUsed = false;
    private boolean podProced = false;
    private int airTicks = 0;
    private boolean wasOnGround = true;
    private float lastHealth = 20.0f;
    private int ticksSinceVelocity = 0;
    private boolean wasVenomed = false;
    private SwapState swapState = SwapState.IDLE;
    private int swapDelay = 0;

    private enum SwapState {
        IDLE,
        OPENING_INVENTORY,
        SWAPPING,
        CLOSING_INVENTORY
    }

    public PodSwap() {
        super("PodSwap", "Auto swap to escape pod when low health", Category.MISC);
        this.addSettings(delay, healthTrigger, swapBackInAir, airTime, safeMode);
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
        originalLeggings = null;
        hasSwappedToPod = false;
        podUsed = false;
        podProced = false;
        airTicks = 0;
        wasOnGround = true;
        ticksSinceVelocity = 0;
        wasVenomed = false;
        lastHealth = mc.thePlayer != null ? mc.thePlayer.getHealth() : 20.0f;
        swapState = SwapState.IDLE;
        swapDelay = 0;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START || mc.thePlayer == null || mc.theWorld == null || !this.isToggled()) {
            return;
        }

        // Handle swap sequence
        if (swapState != SwapState.IDLE) {
            handleSwapSequence();
            return;
        }

        // Check for pod activation (velocity packet)
        if (podProced && !hasSwappedToPod && originalLeggings != null) {
            ticksSinceVelocity++;
            if (ticksSinceVelocity >= 10) {
                triggerSwapBack();
            }
            return;
        }

        // Check for screen open
        if (mc.currentScreen != null) {
            return;
        }

        // Track ground state
        if (mc.thePlayer.onGround) {
            if (!wasOnGround) airTicks = 0;
            wasOnGround = true;
        } else {
            if (wasOnGround) airTicks = 0;
            wasOnGround = false;
            airTicks++;
        }

        // Air-time swap back
        if (swapBackInAir.isEnabled() && airTicks >= airTime.getValue()
                && isWearingEscapePodPants() && originalLeggings != null && hasSwappedToPod) {
            sendMessage(EnumChatFormatting.YELLOW + "Air Time Reached - Swapping Back");
            triggerSwapBack();
            return;
        }

        // Check venom status
        boolean currentlyVenomed = isVenomed();
        boolean venomJustWoreOff = wasVenomed && !currentlyVenomed;
        if (currentlyVenomed) {
            wasVenomed = true;
            return;
        }

        float currentHealth = mc.thePlayer.getHealth();
        float healthInHearts = currentHealth / 2.0f;

        // Trigger swap when low health
        if (!hasSwappedToPod && !podUsed && healthInHearts <= healthTrigger.getValue()
                && (currentHealth < lastHealth || venomJustWoreOff)
                && findEscapePantsSlotInHotbar() != -1) {
            triggerSwapToPod();
        }

        lastHealth = currentHealth;
        wasVenomed = currentlyVenomed;
    }

    @SubscribeEvent
    public void onVelocity(net.minecraftforge.fml.common.gameevent.PlayerEvent event) {
        if (!this.isToggled() || mc.thePlayer == null) return;

        // Handle velocity packet for pod detection
        if (event instanceof net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent) {
            return;
        }
    }

    private void handleSwapSequence() {
        if (swapDelay > 0) {
            swapDelay--;
            return;
        }

        switch (swapState) {
            case OPENING_INVENTORY:
                if (mc.currentScreen != null) {
                    swapState = SwapState.IDLE;
                    return;
                }
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                swapState = SwapState.SWAPPING;
                swapDelay = (int) (delay.getValue() / 50);
                break;

            case SWAPPING:
                if (!(mc.currentScreen instanceof GuiInventory)) {
                    swapState = SwapState.IDLE;
                    return;
                }

                if (hasSwappedToPod) {
                    // Swap back to original
                    int slot = findMatchingArmorContainerSlot(originalLeggings, 2);
                    if (slot != -1) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 7, slot, 2, mc.thePlayer);
                        mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
                    }
                    swapState = SwapState.CLOSING_INVENTORY;
                    swapDelay = 2;
                } else {
                    // Swap to pod
                    int podSlot = findEscapePantsSlotInHotbar();
                    if (podSlot != -1) {
                        originalLeggings = mc.thePlayer.inventory.armorInventory[2];
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 7, podSlot, 2, mc.thePlayer);
                        mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
                        hasSwappedToPod = true;
                    }
                    swapState = SwapState.CLOSING_INVENTORY;
                    swapDelay = 2;
                }
                break;

            case CLOSING_INVENTORY:
                mc.thePlayer.closeScreen();
                swapState = SwapState.IDLE;
                break;
        }
    }

    private void triggerSwapToPod() {
        int podSlot = findEscapePantsSlotInHotbar();
        if (podSlot == -1) {
            return;
        }

        if (safeMode.isEnabled() && mc.thePlayer.isSprinting()) {
            mc.thePlayer.setSprinting(false);
        }

        swapState = SwapState.OPENING_INVENTORY;
        sendMessage(EnumChatFormatting.GREEN + "Swapping to Escape Pod");
    }

    private void triggerSwapBack() {
        if (originalLeggings == null) {
            clearSwapState();
            return;
        }

        if (safeMode.isEnabled() && mc.thePlayer.isSprinting()) {
            mc.thePlayer.setSprinting(false);
        }

        swapState = SwapState.OPENING_INVENTORY;
        hasSwappedToPod = false; // Will swap back
        sendMessage(EnumChatFormatting.YELLOW + "Swapping Back");
    }

    private void clearSwapState() {
        originalLeggings = null;
        hasSwappedToPod = false;
        podProced = false;
    }

    private boolean hasEscapePodInInventory() {
        if (mc.thePlayer == null) return false;
        if (isWearingEscapePodPants()) return true;
        for (int i = 0; i < 36; i++) {
            if (isEscapePod(mc.thePlayer.inventory.mainInventory[i])) return true;
        }
        return false;
    }

    private boolean isEscapePod(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemArmor)) return false;
        if (((ItemArmor) stack.getItem()).armorType != 2) return false;
        if (stack.getDisplayName().toLowerCase().contains("escape")) return true;
        return stack.hasTagCompound() && containsEscapeInNBT(stack.getTagCompound());
    }

    private boolean isWearingEscapePodPants() {
        ItemStack pants = mc.thePlayer.inventory.armorInventory[2];
        if (pants == null) return false;
        if (pants.getDisplayName().toLowerCase().contains("escape")) return true;
        return pants.hasTagCompound() && containsEscapeInNBT(pants.getTagCompound());
    }

    private int findEscapePantsSlotInHotbar() {
        for (int i = 0; i < 9; i++) {
            if (isEscapePod(mc.thePlayer.inventory.mainInventory[i])) return i;
        }
        return -1;
    }

    private int findMatchingArmorContainerSlot(ItemStack target, int armorType) {
        if (target == null || mc.thePlayer == null) return -1;
        for (int i = 0; i < 9; i++) {
            ItemStack s = mc.thePlayer.inventory.mainInventory[i];
            if (s != null && s.getItem() instanceof ItemArmor
                    && ((ItemArmor) s.getItem()).armorType == armorType
                    && areItemStacksEqual(s, target)) return i;
        }
        for (int i = 9; i < 36; i++) {
            ItemStack s = mc.thePlayer.inventory.mainInventory[i];
            if (s != null && s.getItem() instanceof ItemArmor
                    && ((ItemArmor) s.getItem()).armorType == armorType
                    && areItemStacksEqual(s, target)) return i;
        }
        return -1;
    }

    private boolean areItemStacksEqual(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getMetadata() != b.getMetadata()) return false;
        String na = a.hasDisplayName() ? a.getDisplayName() : "";
        String nb = b.hasDisplayName() ? b.getDisplayName() : "";
        return na.equals(nb);
    }

    private boolean containsEscapeInNBT(NBTTagCompound tag) {
        for (String key : tag.getKeySet()) {
            Object value = tag.getTag(key);
            if (value instanceof NBTTagCompound) {
                if (containsEscapeInNBT((NBTTagCompound) value)) return true;
            } else {
                if (value.toString().toLowerCase().contains("escape")) return true;
            }
        }
        return false;
    }

    private boolean isVenomed() {
        if (mc.thePlayer == null) return false;
        for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            if (effect.getPotionID() == Potion.poison.id) return true;
        }
        return false;
    }

    private void sendMessage(String message) {
        String formatted = Wrapper.Colors.dark_gray + "["
                + Wrapper.Colors.dark_red + "B"
                + Wrapper.Colors.dark_gray + "] "
                + Wrapper.Colors.dark_gray + "- "
                + message + Wrapper.Colors.white + ".";
        Wrapper.addChatMessage(formatted);
    }
}
