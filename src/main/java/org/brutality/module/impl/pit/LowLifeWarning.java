package org.brutality.module.impl.pit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.ChatUtil;
import org.brutality.utils.interfaces.MC;

import java.util.HashMap;
import java.util.Map;

public class LowLifeWarning extends Module implements MC {

    public LowLifeWarning() {
        super("LowLifeWarning", "s", Category.PIT);
    }


    public void onUpdate() {
        if (!isToggled()) return;

        Map<String, Integer> lowLifeItems = new HashMap<>();

        // Check hotbar
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            checkItemStack(itemStack, lowLifeItems);
        }

        // Check inventory
        for (int i = 9; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            checkItemStack(itemStack, lowLifeItems);
        }

        if (!lowLifeItems.isEmpty()) {
            displayLowLifeWarning(lowLifeItems);
        }
    }

    private void checkItemStack(ItemStack itemStack, Map<String, Integer> lowLifeItems) {
        if (itemStack != null && itemStack.hasTagCompound()) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();
            if (tagCompound.hasKey("tag")) {
                NBTTagCompound tag = tagCompound.getCompoundTag("tag");
                if (tag.hasKey("Lives")) {
                    int lives = tag.getInteger("Lives");
                    if (lives <= 3) {
                        String itemName = itemStack.getDisplayName();
                        lowLifeItems.put(itemName, lives);
                    }
                }
            }
        }
    }

    private void displayLowLifeWarning(Map<String, Integer> lowLifeItems) {
        StringBuilder message = new StringBuilder("Mystic Lives:");
        for (Map.Entry<String, Integer> entry : lowLifeItems.entrySet()) {
            message.append("\n").append(entry.getKey()).append(" Is On ").append(entry.getValue()).append(" Life/s");
        }
        ChatUtil.displayDraggableMessage(message.toString());
    }
}
