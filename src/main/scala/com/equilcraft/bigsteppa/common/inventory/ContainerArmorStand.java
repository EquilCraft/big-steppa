package com.equilcraft.bigsteppa.common.inventory;

import com.equilcraft.bigsteppa.common.tile.TileArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerArmorStand extends Container {

    private static final int STAND_START = 0;
    private static final int STAND_END = 4;
    private static final int PLAYER_MAIN_START = 4;
    private static final int PLAYER_MAIN_END = 31;
    private static final int HOTBAR_START = 31;
    private static final int HOTBAR_END = 40;
    private static final int PLAYER_ARMOR_START = 40;
    private static final int PLAYER_ARMOR_END = 44;

    private final TileArmorStand armorStand;

    public ContainerArmorStand(InventoryPlayer playerInventory, TileArmorStand armorStand) {
        this.armorStand = armorStand;

        for (int armorType = 0; armorType < TileArmorStand.SLOT_COUNT; armorType++) {
            addSlotToContainer(
                new SlotArmorStand(armorStand, armorType, 80, 8 + armorType * 18, armorType)
            );
        }

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlotToContainer(
                    new Slot(
                        playerInventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                    )
                );
            }
        }

        for (int column = 0; column < 9; column++) {
            addSlotToContainer(
                new Slot(playerInventory, column, 8 + column * 18, 142)
            );
        }

        for (int armorType = 0; armorType < TileArmorStand.SLOT_COUNT; armorType++) {
            int playerInventorySlot = 39 - armorType;
            addSlotToContainer(
                new SlotArmorStand(
                    playerInventory,
                    playerInventorySlot,
                    126,
                    8 + armorType * 18,
                    armorType
                )
            );
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return armorStand.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot sourceSlot = index >= 0 && index < inventorySlots.size()
            ? (Slot) inventorySlots.get(index)
            : null;
        if (sourceSlot == null || !sourceSlot.getHasStack()) {
            return null;
        }

        ItemStack source = sourceSlot.getStack();
        ItemStack original = source.copy();
        int armorType = getArmorType(source);
        boolean moved;

        if (index >= STAND_START && index < STAND_END) {
            int playerArmorIndex = PLAYER_ARMOR_START + index;
            moved = mergeItemStack(source, playerArmorIndex, playerArmorIndex + 1, false)
                || mergeItemStack(source, PLAYER_MAIN_START, HOTBAR_END, true);
        } else if (index >= PLAYER_ARMOR_START && index < PLAYER_ARMOR_END) {
            int standIndex = index - PLAYER_ARMOR_START;
            moved = mergeItemStack(source, standIndex, standIndex + 1, false)
                || mergeItemStack(source, PLAYER_MAIN_START, HOTBAR_END, true);
        } else if (armorType >= 0) {
            moved = mergeItemStack(source, armorType, armorType + 1, false)
                || mergeItemStack(
                    source,
                    PLAYER_ARMOR_START + armorType,
                    PLAYER_ARMOR_START + armorType + 1,
                    false
                );
        } else if (index >= PLAYER_MAIN_START && index < PLAYER_MAIN_END) {
            moved = mergeItemStack(source, HOTBAR_START, HOTBAR_END, false);
        } else if (index >= HOTBAR_START && index < HOTBAR_END) {
            moved = mergeItemStack(source, PLAYER_MAIN_START, PLAYER_MAIN_END, false);
        } else {
            moved = false;
        }

        if (!moved) {
            return null;
        }
        if (source.stackSize == 0) {
            sourceSlot.putStack(null);
        } else {
            sourceSlot.onSlotChanged();
        }
        if (source.stackSize == original.stackSize) {
            return null;
        }
        sourceSlot.onPickupFromSlot(player, source);
        return original;
    }

    private static int getArmorType(ItemStack stack) {
        for (int armorType = 0; armorType < TileArmorStand.SLOT_COUNT; armorType++) {
            if (SlotArmorStand.isValidArmor(stack, armorType)) {
                return armorType;
            }
        }
        return -1;
    }
}
