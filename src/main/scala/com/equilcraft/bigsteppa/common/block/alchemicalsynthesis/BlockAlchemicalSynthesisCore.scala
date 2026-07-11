package com.equilcraft.bigsteppa.common.block.alchemicalsynthesis

import com.equilcraft.bigsteppa.common.block.BlockMultiblockController
import com.equilcraft.bigsteppa.common.tile.alchemicalsynthesis.TileAlchemicalSynthesisCore
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

class BlockAlchemicalSynthesisCore
    extends BlockMultiblockController[TileAlchemicalSynthesisCore](Material.iron) {

  this.setHardness(5.0F)
  this.setResistance(10.0F)
  this.setStepSound(Block.soundTypeMetal)
  this.setCreativeTab(CreativeTabs.tabRedstone)
  this.setBlockTextureName("minecraft:furnace_front_off")

  override def createNewTileEntity(world: World, metadata: Int): TileEntity =
    new TileAlchemicalSynthesisCore

  override protected def formedTranslationKey: String = "chat.bigsteppa.alchemicalSynthesis.formed"
  override protected def incompleteTranslationKey: String = "chat.bigsteppa.alchemicalSynthesis.incomplete"
}
