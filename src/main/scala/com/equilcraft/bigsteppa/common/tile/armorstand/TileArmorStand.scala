package com.equilcraft.bigsteppa.common.tile.armorstand

import com.equilcraft.bigsteppa.common.tile.SpatialRegistered
import com.equilcraft.bigsteppa.api.internal.BlocksChaosStructureRegistry
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand.slotCount
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagCompound, NBTTagList}
import net.minecraft.network.{NetworkManager, Packet}
import net.minecraft.network.play.server.S35PacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB

class TileArmorStand
    extends TileEntity
    with IInventory
    with SpatialRegistered[TileArmorStand] {

  private var inventory = new Array[ItemStack](slotCount)


  override protected def spatialRegistry: BlocksChaosStructureRegistry[TileArmorStand] = TileArmorStand.registry


  override def canUpdate: Boolean = false


  override def getSizeInventory: Int = inventory.length

  override def getStackInSlot(slot: Int): ItemStack =
    if (slot >= 0 && slot < inventory.length) inventory(slot) else null

  override def decrStackSize(slot: Int, amount: Int): ItemStack = {
    val stack = getStackInSlot(slot)
    if (stack == null) return null

    val result =
      if (stack.stackSize <= amount) {
        inventory(slot) = null
        stack
      } else {
        val split = stack.splitStack(amount)
        if (stack.stackSize == 0) inventory(slot) = null
        split
      }

    inventoryChanged()
    result
  }

  override def getStackInSlotOnClosing(slot: Int): ItemStack = {
    val stack = getStackInSlot(slot)
    if (stack != null) {
      inventory(slot) = null
      inventoryChanged()
    }
    stack
  }

  override def setInventorySlotContents(slot: Int, stack: ItemStack): Unit = {
    if (slot < 0 || slot >= inventory.length) return

    inventory(slot) = stack
    if (stack != null && stack.stackSize > getInventoryStackLimit) {
      stack.stackSize = getInventoryStackLimit
    }
    inventoryChanged()
  }

  override def getInventoryName: String = "container.bigsteppa.armorStand"

  override def hasCustomInventoryName: Boolean = false

  override def getInventoryStackLimit: Int = 1

  override def isUseableByPlayer(player: EntityPlayer): Boolean =
    worldObj != null &&
      worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
        player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D

  override def openInventory(): Unit = ()

  override def closeInventory(): Unit = ()

  override def isItemValidForSlot(slot: Int, stack: ItemStack): Boolean =
    stack != null &&
      slot >= 0 &&
        slot < inventory.length &&
          stack.getItem.isValidArmor(stack, slot, null)

  def clearWithoutSync(): Unit = {
    inventory = new Array[ItemStack](slotCount)
    markDirty()
  }

  private def inventoryChanged(): Unit = {
    markDirty()
    if (worldObj != null) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
    }
  }


  override def readFromNBT(tag: NBTTagCompound): Unit = {
    super.readFromNBT(tag)
    inventory = new Array[ItemStack](slotCount)
    val items = tag.getTagList("Items", 10)

    for (index <- 0 until items.tagCount()) {
      val itemTag = items.getCompoundTagAt(index)
      val slot = itemTag.getByte("Slot") & 255
      if (slot < inventory.length) {
        inventory(slot) = ItemStack.loadItemStackFromNBT(itemTag)
      }
    }
  }

  override def writeToNBT(tag: NBTTagCompound): Unit = {
    super.writeToNBT(tag)
    val items = new NBTTagList

    for (slot <- inventory.indices) {
      val stack = inventory(slot)
      if (stack != null) {
        val itemTag = new NBTTagCompound
        itemTag.setByte("Slot", slot.toByte)
        stack.writeToNBT(itemTag)
        items.appendTag(itemTag)
      }
    }
    tag.setTag("Items", items)
  }


  override def getDescriptionPacket: Packet = {
    val tag = new NBTTagCompound
    writeToNBT(tag)
    new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag)
  }

  override def onDataPacket(
    networkManager: NetworkManager,
    packet: S35PacketUpdateTileEntity
  ): Unit = {
    readFromNBT(packet.func_148857_g())
    if (worldObj != null) {
      worldObj.markBlockRangeForRenderUpdate(
        xCoord,
        yCoord,
        zCoord,
        xCoord,
        yCoord + 1,
        zCoord
      )
    }
  }

  @SideOnly(Side.CLIENT)
  override def getRenderBoundingBox: AxisAlignedBB =
    AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1)

  override def validate(): Unit = {
    super.validate()
    spatialValidate()
  }

  override def invalidate(): Unit = {
    super.invalidate()
    spatialInvalidate()
  }
}

object TileArmorStand {
  final val slotHead = 0
  final val slotChest = 1
  final val slotLegs = 2
  final val slotFeet = 3
  final val slotCount = 4

  val registry = new BlocksChaosStructureRegistry[TileArmorStand]()
}
