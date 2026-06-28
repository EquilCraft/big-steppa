package com.equilcraft.bigsteppa.common.inventory;

import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotArmorStand extends Slot {

    private final int armorType;

    public SlotArmorStand(IInventory inventory, int slot, int x, int y, int armorType) {
        super(inventory, slot, x, y);
        this.armorType = armorType;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if (inventory instanceof TileArmorStand) {
            return ((TileArmorStand) inventory).isItemValidForSlot(armorType, stack);
        }
        return isValidArmor(stack, armorType);
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }

    public static boolean isValidArmor(ItemStack stack, int armorType) {
        return stack != null && stack.getItem().isValidArmor(stack, armorType, null);
    }
}
