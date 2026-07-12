package com.equilcraft.bigsteppa.common.block.advancedarcanebore

import com.equilcraft.bigsteppa.common.tile.advancedarcanebore.TileAdvancedArcaneBoreBase
import net.minecraft.block.material.Material
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.IIcon
import net.minecraft.world.World
import thaumcraft.common.Thaumcraft
import thaumcraft.common.config.ConfigBlocks

final class BlockAdvancedArcaneBoreBase extends Block(Material.wood) with ITileEntityProvider {
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

  override def createNewTileEntity(world: World, metadata: Int): TileEntity = new TileAdvancedArcaneBoreBase
}
