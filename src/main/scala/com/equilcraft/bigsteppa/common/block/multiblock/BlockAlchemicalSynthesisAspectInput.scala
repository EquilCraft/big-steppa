package com.equilcraft.bigsteppa.common.block.multiblock

import com.equilcraft.bigsteppa.common.tile.TileAlchemicalSynthesisAspectInput
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

final class BlockAlchemicalSynthesisAspectInput extends Block(Material.iron) with ITileEntityProvider {
  this.setHardness(5.0F)
  this.setResistance(10.0F)
  this.setStepSound(Block.soundTypeMetal)
  this.setCreativeTab(CreativeTabs.tabRedstone)
  this.setBlockTextureName("minecraft:beacon")

  override def createNewTileEntity(world: World, metadata: Int): TileEntity =
    new TileAlchemicalSynthesisAspectInput
}
