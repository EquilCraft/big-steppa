package com.equilcraft.bigsteppa.common.inventory

import com.equilcraft.bigsteppa.common.inventory.ContainerArmorStand._
import com.equilcraft.bigsteppa.common.tile.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.TileArmorStand.SlotCount
import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory.{Container, Slot}
import net.minecraft.item.ItemStack

class ContainerArmorStand(
  playerInventory: InventoryPlayer,
  armorStand: TileArmorStand
) extends Container {

  for (armorType <- 0 until SlotCount) {
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

  for (armorType <- 0 until SlotCount) {
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
      if (index >= StandStart && index < StandEnd) {
        val playerArmorIndex = PlayerArmorStart + index
        mergeItemStack(source, playerArmorIndex, playerArmorIndex + 1, false) ||
          mergeItemStack(source, PlayerMainStart, HotbarEnd, true)
      } else if (index >= PlayerArmorStart && index < PlayerArmorEnd) {
        val standIndex = index - PlayerArmorStart
        mergeItemStack(source, standIndex, standIndex + 1, false) ||
          mergeItemStack(source, PlayerMainStart, HotbarEnd, true)
      } else if (armorType >= 0) {
        mergeItemStack(source, armorType, armorType + 1, false) ||
          mergeItemStack(
            source,
            PlayerArmorStart + armorType,
            PlayerArmorStart + armorType + 1,
            false
          )
      } else if (index >= PlayerMainStart && index < PlayerMainEnd) {
        mergeItemStack(source, HotbarStart, HotbarEnd, false)
      } else if (index >= HotbarStart && index < HotbarEnd) {
        mergeItemStack(source, PlayerMainStart, PlayerMainEnd, false)
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
    (0 until SlotCount)
      .find(SlotArmorStand.isValidArmor(stack, _))
      .getOrElse(-1)
}

object ContainerArmorStand {
  private final val StandStart = 0
  private final val StandEnd = 4
  private final val PlayerMainStart = 4
  private final val PlayerMainEnd = 31
  private final val HotbarStart = 31
  private final val HotbarEnd = 40
  private final val PlayerArmorStart = 40
  private final val PlayerArmorEnd = 44
}
