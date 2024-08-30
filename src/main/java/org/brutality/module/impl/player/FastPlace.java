package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent; // Importing Forge's TickEvent
import org.brutality.events.RightClickEvent; // Importing your custom RightClickEvent
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.ReflectionUtils;

public class FastPlace extends Module {

    // Settings
    private final NumberSetting tickDelay = new NumberSetting("Tick Delay", this, 0, 0, 4, 1);
    private final BooleanSetting blocksOnly = new BooleanSetting("Blocks Only", this, true);

    private static final Minecraft mc = Minecraft.getMinecraft();

    public FastPlace() {
        super("FastPlace", "Place blocks/items faster by reducing the right-click delay", Category.PLAYER);
        this.addSettings(tickDelay, blocksOnly);
        MinecraftForge.EVENT_BUS.register(this); // Register the module to listen for events
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent e) { // Using Forge's TickEvent
        if (e.phase == TickEvent.Phase.END) {
            if (!mc.inGameHasFocus || ReflectionUtils.rightClickDelayTimerField == null) {
                return;
            }

            if (blocksOnly.isEnabled()) {
                ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem(); // For Minecraft 1.8.9
                if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) {
                    return;
                }
            }

            try {
                int currentDelay = ReflectionUtils.rightClickDelayTimerField.getInt(mc);
                System.out.println("Current right-click delay: " + currentDelay);

                int delay = (int) tickDelay.getValue();
                if (delay == 0) {
                    ReflectionUtils.rightClickDelayTimerField.set(mc, 0);
                    System.out.println("Set right-click delay to 0");
                } else if (delay < 4 && currentDelay == 4) {
                    ReflectionUtils.rightClickDelayTimerField.set(mc, delay);
                    System.out.println("Adjusted right-click delay to: " + delay);
                }
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(RightClickEvent event) { // Using your custom RightClickEvent
        try {
            int delay = (int) tickDelay.getValue();
            if (delay == 0) {
                ReflectionUtils.rightClickDelayTimerField.set(mc, 0);
                System.out.println("Set right-click delay to 0 on right-click event");
            }
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            // Reset right-click delay to default when the module is disabled
            if (ReflectionUtils.rightClickDelayTimerField != null) {
                ReflectionUtils.rightClickDelayTimerField.set(mc, 4);
            }
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        super.onDisable();
    }
}
