package com.equilcraft.bigsteppa.common.container.armorstand

import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import net.minecraft.inventory.{IInventory, Slot}
import net.minecraft.item.ItemStack

class SlotArmorStand(
  inventory: IInventory,
  slot: Int,
  x: Int,
  y: Int,
  armorType: Int
) extends Slot(inventory, slot, x, y) {

  override def isItemValid(stack: ItemStack): Boolean =
    inventory match {
      case armorStand: TileArmorStand => armorStand.isItemValidForSlot(armorType, stack)
      case _ => SlotArmorStand.isValidArmor(stack, armorType)
    }

  override def getSlotStackLimit: Int = 1
}

object SlotArmorStand {
  def isValidArmor(stack: ItemStack, armorType: Int): Boolean =
    stack != null && stack.getItem.isValidArmor(stack, armorType, null)
}
