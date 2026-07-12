package com.equilcraft.bigsteppa.common.block.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBoreBase
import net.minecraft.block.{Block, BlockContainer}
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.World
import thaumcraft.common.Thaumcraft
import thaumcraft.common.config.ConfigBlocks

import java.util

final class BlockAdvancedArcaneBoreBase extends BlockContainer(Material.wood) {
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

  override def createTileEntity(world: World, metadata: Int): TileEntity = new TileAdvancedArcaneBoreBase

  override def createNewTileEntity(world: World, metadata: Int): TileEntity = new TileAdvancedArcaneBoreBase

  override def damageDropped(metadata: Int): Int = 4

  override def getSubBlocks(item: Item, tab: CreativeTabs, items: util.List[ItemStack]): Unit = {
    items.add(new ItemStack(item, 1, 4))
  }

  override def onBlockActivated(
    world: World,
    x: Int,
    y: Int,
    z: Int,
    player: net.minecraft.entity.player.EntityPlayer,
    side: Int,
    hitX: Float,
    hitY: Float,
    hitZ: Float
  ): Boolean = true
}
