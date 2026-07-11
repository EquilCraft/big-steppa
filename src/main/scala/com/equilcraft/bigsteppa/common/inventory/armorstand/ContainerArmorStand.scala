package com.equilcraft.bigsteppa.common.inventory.armorstand

import com.equilcraft.bigsteppa.common.inventory.armorstand.ContainerArmorStand._
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand.slotCount
import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory.{Container, Slot}
import net.minecraft.item.ItemStack

class ContainerArmorStand(
  playerInventory: InventoryPlayer,
  armorStand: TileArmorStand
) extends Container {

  for (armorType <- 0 until slotCount) {
    addSlotToContainer(
      new SlotArmorStand(armorStand, armorType, 80, 8 + armorType * 18, armorType)
    )
  }

  for {
    row <- 0 until 3
    column <- 0 until 9
  } {
    addSlotToContainer(
      new Slot(
        playerInventory,
        column + row * 9 + 9,
        8 + column * 18,
        84 + row * 18
      )
    )
  }

  for (column <- 0 until 9) {
    addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 142))
  }

  for (armorType <- 0 until slotCount) {
    val playerInventorySlot = 39 - armorType
    addSlotToContainer(
      new SlotArmorStand(
        playerInventory,
        playerInventorySlot,
        126,
        8 + armorType * 18,
        armorType
      )
    )
  }

  override def canInteractWith(player: EntityPlayer): Boolean =
    armorStand.isUseableByPlayer(player)

  override def transferStackInSlot(player: EntityPlayer, index: Int): ItemStack = {
    val sourceSlot =
      if (index >= 0 && index < inventorySlots.size()) {
        inventorySlots.get(index).asInstanceOf[Slot]
      } else {
        null
      }
    if (sourceSlot == null || !sourceSlot.getHasStack) return null

    val source = sourceSlot.getStack
    val original = source.copy()
    val armorType = getArmorType(source)

    val moved =
      if (index >= standStart && index < standEnd) {
        val playerArmorIndex = playerArmorStart + index
        mergeItemStack(source, playerArmorIndex, playerArmorIndex + 1, false) ||
          mergeItemStack(source, playerMainStart, hotbarEnd, true)
      } else if (index >= playerArmorStart && index < playerArmorEnd) {
        val standIndex = index - playerArmorStart
        mergeItemStack(source, standIndex, standIndex + 1, false) ||
          mergeItemStack(source, playerMainStart, hotbarEnd, true)
      } else if (armorType >= 0) {
        mergeItemStack(source, armorType, armorType + 1, false) ||
          mergeItemStack(
            source,
            playerArmorStart + armorType,
            playerArmorStart + armorType + 1,
            false
          )
      } else if (index >= playerMainStart && index < playerMainEnd) {
        mergeItemStack(source, hotbarStart, hotbarEnd, false)
      } else if (index >= hotbarStart && index < hotbarEnd) {
        mergeItemStack(source, playerMainStart, playerMainEnd, false)
      } else {
        false
      }

    if (!moved) return null

    if (source.stackSize == 0) sourceSlot.putStack(null)
    else sourceSlot.onSlotChanged()

    if (source.stackSize == original.stackSize) return null

    sourceSlot.onPickupFromSlot(player, source)
    original
  }

  private def getArmorType(stack: ItemStack): Int =
    (0 until slotCount)
      .find(SlotArmorStand.isValidArmor(stack, _))
      .getOrElse(-1)
}

object ContainerArmorStand {
  private final val standStart = 0
  private final val standEnd = 4
  private final val playerMainStart = 4
  private final val playerMainEnd = 31
  private final val hotbarStart = 31
  private final val hotbarEnd = 40
  private final val playerArmorStart = 40
  private final val playerArmorEnd = 44
}
