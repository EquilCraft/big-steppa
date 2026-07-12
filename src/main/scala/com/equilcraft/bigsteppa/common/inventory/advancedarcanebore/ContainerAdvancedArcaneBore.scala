package com.equilcraft.bigsteppa.common.inventory.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import net.minecraft.entity.player.{EntityPlayer, InventoryPlayer}
import net.minecraft.inventory.{Container, ICrafting, Slot}
import net.minecraft.item.{ItemPickaxe, ItemStack}
import thaumcraft.common.container.SlotLimitedByClass
import thaumcraft.common.items.wands.foci.ItemFocusExcavation

final class ContainerAdvancedArcaneBore(
  playerInventory: InventoryPlayer,
  val bore: TileAdvancedArcaneBore
) extends Container {
  this.addSlotToContainer(new SlotLimitedByClass(classOf[ItemFocusExcavation], bore, 0, 26, 18))
  this.addSlotToContainer(new SlotLimitedByClass(classOf[ItemPickaxe], bore, 1, 74, 18))

  for {
    row <- 0 until 3
    column <- 0 until 9
  } {
    this.addSlotToContainer(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 83 + row * 18))
  }

  for (column <- 0 until 9) {
    this.addSlotToContainer(new Slot(playerInventory, column, 8 + column * 18, 141))
  }

  override def canInteractWith(player: EntityPlayer): Boolean = this.bore.isUseableByPlayer(player)

  override def enchantItem(player: EntityPlayer, action: Int): Boolean = {
    if (!this.canInteractWith(player)) false
    else {
      action match {
        case 0 | 1 =>
          this.bore.setExcavationWidth(this.bore.excavationWidth + (if (action == 0) -2 else 2))
          true
        case value if value >= 1001 && value <= 1128 =>
          this.bore.setExcavationDepth(value - 1000)
          true
        case _ => false
      }
    }
  }

  override def addCraftingToCrafters(crafter: ICrafting): Unit = {
    super.addCraftingToCrafters(crafter)
    crafter.sendProgressBarUpdate(this, 0, this.bore.excavationWidth)
    crafter.sendProgressBarUpdate(this, 1, this.bore.storedPerditio)
    crafter.sendProgressBarUpdate(this, 2, this.bore.excavationDepth)
  }

  override def detectAndSendChanges(): Unit = {
    super.detectAndSendChanges()
    var index = 0
    while (index < this.crafters.size()) {
      val crafter = this.crafters.get(index).asInstanceOf[ICrafting]
      crafter.sendProgressBarUpdate(this, 0, this.bore.excavationWidth)
      crafter.sendProgressBarUpdate(this, 1, this.bore.storedPerditio)
      crafter.sendProgressBarUpdate(this, 2, this.bore.excavationDepth)
      index += 1
    }
  }

  override def updateProgressBar(id: Int, value: Int): Unit = {
    if (id == 0) this.bore.setExcavationWidth(value)
    else if (id == 1) this.bore.setStoredPerditio(value)
    else if (id == 2) this.bore.setExcavationDepth(value)
  }

  override def transferStackInSlot(player: EntityPlayer, index: Int): ItemStack = {
    if (index < 0 || index >= this.inventorySlots.size()) return null
    val slot = this.inventorySlots.get(index).asInstanceOf[Slot]
    if (!slot.getHasStack) return null
    val stack = slot.getStack
    val original = stack.copy()
    val moved =
      if (index < 2) this.mergeItemStack(stack, 2, this.inventorySlots.size(), true)
      else if (stack.getItem.isInstanceOf[ItemFocusExcavation]) this.mergeItemStack(stack, 0, 1, false)
      else if (stack.getItem.isInstanceOf[ItemPickaxe]) this.mergeItemStack(stack, 1, 2, false)
      else if (index < 29) this.mergeItemStack(stack, 29, 38, false)
      else this.mergeItemStack(stack, 2, 29, false)
    if (!moved) return null
    if (stack.stackSize == 0) slot.putStack(null) else slot.onSlotChanged()
    if (stack.stackSize == original.stackSize) null else original
  }
}
