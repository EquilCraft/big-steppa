package com.equilcraft.bigsteppa.common.block.multiblock

import com.equilcraft.bigsteppa.common.tile.TileAlchemicalSynthesisCore
import net.minecraft.block.{Block, ITileEntityProvider}
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.world.World

class BlockAlchemicalSynthesisCore extends Block(Material.iron) with ITileEntityProvider {
  this.setHardness(5.0F)
  this.setResistance(10.0F)
  this.setStepSound(Block.soundTypeMetal)
  this.setCreativeTab(CreativeTabs.tabRedstone)
  this.setBlockTextureName("minecraft:furnace_front_off")

  override def createNewTileEntity(world: World, metadata: Int): TileEntity =
    new TileAlchemicalSynthesisCore

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
    if (!world.isRemote) {
      world.getTileEntity(x, y, z) match {
        case core: TileAlchemicalSynthesisCore =>
          val translationKey =
            if (core.checkStructureNow()) "chat.bigsteppa.alchemicalSynthesis.formed"
            else "chat.bigsteppa.alchemicalSynthesis.incomplete"
          player.addChatMessage(new ChatComponentTranslation(translationKey))
        case _ =>
      }
    }
    true
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, neighbor: Block): Unit = {
    world.getTileEntity(x, y, z) match {
      case core: TileAlchemicalSynthesisCore => core.scheduleStructureCheck()
      case _                                  =>
    }
  }
}
