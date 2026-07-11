package com.equilcraft.bigsteppa.common.blocks.armorstand

import com.equilcraft.bigsteppa.BigSteppa
import com.equilcraft.bigsteppa.common.gui.ArmorStandGuiHandler
import com.equilcraft.bigsteppa.common.tile.TileArmorStand
import com.equilcraft.bigsteppa.common.tile.armorstand.TileArmorStand.{SlotCount, SlotFeet, SlotLegs}
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.{Entity, EntityLivingBase}
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{AxisAlignedBB, IIcon, MathHelper}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection

import java.util
import java.util.Random

class BlockArmorStand extends BlockContainer(Material.wood) {
  private var removingOtherHalf = false

  setHardness(2.5F)
  setResistance(5.0F)
  setStepSound(Block.soundTypeWood)
  setCreativeTab(CreativeTabs.tabDecorations)

  override def registerBlockIcons(register: IIconRegister): Unit = {
    blockIcon = register.registerIcon("minecraft:planks_oak")
  }

  override def getIcon(side: Int, metadata: Int): IIcon = blockIcon

  override def renderAsNormalBlock: Boolean = false

  override def isOpaqueCube: Boolean = false

  override def getRenderType: Int = -1

  override def getMobilityFlag: Int = 2

  override def setBlockBoundsBasedOnState(
    world: IBlockAccess,
    x: Int,
    y: Int,
    z: Int
  ): Unit = {
    val rotation = world.getBlockMetadata(x, y, z) & 3
    if ((rotation & 1) == 0) {
      setBlockBounds(0.27F, 0.0F, 0.05F, 0.73F, 1.0F, 0.95F)
    } else {
      setBlockBounds(0.05F, 0.0F, 0.27F, 0.95F, 1.0F, 0.73F)
    }
  }

  override def addCollisionBoxesToList(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    mask: AxisAlignedBB,
    boxes: util.List[AxisAlignedBB],
    entity: Entity
  ): Unit = {
    setBlockBoundsBasedOnState(world, x, y, z)
    super.addCollisionBoxesToList(world, x, y, z, mask, boxes, entity)
  }

  override def getSelectedBoundingBoxFromPool(
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AxisAlignedBB = {
    setBlockBoundsBasedOnState(world, x, y, z)
    super.getSelectedBoundingBoxFromPool(world, x, y, z)
  }

  override def getCollisionBoundingBoxFromPool(
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): AxisAlignedBB = {
    setBlockBoundsBasedOnState(world, x, y, z)
    super.getCollisionBoundingBoxFromPool(world, x, y, z)
  }

  override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean =
    y < world.getHeight - 1 &&
      super.canPlaceBlockAt(world, x, y, z) &&
      world.isAirBlock(x, y + 1, z)

  override def onBlockPlacedBy(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    placer: EntityLivingBase,
    stack: ItemStack
  ): Unit = {
    val rotation =
      (MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5D) + 1) & 3
    world.setBlockMetadataWithNotify(x, y, z, rotation, 3)
    world.setBlock(x, y + 1, z, this, rotation | 4, 3)
  }

  override def onBlockActivated(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    player: EntityPlayer,
    side: Int,
    hitX: Float,
    hitY: Float,
    hitZ: Float
  ): Boolean = {
    val metadata = world.getBlockMetadata(x, y, z)
    val lowerY = if ((metadata & 4) == 0) y else y - 1
    world.getTileEntity(x, lowerY, z) match {
      case armorStand: TileArmorStand =>
        if (world.isRemote) return true

        val standSlot = clickedSlot(metadata, hitY)
        if (player.isSneaking) {
          val powered =
            world.isBlockIndirectlyGettingPowered(x, lowerY, z) ||
              world.isBlockIndirectlyGettingPowered(x, lowerY + 1, z)
          if (powered) {
            for (slot <- 0 until SlotCount) {
              swapWithPlayer(player, armorStand, slot)
            }
          } else {
            swapWithPlayer(player, armorStand, standSlot)
          }
          syncPlayerInventory(player)
          true
        } else {
          val held = player.getHeldItem
          if (
            held != null &&
            armorStand.getStackInSlot(standSlot) == null &&
            armorStand.isItemValidForSlot(standSlot, held)
          ) {
            val inserted = held.copy()
            inserted.stackSize = 1
            armorStand.setInventorySlotContents(standSlot, inserted)
            held.stackSize -= 1
            if (held.stackSize <= 0) {
              player.inventory.setInventorySlotContents(player.inventory.currentItem, null)
            }
            syncPlayerInventory(player)
          } else {
            player.openGui(
              BigSteppa,
              ArmorStandGuiHandler.GuiId,
              world,
              x,
              lowerY,
              z
            )
          }
          true
        }
      case _ => false
    }
  }

  private def clickedSlot(metadata: Int, hitY: Float): Int =
    if ((metadata & 4) == 0) {
      if (hitY < 0.5F) SlotFeet else SlotLegs
    } else {
      if (hitY < 0.5F) TileArmorStand.SlotChest else TileArmorStand.SlotHead
    }

  private def swapWithPlayer(
    player: EntityPlayer,
    armorStand: TileArmorStand,
    standSlot: Int
  ): Unit = {
    val playerArmorSlot = 3 - standSlot
    val onStand = armorStand.getStackInSlot(standSlot)
    val onPlayer = player.inventory.armorInventory(playerArmorSlot)
    armorStand.setInventorySlotContents(standSlot, onPlayer)
    player.inventory.armorInventory(playerArmorSlot) = onStand
    player.inventory.markDirty()
  }

  private def syncPlayerInventory(player: EntityPlayer): Unit = {
    player.inventory.markDirty()
    player match {
      case serverPlayer: EntityPlayerMP =>
        serverPlayer.inventoryContainer.detectAndSendChanges()
      case _ =>
    }
  }

  override def breakBlock(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    oldBlock: Block,
    metadata: Int
  ): Unit = {
    if (removingOtherHalf) {
      super.breakBlock(world, x, y, z, oldBlock, metadata)
      return
    }

    val lowerY = if ((metadata & 4) == 0) y else y - 1
    if (!world.isRemote) {
      world.getTileEntity(x, lowerY, z) match {
        case armorStand: TileArmorStand => dropContents(world, x, lowerY, z, armorStand)
        case _ =>
      }
    }

    val otherY = if ((metadata & 4) == 0) y + 1 else y - 1
    removingOtherHalf = true
    try {
      if (world.getBlock(x, otherY, z) == this) {
        world.setBlockToAir(x, otherY, z)
      }
    } finally {
      removingOtherHalf = false
    }
    super.breakBlock(world, x, y, z, oldBlock, metadata)
  }

  private def dropContents(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    armorStand: TileArmorStand
  ): Unit = {
    val random = world.rand
    for (slot <- 0 until armorStand.getSizeInventory) {
      val stack = armorStand.getStackInSlot(slot)
      if (stack != null) {
        val entity = new EntityItem(
          world,
          x + 0.15D + random.nextFloat() * 0.7D,
          y + 0.2D + random.nextFloat() * 0.8D,
          z + 0.15D + random.nextFloat() * 0.7D,
          stack.copy()
        )
        entity.motionX = random.nextGaussian() * 0.05D
        entity.motionY = random.nextGaussian() * 0.05D + 0.2D
        entity.motionZ = random.nextGaussian() * 0.05D
        world.spawnEntityInWorld(entity)
      }
    }
    armorStand.clearWithoutSync()
  }

  override def getItemDropped(metadata: Int, random: Random, fortune: Int): Item =
    Item.getItemFromBlock(this)

  override def damageDropped(metadata: Int): Int = 0

  override def hasTileEntity(metadata: Int): Boolean = (metadata & 4) == 0

  override def createNewTileEntity(world: World, metadata: Int): TileEntity =
    if ((metadata & 4) == 0) new TileArmorStand else null

  override def rotateBlock(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    axis: ForgeDirection
  ): Boolean = {
    if (axis != ForgeDirection.UP && axis != ForgeDirection.DOWN) return false

    val metadata = world.getBlockMetadata(x, y, z)
    val lowerY = if ((metadata & 4) == 0) y else y - 1
    if (!world.getTileEntity(x, lowerY, z).isInstanceOf[TileArmorStand]) return false

    val currentRotation = metadata & 3
    val rotation =
      if (axis == ForgeDirection.UP) (currentRotation + 1) & 3
      else (currentRotation + 3) & 3

    world.setBlockMetadataWithNotify(x, lowerY, z, rotation, 3)
    if (world.getBlock(x, lowerY + 1, z) == this) {
      world.setBlockMetadataWithNotify(x, lowerY + 1, z, rotation | 4, 3)
    }
    true
  }

  override def getValidRotations(
    world: World,
    x: Int,
    y: Int,
    z: Int
  ): Array[ForgeDirection] =
    Array(ForgeDirection.UP, ForgeDirection.DOWN)
}
