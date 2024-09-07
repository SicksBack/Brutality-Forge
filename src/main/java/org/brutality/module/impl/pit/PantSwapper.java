package org.brutality.module.impl.pit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.Wrapper;
import org.lwjgl.input.Keyboard;

public class PantSwapper extends Module {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private volatile boolean isSwapping = false;
    private final NumberSetting delay = new NumberSetting("Delay", this, 100, 0, 500, 0);
    private final BooleanSetting includeBoots = new BooleanSetting("Include Boots", this, false);

    public PantSwapper() {
        super("PantSwapper", "Automatically swaps pants and optionally boots in inventory.", Category.PIT);
        this.addSettings(delay, includeBoots);
        setKey(Keyboard.KEY_C);
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        isSwapping = false;
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        isSwapping = false;
    }

    @SubscribeEvent
    public void onKeyPress(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (mc.thePlayer == null || mc.theWorld == null || mc.currentScreen != null || isSwapping) {
            return;
        }

        // Replace the condition with the actual trigger for swapping
        if (true /* Replace with actual condition to trigger swapping */) {
            isSwapping = true;
            if (delay.getValue() > 0) {
                scheduler.schedule(this::openInventory, 100L, TimeUnit.MILLISECONDS);
            } else {
                openInventory();
            }
        }
    }

    private void openInventory() {
        mc.addScheduledTask(() -> {
            if (delay.getValue() > 0) {
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                scheduler.schedule(this::swapPants, (long) delay.getValue(), TimeUnit.MILLISECONDS);
            } else {
                swapPants();
            }
        });
    }

    private void swapPants() {
        mc.addScheduledTask(() -> {
            if (mc.currentScreen instanceof GuiInventory) {
                int pantsSlot = this.findPantsSlotInHotbar();
                if (pantsSlot != -1) {
                    mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
                    sendSuccessMessage("Pants");
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 7, pantsSlot, 2, mc.thePlayer);
                    if (includeBoots.isEnabled()) {
                        if (delay.getValue() > 0) {
                            scheduler.schedule(this::swapBoots, (long) delay.getValue(), TimeUnit.MILLISECONDS);
                        } else {
                            swapBoots();
                        }
                    } else {
                        closeInventory();
                        this.toggle(); // Toggle off the module after swapping pants
                    }
                } else {
                    mc.thePlayer.playSound("mob.villager.no", 1.0f, 0.7f);
                    sendFailureMessage("No Leggings Found");
                    closeInventory();
                    this.toggle(); // Toggle off the module after failure
                }
            } else {
                isSwapping = false;
            }
        });
    }

    private void swapBoots() {
        mc.addScheduledTask(() -> {
            if (mc.currentScreen instanceof GuiInventory) {
                int bootsSlot = this.findBootsSlotInHotbar();
                if (bootsSlot != -1) {
                    mc.thePlayer.playSound("random.click", 1.0f, 1.2f);
                    sendSuccessMessage("Boots");
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 8, bootsSlot, 2, mc.thePlayer);
                } else {
                    mc.thePlayer.playSound("mob.villager.no", 1.0f, 0.7f);
                    sendFailureMessage("No Boots Found");
                }
                closeInventory();
                this.toggle(); // Toggle off the module after swapping boots or failure
            } else {
                isSwapping = false;
            }
        });
    }

    private int findPantsSlotInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.armorType == 2) { // Check if the item is pants (leggings)
                    return i;
                }
            }
        }
        return -1;
    }

    private int findBootsSlotInHotbar() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && itemStack.getItem() instanceof ItemArmor) {
                ItemArmor armor = (ItemArmor) itemStack.getItem();
                if (armor.armorType == 3) { // Check if the item is boots
                    return i;
                }
            }
        }
        return -1;
    }

    private void closeInventory() {
        mc.addScheduledTask(() -> {
            mc.thePlayer.closeScreen();
            isSwapping = false;
        });
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
