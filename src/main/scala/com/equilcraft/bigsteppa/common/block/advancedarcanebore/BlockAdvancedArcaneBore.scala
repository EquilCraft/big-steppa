package com.equilcraft.bigsteppa.common.block.advancedarcanebore

import com.equilcraft.bigsteppa.BigSteppa
import com.equilcraft.bigsteppa.common.gui.advancedarcanebore.AdvancedArcaneBoreGuiProvider
import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBore
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.util.ForgeDirection
import thaumcraft.common.Thaumcraft
import thaumcraft.common.config.ConfigBlocks
import thaumcraft.common.items.wands.ItemWandCasting
import thaumcraft.common.lib.utils.InventoryUtils

final class BlockAdvancedArcaneBore extends BlockContainer(Material.wood) {
  this.setHardness(2.5F)
  this.setResistance(10.0F)
  this.setStepSound(Block.soundTypeWood)
  this.setCreativeTab(Thaumcraft.tabTC)

  override def registerBlockIcons(register: IIconRegister): Unit = {
    this.blockIcon = register.registerIcon("thaumcraft:woodplain")
  }

  override def getIcon(side: Int, metadata: Int): IIcon = this.blockIcon

  override def renderAsNormalBlock: Boolean = false

  override def isOpaqueCube: Boolean = false

  override def getRenderType: Int = ConfigBlocks.blockWoodenDeviceRI

  override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int): Unit = {
    val direction = world.getTileEntity(x, y, z) match {
      case bore: TileAdvancedArcaneBore => bore.orientation
      case _ => ForgeDirection.UNKNOWN
    }
    this.setBlockBounds(
      if (direction.offsetX < 0) -1.0F else 0.0F,
      if (direction.offsetY < 0) -1.0F else 0.0F,
      if (direction.offsetZ < 0) -1.0F else 0.0F,
      if (direction.offsetX > 0) 2.0F else 1.0F,
      if (direction.offsetY > 0) 2.0F else 1.0F,
      if (direction.offsetZ > 0) 2.0F else 1.0F
    )
  }

  override def createNewTileEntity(world: World, metadata: Int): TileEntity = new TileAdvancedArcaneBore

  override def onBlockPlacedBy(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    placer: EntityLivingBase,
    stack: ItemStack
  ): Unit = {
    world.getTileEntity(x, y, z) match {
      case bore: TileAdvancedArcaneBore if placer.isInstanceOf[EntityPlayer] =>
        bore.setOwner(placer.asInstanceOf[EntityPlayer])
      case _ =>
    }
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
    val held = player.getHeldItem
    if (world.isRemote || held != null && held.getItem.isInstanceOf[ItemWandCasting]) {
      true
    } else {
      player.openGui(BigSteppa, AdvancedArcaneBoreGuiProvider.advancedArcaneBoreGuiId, world, x, y, z)
      true
    }
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, neighbor: Block): Unit = ()

  override def breakBlock(world: World, x: Int, y: Int, z: Int, oldBlock: Block, metadata: Int): Unit = {
    InventoryUtils.dropItems(world, x, y, z)
    super.breakBlock(world, x, y, z, oldBlock, metadata)
  }
}
